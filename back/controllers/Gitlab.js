const User = require('../models/User')
const axios = require('axios')

exports.GetUserProjects = async(req, res, next) => {
    const user = await User.findOne({_id: res.locals.user})
    if (!user.gitlab_token)
        return res.status(403).json({err: "No account Linked"})
    console.log(user.gitlab_token)
    const gitluser = await axios.get("https://www.gitlab.com/api/v4/user?access_token=" + user.gitlab_token)
    .then(res => {return res.data})
    .catch(err => console.log(err))
    const data = await axios.get("https://www.gitlab.com/api/v4/users/" + gitluser.id + "/projects?access_token=" + user.gitlab_token)
    .then(res => {return res.data})
    .catch(err => console.log(err))
    var toSend = []

    data.forEach(d => {
        toSend.push({
            name: d.name,
            id: d.id
        })
    })
    return res.status(200).json(toSend)
}

exports.GetProjectBranch = async(req, res, next) => {
    const user = await User.findOne({_id: res.locals.user})
    if (!user.gitlab_token)
        return res.status(403).json({err: "No account linked"})
    if (!req.params.id)
        return res.status.json({err: "Unspecified project id"})
    var url = "https://www.gitlab.com/api/v4/projects/" + req.params.id + "/repository/branches?access_token=" + user.gitlab_token
    var toSend = []
    var data = await axios.get(url)
    .then(result => {return result})
    .catch(err => {
        res.status(503).json({err: err})
    })
    data = data.data
    if (data.status === 503)
        return res.status(503).json({msg: "Gitlab Internal Error"})
    if (data) {
        data.forEach(d => {
            toSend.push({
                name: d.name
            })
        })
    }
    return res.status(200).json(toSend)
}