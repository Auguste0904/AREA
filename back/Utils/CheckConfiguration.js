exports.CheckServices = (service) => {
    if (service.serviceTrigger < 1 || service.serviceTrigger > 7)
        return false;
    else if (service.serviceAction < 1 || service.serviceAction > 7)
        return false;
    var check = service.conditions.findIndex(c => {
        return ParseService(c, service.serviceTrigger, 1)
    })
    if (check === -1)
        return false;
    check = service.actions.findIndex(a => {
        return ParseService(a, service.serviceAction, 2)
    })
    if (check === -1)
        return false;
    return true;
}

function ParseService(data, service, type) {
    if (service === 1)
        return CheckGmail(data, type)
    else if (service === 2)
        return CheckDrive(data, type)
    else if (service === 3)
        return CheckEpitech(data, type)
    else if (service === 4)
        return CheckGithub(data, type)
    else if (service === 5)
        return CheckTrello(data, type)
    else if (service === 6)
        return CheckGitlab(data, type)
    else if (service === 7)
        return CheckAgenda(data, type)
    return true;
}

function CheckGmail(data, type) {
    if (type === 1 && (data.event < 1 || data.event > 3))
        return false
    else if (type === 2 && data.event !== 1)
        return false;
    return true;
}

function CheckDrive(data, type) {
    if (type === 1)
        return false
    else if (type === 2 && data.event !== 1)
        return false
    return true
}

function CheckEpitech(data, type) {
    if (type === 1 && (data.event < 1 || data.event > 3))
        return false
    else if (type === 2)
        return false
    return true
}

function CheckGithub(data, type) {
    if (type === 1 && (data.event < 1 || data.event > 3))
        return false
    else if (type === 2 && (data.event < 1 || data.event > 2))
        return false
    return true
}

function CheckTrello(data, type) {
    // TODO
    if (type === 1 && (data.event < 1 || data.event > 2))
        return false
    else if (type === 2 && data.event !== 1)
        return false
    return true
}

function CheckGitlab(data, type) {
    if (type === 1 && (data.event < 1 || data.event > 3))
        return false
    else if (type === 2 && data.event !== 1)
        return false
    return true
}

function CheckAgenda(data, type) {
    if (type === 1 && (data.event < 1 || data.event > 2))
        return false
    else if (type === 2)
        return false
    return true
}