const User = require('../models/User');
const axios = require('axios')
const { GOOGLE_CLIENT, GOOGLE_SECRET } = require('../Config');

exports.CheckGoogle = async(user) => {
    if (!user.google_token || !user.google_refresh)
        return user;
    const info = await axios.get("https://www.googleapis.com/oauth2/v1/userinfo?alt=json", {
        headers: {
            authorization: `Bearer ${user.google_token}`
        }
    }).then(res => {return res})
    .catch(() => {return null})
    if (!info || info.data.error) {
        const tok = await axios.post("https://oauth2.googleapis.com/token", {
            client_id: GOOGLE_CLIENT,
            client_secret: GOOGLE_SECRET,
            grant_type: "refresh_token",
            refresh_token: user.google_refresh
        }).then(res => {return res.data.access_token})
        .catch(err => console.log(err))
        await User.updateOne({_id: user.id}, {
            $set: {
                google_token: tok
            }
        }).catch(err => console.log(err))
    }
    else if (info && info.data && info.data.id)
        return user
    user = await User.findOne({_id: user.id})
    return user
}