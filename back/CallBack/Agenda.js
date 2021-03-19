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
    var target = new Date(parseInt(data.condition.split(" ")[1]))
    tmp.setDate(tmp.getDate() + parseInt(data.condition.split(" ")[0]))
    target.setHours(0, 0, 0, 0)
    if (tmp.getTime() === target.getTime())
        return true;
    return false;
}

function GetWeek(date) {
    var dnum = date.getUTCDay() || 7;
    date.setUTCDate(date.getUTCDate() + 4 - dnum)
    var ys = new Date(Date.UTC(date.getUTCFullYear(), 0, 1))
    return Math.ceil((((date - ys) / 86400000) + 1) / 7) - 1
}

function CheckTimeComparaison(data, last_check) {
    var tmp = new Date(last_check)
    var cmp = new Date()
    tmp.setHours(0, 0, 0, 0)
    cmp.setHours(0, 0, 0, 0)
    if (tmp.getTime() === cmp.getTime())
        return false;
    var cond = data.condition.split(" ")
    if (cond.length !== 2)
        return false;
    if (cond[1] === "day") {
        return true
    } else if (cond[1] === "week") {
        if (GetWeek(tmp) + parseInt(cond[0]) === GetWeek(new Date()))
            return true;
    } else if (cond[1] === "month") {
        return (tmp.getMonth() + parseInt(cond[0])) === cmp.getMonth()
    }
    return false;
}