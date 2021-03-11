const User = require('../models/User')
const axios = require('axios')
const { TRELLO_CLIENT, TRELLO_SECRET } = require('../Config')

exports.GetBoard = async(req, res, next) => {
    const user = await User.findOne({ _id: res.locals.user })
    if (!user.trello_token)
        return res.status(403).json({ err: "No trello account linked" });
    // TODO
    console.log("token trello:", user.trello_token);
    const data = await axios.get("https://api.trello.com/1/members/me/boards?key=" + TRELLO_CLIENT + "&token=" + user.trello_token)
    .then(res => {return res.data})
    .catch(err => console.log(err))
    var toSend = []
    data.forEach(d => {
        toSend.push({
            id: d.id,
            name: d.name
        })
    })
    return res.status(200).json(toSend)
}

exports.GetList = async(req, res, next) => {
    if (!req.params.id)
        return res.status(400).json("params missing")
    const user = await User.findOne({ _id: res.locals.user })
    if (!user.trello_token)
        return res.status(403).json({ err: "No trello account linked" });
    const data = await axios.get("https://api.trello.com/1/board/"+req.params.id+"/lists?key=" + TRELLO_CLIENT + "&token=" + user.trello_token)
    .then(res => {return res.data})
    .catch(err => console.log(err))
    var toSend = []
    data.forEach(d => {
        toSend.push({
            id: d.id,
            name: d.name
        })
    })
    return res.status(200).json(toSend)
}