const Axios = require('axios')
const mimeMessage = require('mime-message')
const { GOOGLE_CLIENT, GOOGLE_SECRET, FRONT_ADDRESS, CONTACT_MAIL, CONTACT_PWD } = require('../Config');

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
    const tok = await axios.post("https://oauth2.googleapis.com/token", {
        client_id: GOOGLE_CLIENT,
        client_secret: GOOGLE_SECRET,
        grant_type: "refresh_token",
        refresh_token: user.google_token
    }).then(res => {return res.data.access_token})
    .catch(err => console.log(err))
    var dat = JSON.parse(data.data)
    console.log(dat)
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
    await Axios.post("https://gmail.googleapis.com/gmail/v1/users/me/messages/send?access_token=" + tok, {
            raw: bs64
        }).then(res => console.log(res))
        .catch(err => console.log(err))
}

async function CheckKeyWord(data, user, last_check) {
    const tok = await axios.post("https://oauth2.googleapis.com/token", {
        client_id: GOOGLE_CLIENT,
        client_secret: GOOGLE_SECRET,
        grant_type: "refresh_token",
        refresh_token: user.google_token
    }).then(res => {return res.data.access_token})
    .catch(err => console.log(err))
    var tmp = new Date(last_check)
    tmp.setHours(0, 0, 0, 0)
    console.log(user.google_token)
    const date = await Axios.get("https://www.googleapis.com/gmail/v1/users/me/messages?q=sent%20after%3A" + tmp.getTime() / 1000 + "&alt=json&access_token=" + tok)
        .then(res => { return res.data })
        .catch(err => console.log(err))
    var message = []
    console.log(date)
    const prom = Promise.all(date.messages.map(async d => {
        const dat = await Axios.get("https://www.googleapis.com/gmail/v1/users/me/messages/" + d.id + "?alt=json&access_token=" + tok)
        return dat;
    })).then(res => {
        message.push(res)
    }).catch(err => console.log(err))
    var check = false;
    await Promise.resolve(prom)
        .then(() => {
            message[0].forEach(m => {
                m.data.payload.headers.forEach(h => {
                    if (h.name === "Subject")
                        if (h.value.includes(data.data))
                            check = true;
                })
            })
        })
    return check;
}

async function CheckRecipe(data, user, last_check) {
    const tok = await axios.post("https://oauth2.googleapis.com/token", {
        client_id: GOOGLE_CLIENT,
        client_secret: GOOGLE_SECRET,
        grant_type: "refresh_token",
        refresh_token: user.google_token
    }).then(res => {return res.data.access_token})
    .catch(err => console.log(err))
    var tmp = new Date(last_check)
    tmp.setHours(0, 0, 0, 0)
    console.log(user.google_token)
    const date = await Axios.get("https://www.googleapis.com/gmail/v1/users/me/messages?q=sent%20after%3A" + tmp.getTime() / 1000 + "&alt=json&access_token=" + tok)
        .then(res => { return res.data })
        .catch(err => console.log(err))
    var message = []
    console.log(date)
    const prom = Promise.all(date.messages.map(async d => {
        const dat = await Axios.get("https://www.googleapis.com/gmail/v1/users/me/messages/" + d.id + "?alt=json&access_token=" + tok)
        return dat;
    })).then(res => {
        message.push(res)
    }).catch(err => console.log(err))
    var check = false;
    await Promise.resolve(prom)
        .then(() => {
            message[0].forEach(m => {
                m.data.payload.headers.forEach(h => {
                    if (h.name === "From") {
                        if (h.value.includes(data.data))
                            check = true;
                    }
                })
            })
        })
    return check;
}

async function CheckTime(data, user, last_check) {
    const tok = await axios.post("https://oauth2.googleapis.com/token", {
        client_id: GOOGLE_CLIENT,
        client_secret: GOOGLE_SECRET,
        grant_type: "refresh_token",
        refresh_token: user.google_token
    }).then(res => {return res.data.access_token})
    .catch(err => console.log(err))
    var tmp = new Date(last_check)
    tmp.setHours(0, 0, 0, 0)
    console.log(user.google_token)
    const date = await Axios.get("https://www.googleapis.com/gmail/v1/users/me/messages?q=sent%20after%3A" + tmp.getTime() / 1000 + "&alt=json&access_token=" + tok)
        .then(res => { return res.data })
        .catch(err => console.log(err))
    var message = []
    console.log(date)
    const prom = Promise.all(date.messages.map(async d => {
        const dat = await Axios.get("https://www.googleapis.com/gmail/v1/users/me/messages/" + d.id + "?alt=json&access_token=" + tok)
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
                m.data.payload.headers.forEach(h => {
                    if (h.name === "Date") {
                        var test = new Date(h.value)
                        test.setDate(1)
                        var args = data.data.split(' ')
                        var times = args[1].split(':')
                        dateCheck.setHours(times[0], times[1])
                        console.log(args)
                        if (args[0] === "Before")
                            check = test.getTime() < dateCheck.getTime()
                        else if (args[0] === "After")
                            check = test.getTime() > dateCheck.getTime()
                    }
                })
            })
        })
    return check;
}