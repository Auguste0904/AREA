const User = require('../models/User')
const axios = require('axios')
const checkGoogle = require('../Utils/CheckToken')
const { GOOGLE_CLIENT, GOOGLE_SECRET, FRONT_ADDRESS, CONTACT_MAIL, CONTACT_PWD } = require('../Config');

exports.GetDriveFolder = async(req, res, next) => {
    var user = await User.findOne({_id: res.locals.user});
    if (!user.google_token)
        return res.status(403).json({err: "No account Linked"})
    user = await checkGoogle.CheckGoogle(user)
    const data = await axios.get("https://www.googleapis.com/drive/v2/files", {
        headers : {
            Authorization: `Bearer ${user.google_token}`
        }
    })
    .then(res => {return res.data})
    .catch(err => console.log(err))
    return res.status(200).json(data)
}