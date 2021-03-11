const User = require('../models/User')
const axios = require('axios')
const { GOOGLE_CLIENT, GOOGLE_SECRET, FRONT_ADDRESS, CONTACT_MAIL, CONTACT_PWD } = require('../Config');

exports.GetDriveFolder = async(req, res, next) => {
    const user = await User.findOne({_id: res.locals.user});
    console.log(user)
    if (!user.google_token)
        return res.status(403).json({err: "No account Linked"})
    const tok = await axios.post("https://oauth2.googleapis.com/token", {
        client_id: GOOGLE_CLIENT,
        client_secret: GOOGLE_SECRET,
        grant_type: "refresh_token",
        refresh_token: user.google_token
    }).then(res => {return res.data.access_token})
    .catch(err => console.log(err))
    const data = await axios.get("https://www.googleapis.com/drive/v2/files", {
        headers : {
            Authorization: `Bearer ${tok}`
        }
    })
    .then(res => {return res.data})
    .catch(err => console.log(err))
    console.log(data)
    return res.status(200).json(data)
}