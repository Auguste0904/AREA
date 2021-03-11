const agenda = require('./Agenda')
const gmail = require('./Gmail')
const trello = require('./Trello')
const drive = require('./Drive')
const epitech = require('./Epitech')
const gitlab = require('./Gitlab')
const github = require('./Github')

var User = {
    firstname: "test",
    lastname: "testName",
    email: "dimarianathan@gmail.com",
    github_token: "486388c2fe6bbe8c2fdfedbd8e44a44c8bc80788",
    gitlab_token: "f9c07ee584d5b5d0735743a940fdae76b2a7c38888db405ce1a7a9bd23081e29",
    discord_token: "JZwg4xwcP0aV2yWIrXQ4q3yJ0dBWoA",
    google_token: "1//03QIy4h0mIv68CgYIARAAGAMSNwF-L9IrnaKxsScY5__BtN6WeZXEg8Z-RT5wTry9-djrgcMe4SbcCRob3jGrpAWK076s7hE2l6g",
    epitech: {
        email: "nathan.di-maria@epitech.eu",
        autologin: "https://intra.epitech.eu/auth-36793f541d35f0312044c2fbd1750a500b4d56dd"
    },
    trello_token: "86b0eeb037c91c966ced5d7142e0ab998923e4e0eaee29f87949dcea87b9a316"
}

var last_check = new Date()
last_check.setDate(5)

// Check Agenda :

function Agenda() {
    var data = {
        event: 1,
        data: "4 " + Date.now()
    }
    agenda.ParseCondition(data, User, last_check)
    data = {
        event: 2,
        data: "3 Month"
    }
    agenda.ParseCondition(data, User, last_check)
}

// Check Discord :

function Discord() {
    var data = {
        event: 1,
        data: "Before 15h"
    }
    discord.ParseCondition(data, User, last_check)
    data = {
            event: 2
        }
        //Discord.ParseCondition(data, User, last_check)
}

// Check Drive :

async function Drive() {
    var jso = {
        Email: "bryan.bouillot@epitech.eu;nathan.di-maria@epitech.eu",
        Files: "1-XfJIFiu5lkVtyvSi1rmZ19c0sxl8Ma9"
    }
    var data = {
        event: 1,
        data: JSON.stringify(jso)
    }
    drive.ParseAction(data, User)
}

// Check Epitech :

async function Epitech() {
    var data = {
        event: 3
    }
    console.log( await epitech.ParseCondition(data, User, last_check))
}

// Check Github :

async function Github() {
    var pull = {
        id: "PookieTek/Back-Skrabbl",
        name: "truc",
        sha: "master",
    }
    var data = {
        event: 2,
        data: JSON.stringify(pull)
    }
    console.log(await github.ParseAction(data, User))
}


// Check Gitlab :

async function Gitlab() {
    var branch = {
        id: "19789623",
        name: "machintruc",
        sha: "master"
    }
    var data = {
        event: 1,
        data: JSON.stringify(branch)
    }
    gitlab.ParseAction(data, User)
}

// Check Gmail :

function Gmail() {
    // var data = {
    //     event: 1,
    //     data: "Alerte"
    // }
    // gmail.ParseCondition(data, User, last_check)
    // data = {
    //     event: 2,
    //     data: "orly@cloudinary.com"
    // }
    // gmail.ParseCondition(data, User, last_check)
    // data = {
    //     event: 3,
    //     data: "After 22:00"
    // }
    // gmail.ParseCondition(data, User, last_check)
    // var message = {
    //     email: 'bryan.bouillot@epitech.eu',
    //     object: 'test area',
    //     body: 'coucou bryan'
    // }
    // var data = {
    //     event: 1,
    //     data: JSON.stringify(message)
    // }
    // gmail.ParseAction(data, User)
}


async function Trello() {
    var data = {
        event: 1,
        data: "601addfeb153854a716301d0 newlist truc machin"
    }
    console.log(await trello.ParseAction(data, User, last_check))
}

//Discord()
//Github()
//Gitlab()