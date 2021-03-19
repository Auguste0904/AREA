const User = require('../models/User');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const nodemailer = require('nodemailer');
const { google } = require('googleapis');
const axios = require('axios');
const { GOOGLE_CLIENT, GOOGLE_SECRET, FRONT_ADDRESS, CONTACT_MAIL, CONTACT_PWD } = require('../Config');
const googleCheck = require('../Utils/CheckToken')

const scopes = [
    "https://www.googleapis.com/auth/userinfo.profile",
    "https://www.googleapis.com/auth/userinfo.email",
    "https://www.googleapis.com/auth/gmail.send",
    "https://mail.google.com/",
    "https://www.googleapis.com/auth/drive"
];

const transfert = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: CONTACT_MAIL,
        pass: CONTACT_PWD
    }
})

const Oauth2Client = new google.auth.OAuth2(
    GOOGLE_CLIENT,
    GOOGLE_SECRET,
    "http://localhost:8081/googleVerify",
);

google.options({
    auth: Oauth2Client
});



/* /api/user/getGoogle
 * Params: 
 * Return google URL
 */
exports.getConnexionUrl = (req, res, next) => {
    const uri = Oauth2Client.generateAuthUrl({
        access_type: 'offline',
        scope: scopes
    });
    return res.status(200).json({ url: uri });
};

/** /api/user/authgoogle
 *  Params body : code
 *  return : User & Token
 */

exports.LoginGoogle = async(req, res, next) => {
    if (!req.body.code)
        return res.status(401).json({ error: "params missing" });
    const info = await getgoogleinfo({ code: req.body.code });
    if (!info)
        return res.status(401).json({ error: "Bad Code" });
    console.log(info.token)
    const user = await User.findOneAndUpdate({ _id: res.locals.user }, {
        $set: {
            google_refresh: info.token.refresh_token,
            google_token: info.token.access_token
        }
    })
    return res.status(201).json(user);
}

exports.getUserInfo = async(req, res, next) => {
    if (!req.body.code)
        return res.status(401).json({ error: "params missing" });
    const info = await getgoogleinfo({ code: req.body.code });
    if (!info.user)
        return res.status(401).json({ error: "Bad Code" });
    var check = await User.findOne({ email: info.user.email })
    console.log(info.token)
    if (!check) {
        check = new User({
            email: info.user.email,
            firstname: info.user.given_name,
            lastname: info.user.family_name,
            active: true,
            google_token: info.token.access_token,
            google_refresh: info.token.refresh_token
        });
        check.save()
    }
    return res.status(201).json({
        userId: check._id,
        token: jwt.sign({ userId: check._id },
            'AREATOKEN', { expiresIn: '24h' }
        )
    });
};


async function getgoogleinfo({ code }) {
    code = decodeURIComponent(code);
    try {
        const { tokens } = await Oauth2Client.getToken(code);
        Oauth2Client.setCredentials(tokens);
        const us = await axios.get(`https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=${tokens.access_token}`, {
                headers: {
                    Authorization: `Bearer ${tokens.id_token}`,
                },
            }, )
            .then(res => res.data)
            .catch(error => {
                throw new Error(error.message);
            });
        return { user: us, token: tokens };
    } catch (e) {
        return;
    }
}

/** /api/user/signup
 *  Params Body :
 *      - email
 *      - password
 *      - firstname
 *      - lastname
 *  Return User & send mail
 */

exports.signup = async(req, res, next) => {
    if (!req.body.email || !req.body.password)
        return res.status(403).json({ error: "Params Missing" });
    const check = await User.findOne({ email: req.body.email })
    if (check)
        return res.status(403).json({ error: "Email Taken" })
    bcrypt.hash(req.body.password, 10)
        .then(hash => {
            const client = new User({
                firstname: req.body.firstname,
                lastname: req.body.lastname,
                email: req.body.email,
                password: hash
            });
            var code = Math.round(Math.random() * (9999 - 1111) + 1111)
            const mailOpt = {
                from: CONTACT_MAIL,
                to: req.body.email,
                subject: "Confirm Your Email",
                html: `Please, click on the <a href="http://localhost:8081/api/user/verify/${code}">link<a/> to confirm your email.
                <br>If the link is invalid,
                you can copy paste this link in your brower : localhost:8081/api/user/verify/${code}`
            }
            transfert.sendMail(mailOpt)
            client.save()
                .then(() => res.status(201).json({ client }))
                .catch(err => res.status(500).json({ err }))
        }).catch(err => res.status(500).json({ err }));
}

/** /api/user/verify
 *  Params Query : token
 *  Return "ok"
 */

exports.verify = async(req, res, next) => {
    token = req.params.token
    const user = await User.updateOne({ code: token }, {
            $set: {
                active: true
            },
            $unset: {
                code: 0
            }
        })
        .then(() => res.status(201).json({ status: "ok" }))
        .catch(() => res.status(403).json({ err: "invalid code" }))
}

/** /api/user/login
 *  Params Body : 
 *      - email
 *      - password
 *  return User & token
 */

exports.login = async(req, res, next) => {
    User.findOne({ email: req.body.email })
        .then(user => {
            if (!user)
                return res.status(403).json({ error: "user not found" });
            bcrypt.compare(req.body.password, user.password)
                .then(valid => {
                    if (!valid)
                        return res.status(401).json("unauthorized");
                    res.status(200).json({
                        userId: user._id,
                        token: jwt.sign({ userId: user._id },
                            "AREATOKEN", { expiresIn: '24h' }
                        )
                    });
                }).catch(err => res.status(501).json({ err }))
        }).catch(err => {
            res.status(403).json({ error: "user not found" })
        });
}

/** /api/user/me
 *  Params:
 *  Return user
 * 
 * 
 */

exports.me = async(req, res, next) => {
    const user = await (await User.findOne({ _id: res.locals.user }).populate("services").exec())
    googleCheck.CheckGoogle(user)
    if (!user)
        return res.status(500).json({ error: "no user found" })
    return res.status(200).json(user)
}

/**  /api/user/update
 *   Params Body :
 *      - firstname
 *      - lastname
 *    Return User
 * 
 */

exports.update = async(req, res, next) => {
    if (!req.body.firstname || !req.body.lastname)
        return res.status(400).json({ err: "params missing" });
    var user = await User.findOne({ _id: res.locals.user })
    if (!user)
        return res.status(403).json({ error: "no user found" })
    user.firstname = req.body.firstname;
    user.lastname = req.body.lastname;
    user.save();
    return res.status(201).json({ user });
}

/** /api/user/change
 *  Params Body :
 *      - last : last password
 *      - new  : new password
 *  Return "ok"
 * 
 */

exports.changePwd = async(req, res, next) => {
    if (!req.body.last || !req.body.new)
        return res.status(500).json({ err: "params missing" })
    var user = await User.findOne({ _id: res.locals.user });
    bcrypt.compare(req.body.last, user.password)
        .then(valid => {
            if (!valid)
                return res.status(401).json({ error: "invalid password" });
            bcrypt.hash(req.body.new, 10)
                .then(hash => {
                    user.password = hash;
                    user.save();
                })
            res.status(200).json("ok")
        }).catch(err => res.status(501).json({ err }))
}

/** /api/user/delete
 *  Params :
 *  Return "ok"
 */

exports.deleteMe = async(req, res, next) => {
    const user = await User.findOne({ _id: res.locals.user });
    if (!user)
        return res.statu(400).json({ err: "no user" })
            // DELETE ALL PROCCESSES HERE
    User.deleteOne({ _id: res.locals.user })
        .catch(err => console.log(err))
    return res.status(201).json("ok")
}
