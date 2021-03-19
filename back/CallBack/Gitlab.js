const { default: axios } = require("axios")

// check la différence sur la doc entre data.project_id/github (proprietaire/nom) et data.project_id/gitlab (int)
// faire un controller gitlab

exports.ParseCondition = async(data, user, lastcheck) => {
    if (data.event === 1)
        return checkProject(data, user, lastcheck)
}

exports.ParseAction = async(data, user) => {
    if (CreateBranch(data, user)) {
        return CreateBranch(data, user)
    }
}

// Trigger
async function checkProject(data, user, lastcheck) {
    const gitluser = await axios.get("https://www.gitlab.com/api/v4/user?access_token=" + user.gitlab_token)
        .then(res => { return res.data })
        .catch(err => console.log(err))
    const dat = await axios.get("https://www.gitlab.com/api/v4/users/" + gitluser.id + "/projects?access_token=" + user.gitlab_token)
        .then(res => { return res.data })
        .catch(err => console.log(err))
    var check = false
    dat.forEach(d => {
        var cmp = new Date(d.created_at)
        if (cmp.getTime() > lastcheck.getTime())
            check = true
    })
    return check
}

// Action
//normalement fct
async function CreateBranch(data, user) {
    var value = JSON.parse(data.action)
    await axios.post("https://www.gitlab.com/api/v4/projects/" + value.id + "/repository/branches?access_token=" + user.gitlab_token, {
        branch: value.name,
        ref: value.sha
    }).then(res => console.log(res.data))
    .catch(err => console.log(err))
}