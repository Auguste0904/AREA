const User = require('../models/User')
const axios = require('axios')

exports.GetUserProjects = async(req, res, next) => {
    const user = await User.findOne({_id: res.locals.user})
    if (!user.github_token)
        return res.status(403).json({err: "No account Linked"})
    console.log(user.github_token)
    const data = await axios.get("https://api.github.com/user/repos", {
        headers: {
            authorization: `Bearer ${user.github_token}`,
            accept: "application/vnd.github.v3+json"
        }
    })
    .then(res => {return res.data})
    .catch(err => console.log(err))
    var toSend = []
    data.forEach(d => {
        toSend.push({
            name: d.full_name,
        })
    })
    return res.status(200).json(toSend)
}

exports.GetProjectBranch = async(req, res, next) => {
    const user = await User.findOne({_id: res.locals.user})
    if (!user.github_token)
        return res.status(403).json({err: "No account linked"})
    if (!req.params.owner || !req.params.project)
        return res.status.json({err: "Unspecified project id"})
    var url = "https://api.github.com/repos/" + req.params.owner + "/" + req.params.project + "/branches"
    console.log(url)
    var toSend = []
    const data = await axios.get(url, {
        headers: {
            authorization: `Bearer ${user.github_token}`,
        }
    }).then(result => {return result.data})
    .catch(err => {
        res.status(503).json({err: err})
    })
    if (data) {
        data.forEach(d => {
            toSend.push(d.name)
        })
    }
    return res.status(200).json(toSend)
}