const cron = require('node-cron')
const Agenda = require('./CallBack/Agenda')
const Trello = require('./CallBack/Trello')
const Epitech = require('./CallBack/Epitech')
const Github = require('./CallBack/Github')
const Gitlab = require('./CallBack/Gitlab')
const Gmail = require('./CallBack/Gmail')
const Drive = require('./CallBack/Drive')
const User = require('./models/User')
const Service = require('./models/Service')

exports.runCron = async() => {
    console.log("cron call")
        cron.schedule('*/5 * * * *', function() {
            CheckJob()
        })
    return;
}

async function CheckJob() {
    // Check All User // All Services
    // 
    var users = await User.find().populate("services").exec()
    users.forEach(user => {
        user.services.forEach(service => {
            if (CheckCondition(service, user))
                ExecAction(service, user)
        })
    })
}

async function CheckCondition(service, user) {
    var check = true;
    service.conditions.forEach(async condition => {
        if (!check)
            return;
        if (service.serviceTrigger === 1)
            check = await Gmail.ParseCondition(condition, user, service.last_check)
        else if (service.serviceTrigger === 3)
            check = await Epitech.ParseCondition(condition, user), service.last_check
        else if (service.serviceTrigger === 4)
            check = await Github.ParseCondition(condition, user, service.last_check)
        else if (service.serviceTrigger === 5)
            check = await Trello.ParseCondition(condition, user, service.last_check)
        else if (service.serviceTrigger === 6)
            check = await Gitlab.ParseCondition(condition, user)
        else if (service.serviceTrigger === 7)
            check = await Agenda.ParseCondition(condition, user, service.last_check)
    })
    return check;
}

function ExecAction(service, user) {
    service.actions.forEach(action => {
        if (service.serviceAction === 1)
            Gmail.ParseAction(action, user)
        else if (service.serviceAction === 2)
            Drive.ParseAction(action, user)
        else if (service.serviceAction === 4)
            Github.ParseAction(action, user)
        else if (service.serviceAction === 5)
            Trello.ParseAction(action, user)
        else if (service.serviceAction === 6)
            Gitlab.ParseAction(action, user)
    })
    Service.updateOne({ _id: service._id }, {
        $set: {
            last_check: new Date()
        }
    })
}