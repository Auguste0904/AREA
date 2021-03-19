const axios = require('axios');


exports.ParseCondition = async(data, user, last_check) => {
    var check_time = new Date(last_check);
    var cmp = new Date();
    check_time.setHours(0, 0, 0, 0);
    cmp.setHours(0, 0, 0, 0);
    if (check_time.getTime() === cmp.getTime())
        return false;
    if (data.event === 1)
        return CheckMeeting(data, user, last_check)
    else if (data.event === 2)
        return CheckAlert(data, user, last_check)
    else if (data.event === 3)
        return CheckMissing(data, user, last_check)
    return false
}

async function CheckMeeting(data, user, last_check) {
    const dat = await axios.get(user.epitech.autologin+"/user/notification/coming?format=json")
    .then(res => {return res.data})
    .catch(err => console.log(err))
    var size = 0
    for (item in dat) {
        size++
    }
    return size > 0
}


async function CheckAlert(data, user, last_check) {
    const dat = await axios.get(user.epitech.autologin+"/user/notification/alert?format=json")
    .then(res => {return res.data})
    .catch(err => console.log(err))
    return dat.length > 0
}


async function CheckMissing(data, user, last_check) {
    const dat = await axios.get(user.epitech.autologin+"/user/notification/missed?format=json")
    .then(res => {return res.data})
    .catch(err => console.log(err))
    return dat.recents.length > 0
}