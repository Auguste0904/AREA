const Axios = require('axios');
const { TRELLO_CLIENT, TRELLO_SECRET } = require('../Config')

exports.ParseCondition = async(data, user, last_check) => {
    if (data.event === 1)
        return CheckActivites(data, user, last_check)
}

exports.ParseAction = async(data, user) => {
    if (data.event === 1)
        CreateList(data, user)
}

async function CheckActivites(data, user, last_check) {
    const list = await Axios.get("https://api.trello.com/1/lists/" + data.data + "/actions?key=" + TRELLO_CLIENT + "&token=" + user.trello_token)
    .then(res => {return res.data})
    .catch(err => console.log(err))
    var check = false;
    list.forEach(l => {
        var cmp = new Date(l.date)
        if (cmp.getTime() > last_check.getTime())
            check = true;
    })
    return check;
}

async function CreateList(data, user) {
    var args = data.data.split(" ")
    var id = args.shift()
    var name = args.join(" ")
    console.log(id, name)
    await Axios.post("https://api.trello.com/1/lists?key=" + TRELLO_CLIENT + "&token=" + user.trello_token + "&name=" + name + "&idBoard=" + id)
    .then(res => console.log(res))
    .catch(err => console.log(err))
}