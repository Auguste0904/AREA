const axios = require('axios')
const { GOOGLE_CLIENT, GOOGLE_SECRET, FRONT_ADDRESS, CONTACT_MAIL, CONTACT_PWD } = require('../Config');
const checkGoogle = require('../Utils/CheckToken')

exports.ParseAction = async(data, user) => {
    if (data.event === 1)
        ShareFile(data, user)
}

async function ShareFile(data, user) {
    user = await checkGoogle.CheckGoogle(user)
    var dat = JSON.parse(data.action)
    var email_list = dat.email.split(";")
    var file_list = dat.Files.split(";")
    file_list.forEach(async f => {
        email_list.forEach(async u => {
            const result = await axios.post("https://www.googleapis.com/drive/v3/files/" + f + "/permissions", {
                    role: "reader",
                    type: "user",
                    emailAddress: u
                }, {
                    headers: {
                        Authorization: `Bearer ${user.google_token}`
                    }
                })
                .then(res => { return res.data })
                .catch(err => console.log(err))
        })
    })
}