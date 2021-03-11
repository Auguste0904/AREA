const axios = require('axios')
const { GOOGLE_CLIENT, GOOGLE_SECRET, FRONT_ADDRESS, CONTACT_MAIL, CONTACT_PWD } = require('../Config');

exports.ParseAction = async(data, user) => {
    if (data.event === 1)
        ShareFile(data, user)
}

async function ShareFile(data, user) {
    const tok = await axios.post("https://oauth2.googleapis.com/token", {
        client_id: GOOGLE_CLIENT,
        client_secret: GOOGLE_SECRET,
        grant_type: "refresh_token",
        refresh_token: user.google_token
    }).then(res => {return res.data.access_token})
    .catch(err => console.log(err))
    var dat= JSON.parse(data.data)
    var email_list = dat.Email.split(";")
    var file_list = dat.Files.split(";")
    console.log(email_list)
    console.log(user.google_token)
    file_list.forEach(async f => {
        email_list.forEach(async u => {
            const result = await axios.post("https://www.googleapis.com/drive/v3/files/" + f +"/permissions", {
                role: "reader",
                type: "user",
                emailAddress: u
            }, {
                headers : {
                    Authorization: `Bearer ${tok}`
                }
            })
            .then(res => {return res.data})
            .catch(err => console.log(err))
            console.log(result)
        })
    })
}