exports.ParseCondition = async(data, user, last_check) => {
    if (data.event === 1)
        return CheckTimeLeft(data, last_check)
    else if (data.event === 2)
        return CheckTimeComparaison(data, last_check)
    return false
}

function CheckTimeLeft(data, last_check) {
    var tmp = new Date(last_check)
    var cmp = new Date()
    tmp.setHours(0, 0, 0, 0)
    cmp.setHours(0, 0, 0, 0)
    if (tmp.getTime() === cmp.getTime())
        return false;
    var target = new Date(data.condition.split()[1])
    tmp.setDate(tmp.getDate() + data.condition.split()[0])
    target.setHours(0, 0, 0, 0)
    if (tmp.getTime() === target.getTime())
        return true;
    return false;
}

function CheckTimeComparaison(data, last_check) {
    var tmp = new Date(last_check)
    var cmp = new Date()
    tmp.setHours(0, 0, 0, 0)
    cmp.setHours(0, 0, 0, 0)
    if (tmp.getTime() === cmp.getTime())
        return false;
    var cond = data.split()
    if (cond.length !== 2)
        return false;
    if (cond[1] === "Day") {
        return cmp.getDay() === cond[0]
    } else if (cond[2] === "Week") {
        if (cmp.getDate() % 7 === cond[0])
            return true;
    } else if (cond[3] === "Month") {
        return (cmp.getMonth() + 1) === cond[0]
    }
    return false;
}