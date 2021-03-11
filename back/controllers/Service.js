const Service = require('../models/Service')
const User = require('../models/User')
const tools = require('../Utils/CheckConfiguration')


exports.CreateService = async (req, res, next) => {
    if (!req.body.trigger || !req.body.conditions || !req.body.service || !req.body.actions)
        return res.status(401).json({error: "params Missing"})
    var check = new Service({
        serviceTrigger: req.body.trigger,
        conditions: req.body.conditions,
        serviceAction: req.body.service,
        actions: req.body.actions
    })
    if (!tools.CheckServices(check))
        return res.status(401).json({error: "Invalid Services"})
    var serv = await Service.create({
        serviceTrigger: req.body.trigger,
        conditions: req.body.conditions,
        serviceAction: req.body.service,
        actions: req.body.actions
    })
    if (!serv)
        return res.status(401).json({err: "pas bien"})
    var us = await User.updateOne({_id : res.locals.user}, {
        $push: {
            services: serv._id,
        }
    })
    if (!us)
        return res.status(401).json({err: "pas bien"})
    console.log("allo ?")
    return res.status(201).json(serv)
}

exports.EditService = async (req, res, next) => {
    if (!req.body.id || !req.body.trigger || !req.body.conditions || !req.body.service || !req.body.actions)
        return res.status(401).json({error: "params Missing"})
    if (req.body.trigger < 1 || req.body.trigger > 7 || req.body.service < 1 || req.body.service > 7)
        return res.status(401).json({error: "Invalid Services"})
    var check = new Service({
        serviceTrigger: req.body.trigger,
        conditions: req.body.conditions,
        serviceAction: req.body.service,
        actions: req.body.actions
    })
    if (!tools.CheckServices(check))
        return res.status(401).json({error: "Invalid Services"})
    var serv = await Service.findOneAndUpdate({_id:  req.body.id}, {
        serviceTrigger: req.body.trigger,
        conditions: req.body.conditions,
        serviceAction: req.body.service,
        actions: req.body.actions
    }, {new: true})
    .catch(err => res.status(400).json({err}))
    return res.status(201).json(serv)
}

exports.DeleteService = (req, res, next) => {
    if (!req.body.id)
        return res.status(401).json({error: "params missing"})
    User.updateOne({_id: res.locals.user}, {
        $pull : {
            services: req.body.id
        }
    })
    var check = Service.deleteOne({_id: req.body.id})
    .then(() => res.status(201).json({msg: "ok"}))
    .catch(err => res.status(500).json(err))
    return res;
}

exports.GetService = async (req, res, next) => {
    if (!req.params.id)
        return res.status(401).json(({error: "params missing"}))
    var serv = await Service.findOne({_id: req.params.id})
    if (!serv)
        return res.status(400).json({err: "no service found"})
    return res.status(200).json({serv})
}