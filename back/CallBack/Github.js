const { default: axios } = require("axios");
const { AZURE_SECRETID } = require("../Config");
const qs = require('qs')
const controller = require('../controllers/Github')

exports.ParseCondition = async(data, user, last_check) => {
    if (data.event === 1)
        return CheckProject(data, user, last_check)
    else if (data.event === 2)
        return CheckCommit(data, user, last_check)
    else if (data.event === 3)
        return CheckPull(data, user, last_check)
    return false;
}

exports.ParseAction = async(data, user) => {
    if (data.event === 1) {
        return PullRequest(data, user)
    } else if (data.event === 2) {
        return CreateBranch(data, user)
    }
}

//Trigger

async function CheckProject(data, user, last_check) {
    return axios.get("https://api.github.com/user/repos", {
            headers: {
                authorization: `Bearer ${user.github_token}`,
                accept: "application/vnd.github.v3+json"
            }
        })
        .then(res => {
            console.log("result github cron")
            var check = false
            res.data.forEach(d => {
                console.log(d.created_at)
                var cmp = new Date(d.created_at)
                console.log(last_check)
                if (cmp.getTime() > last_check)
                    check = true
            })
            return check;
        }).catch(err => {
            console.log(err)
            return false
        })
}

//"https://api.github.com/repos/"+ data.project_id +'/branches/'+Branch
// recupere l'ensemble des commits
// comment on boucle sur les commits pour récuperer les dates de chacun
async function CheckCommit(data, user, last) {
    const datecheck = new Date(last)
    return axios.get("https://api.github.com/repos/" + data.data + "/commits?since=" + datecheck.toISOString(), {
        headers: {
            authorization: `Bearer ${user.github_token}`,
            accept: "application/vnd.github.v3+json"
        }
    }).then(res => {
        var check = false
        if (res.data.length > 0)
            return true;
        return false;
    }).catch(err => {
        console.log(err)
        return false
    })
}

//"https://api.github.com/repos/"+ data.project_id+ "/pulls"
// headers: {
//     authorization: `Bearer ${user.github_token}`,
//     accept: "application/vnd.github.v3+json"
// },
function CheckPull(data, user, last) {
    return axios.get("https://api.github.com/repos/" + data.data + "/pulls", {
            headers: {
                authorization: `Bearer ${user.github_token}`,
                accept: "application/vnd.github.v3+json"
            }
        })
        .then(res => {
            var check = false
            res.data.forEach(d => {
                var cmp = new Date(d.created_at)
                if (cmp.getTime() > last.getTime())
                    check = true;
            })
            return check
        }).catch(err => {
            console.log(err)
            return false
        })
}

/// ACTION
// Check for user owner
//'https://api.github.com/repos/' + data.project + '/pulls'
// headers: {
//     authorization: `Bearer ${user.github_token}`,
//     accept: "application/vnd.github.v3+json"
// },
// data: qs.stringify({
//     'head': head,
//     'base': base,
//     'title': data.title,
//     'body': data.body
// })
async function PullRequest(data, user) {
    var value = JSON.parse(data.data)
    await axios.post('https://api.github.com/repos/' + value.id + '/pulls', {
            head: value.head,
            base: value.base,
            title: value.title,
            body: value.body
        }, {
            headers: {
                authorization: `Bearer ${user.github_token}`,
                accept: "application/vnd.github.v3+json"
            }
        }).then(res => { console.log(res.data) })
        .catch(err => { console.log(err) })
}

// id repo name ?
// async
//'https://api.github.com/repos/' + data.project_id+ '/git/refs'
//
// headers: {
//     authorization: `Bearer ${user.github_token}`,
//     accept: "application/vnd.github.v3+json"
// },
// data: qs.stringify({
//     'ref': "refs/heads/" + data.name,
//     'sha': data.sha
// })

async function CreateBranch(data, user) {
    var value = JSON.parse(data.data)
    var target = ""
    const sha = await axios.get("https://api.github.com/repos/" + value.id + "/branches", {
            headers: {
                authorization: `Bearer ${user.github_token}`,
            }
        })
        .then(res => { return res.data })
        .catch(err => console.log(err))
    sha.forEach(s => {
        if (s.name === value.sha)
            target = s.commit.sha
    })
    await axios.post("https://api.github.com/repos/" + value.id + "/git/refs", {
            ref: "refs/heads/" + value.name,
            sha: target
        }, {
            headers: {
                authorization: `Bearer ${user.github_token}`,
                accept: "application/vnd.github.v3+json"
            }
        }).then(res => console.log(res.data))
        .catch(err => console.log(err))
}