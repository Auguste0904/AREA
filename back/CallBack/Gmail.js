const axios = require('axios')
const mimeMessage = require('mime-message')
const { GOOGLE_CLIENT, GOOGLE_SECRET, FRONT_ADDRESS, CONTACT_MAIL, CONTACT_PWD } = require('../Config');
const checkGoogle = require('../Utils/CheckToken')

exports.ParseCondition = async(data, user, last_check) => {
    if (data.event === 1)
        return CheckKeyWord(data, user, last_check)
    else if (data.event === 2)
        return CheckRecipe(data, user, last_check)
    else if (data.event === 3)
        return CheckTime(data, user, last_check)
    return false;
}

exports.ParseAction = async(data, user) => {
    if (data.event === 1)
        return SendMail(data, user)
}

async function SendMail(data, user) {
    user = await checkGoogle.CheckGoogle(user)
    var dat = JSON.parse(data.action)
    const message = {
        type: "text/html",
        encoding: "UTF-8",
        from: "me",
        to: [
            dat.email
        ],
        cc: [],
        bcc: [],
        replyTo: [],
        date: new Date(),
        subject: dat.object,
        body: dat.body
    }
    const msg = mimeMessage.createMimeMessage(message)
    const bs64 = msg.toBase64SafeString()
    await axios.post("https://gmail.googleapis.com/gmail/v1/users/me/messages/send?access_token=" + user.google_token, {
            raw: bs64
        }).then(res => console.log("email sended"))
        .catch(err => console.log(err))
}

async function CheckKeyWord(data, user, last_check) {
    user = await checkGoogle.CheckGoogle(user)
    var tmp = new Date(last_check)
    const date = await axios.get("https://www.googleapis.com/gmail/v1/users/me/messages?alt=json&access_token=" + user.google_token)
        .then(res => { return res.data })
        .catch(err => console.log(err))
    var message = []
    if (!date || !date.messages)
        return false;
    var recv = []
    for (var i = 0; i < 10; i++)
        recv.push(date.messages[i])
    const prom = Promise.all(recv.map(async d => {
        const dat = await axios.get("https://www.googleapis.com/gmail/v1/users/me/messages/" + d.id + "?alt=json&access_token=" + user.google_token)
        return dat;
    })).then(res => {
        message.push(res)
    }).catch(err => console.log(err))
    var check = false;
    await Promise.resolve(prom)
        .then(() => {
            message[0].forEach(m => {
                var isAfter = false
                m.data.payload.headers.forEach(h => {
                    if (h.name === "Date") {
                        var tmpd = new Date(h.value)
                        console.log(tmpd, " ", last_check)
                        if (tmpd.getTime() > last_check.getTime())
                            isAfter = true
                    }
                })
                m.data.payload.headers.forEach(h => {
                    if (isAfter === true && h.name === "Subject") {
                        console.log(h)
                        if (h.value.includes(data.condition))
                            check = true;
                    }
                })
            })
        })
    return check;
}

async function CheckRecipe(data, user, last_check) {
    user = await checkGoogle.CheckGoogle(user)
    var tmp = new Date(last_check)
    const date = await axios.get("https://www.googleapis.com/gmail/v1/users/me/messages?alt=json&access_token=" + user.google_token)
        .then(res => { return res.data })
        .catch(err => console.log(err))
    var message = []
    if (!date || !date.messages)
        return false;
    var recv = []
    for (var i = 0; i < 10; i++)
        recv.push(date.messages[i])
    const prom = Promise.all(recv.map(async d => {
        const dat = await axios.get("https://www.googleapis.com/gmail/v1/users/me/messages/" + d.id + "?alt=json&access_token=" + user.google_token)
        return dat;
    })).then(res => {
        message.push(res)
    }).catch(err => console.log(err))
    var check = false;
    await Promise.resolve(prom)
        .then(() => {
            message[0].forEach(m => {
                var isAfter = false
                m.data.payload.headers.forEach(h => {
                    if (h.name === "Date") {
                        var tmpd = new Date(h.value)
                        console.log(tmpd, " ", last_check)
                        if (tmpd.getTime() > last_check.getTime())
                            isAfter = true
                    }
                })
                m.data.payload.headers.forEach(h => {
                    if (isAfter === true && h.name === "From") {
                        console.log(h)
                        if (h.value.includes(data.condition))
                            check = true;
                    }
                })
            })
        })
    return check;
}

async function CheckTime(data, user, last_check) {
    user = await checkGoogle.CheckGoogle(user)
    var tmp = new Date(last_check)
    const date = await axios.get("https://www.googleapis.com/gmail/v1/users/me/messages?alt=json&access_token=" + user.google_token)
        .then(res => { return res.data })
        .catch(err => console.log(err))
    var message = []
    if (!date || !date.messages)
    return false;
    var recv = []
    for (var i = 0; i < 10; i++)
    recv.push(date.messages[i])
    const prom = Promise.all(recv.map(async d => {
        const dat = await axios.get("https://www.googleapis.com/gmail/v1/users/me/messages/" + d.id + "?alt=json&access_token=" + user.google_token)
        return dat;
    })).then(res => {
        message.push(res)
    }).catch(err => console.log(err))
    var check = false;
    var dateCheck = new Date()
    dateCheck.setDate(1)
    await Promise.resolve(prom)
        .then(() => {
            message[0].forEach(m => {
                var isAfter = false
                m.data.payload.headers.forEach(h => {
                    if (h.name === "Date") {
                        var tmpd = new Date(h.value)
                        if (tmpd.getTime() > last_check.getTime())
                            isAfter = true
                    }
                })
                m.data.payload.headers.forEach(h => {
                    if (isAfter && h.name === "Date") {
                        var test = new Date(h.value)
                        test.setDate(1)
                        var args = data.condition.split(' ')
                        var times = args[1].split(':')
                        dateCheck.setHours(times[0], times[1])
                        if (args[0] === "Before") {
                            if (!check)
                                check = test.getTime() < dateCheck.getTime()
                        } else if (args[0] === "After") {
                            if (!check)
                                check = test.getTime() > dateCheck.getTime()
                        }
                    }
                })
            })
        })
    return check;
}