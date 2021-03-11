const User = require('../models/User')
const { TRELLO_CLIENT, TRELLO_SECRET, AZURE_APP, AZURE_SECRET, GITHUB_CLIENT, GITHUB_SECRET, GITHUB_STATE, DISCORD_CLIENT, DISCORD_SECRET, LINKEDIN_CLIENT, LINKEDIN_SECRET, LINKEDIN_STATE, GITLAB_CLIENT, GITLAB_SECRET, GITLAB_VERIFIER } = require('../Config');
const msal = require('@azure/msal-node');
const axios = require('axios')
const qs = require('qs')
const CryptoJS = require('crypto-js')

/* GITHUB */

exports.GetGithub = (req, res, next) => {
    var url = `https://github.com/login/oauth/authorize?client_id=${GITHUB_CLIENT}&scope=user%20repo&state=${GITHUB_STATE}`
    return res.status(200).json(url)
}

exports.LoginGithub = async(req, res, next) => {
    if (!req.body.code)
        return res.status(401).json({ err: "params missing" })
    var data = await axios.post('https://github.com/login/oauth/access_token', {
        client_id: GITHUB_CLIENT,
        client_secret: GITHUB_SECRET,
        code: req.body.code,
        state: GITHUB_STATE
    })
    var token = data.data.split('=')[1].split('&')[0]
    User.findOneAndUpdate({ _id: res.locals.user }, {
            $set: {
                github_token: token
            }
        }).then(res => console.log(res))
        .catch(err => console.log(err))
    return res.status(200).json("ok")

}

/* TRELLO */

exports.GetTrello = (req, res, next) => {
    return res.status(200).json("https://trello.com/1/authorize?expiration=never&scope=read,write,account&response_type=token&name=Server%20Token&key=b6a464cd39cb11c23408b0e63b918c82&return_url=http://localhost:8081/trelloVerify")
}

exports.LoginTrello = async (req, res, next) => {
    if (!req.body.code)
        return res.status(401).json({ err: "params missing" })
    await User.findOneAndUpdate({ _id: res.locals.user }, {
        $set: {
            trello_token: req.body.code
        }
    })
    return res.status(200).json("ok")
}

/* EPITECH */

exports.LoginEpitech = async(req, res, next) => {
    if (!req.body.email || !req.body.autolog)
        return res.status(401).json({ err: "params missing" })
    const user = await User.findOneAndUpdate({ _id: res.locals.user }, {
        $set: {
            epitech: {
                autologin: req.body.autolog,
                email: req.body.email
            }
        }
    })
    return res.status(201).json(user)
}


/* GITLAB */


exports.GetGitLab = async(req, res, next) => {
    var chall = CryptoJS.SHA256(GITLAB_VERIFIER).toString(CryptoJS.enc.Base64).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_')
    chall = encodeURI(chall)
    const url = `https://gitlab.com/oauth/authorize?client_id=${GITLAB_CLIENT}&redirect_uri=http%3A%2F%2Flocalhost%3A8081%2FgitlabVerify&response_type=code&state=STATE&scope=api%20read_repository%20profile&code_challenge=${chall}&code_challenge_method=S256`
    return res.status(200).json(url);
}

exports.LoginGitLab = async(req, res, next) => {
    if (!req.body.code)
        return res.status(401).json({ err: "code param is missing" })
    const data = await axios({
        method: 'post',
        url: "https://gitlab.com/oauth/token",
        data: qs.stringify({
            'client_id': GITLAB_CLIENT,
            'client_secret': GITLAB_SECRET,
            'code': req.body.code,
            'grant_type': 'authorization_code',
            'code_verifier': GITLAB_VERIFIER,
            'redirect_uri': 'http://localhost:8081/gitlabVerify'
        })
    }).then(res => {
        return res.data
    }).catch(err => console.log(err))
    console.log(data)
    const user = await User.findByIdAndUpdate({ _id: res.locals.user }, {
        $set: {
            gitlab_token: data.access_token
        }
    })
    return res.status(201).json("OK")
}