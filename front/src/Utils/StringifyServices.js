const services = new Map([
    [1, {name: 'Gmail', icon: 'Gmail.png'}],
    [2, {name: 'Drive', icon: 'drive.png'}],
    [3, {name: 'Epitech', icon: 'Epitech.png'}],
    [4, {name: 'Github', icon: 'Github.png'}],
    [5, {name: 'Trello', icon: 'Trello.png'}],
    [6, {name: 'GitLab', icon: 'GitLab.png'}],
    [7, {name: 'Agenda', icon: 'Agenda.png'}]
]);

const triggersActions = {
    Gmail: {
        trigger: new Map([
            [1, 'Email contains ${keyword} in object.'],
            [2, 'Receive from ${email}.'],
            [3, 'Receive ${keyword} ${date}.']
        ]),
        action: new Map([
            [1, 'Send mail to ${email} with object ${object}.']
        ])
    },
    Drive: {
        action: new Map([
            [1, 'Share Files']
        ])
    },
    Epitech: {
        trigger: new Map([
            [1, 'New meeting.'],
            [2, 'New alert.'],
            [3, 'New missing.']
        ]),
    },
    Github: {
        trigger: new Map([
            [1, 'New project.'],
            [2, 'New commit on ${id}.'],
            [3, 'New pull request on ${projectid}.'],
        ]),
        action: new Map([
            [1, 'Pull request ${title} on ${id} from ${head} to ${base}.'],
            [2, 'Create branch ${name} on ${id}']
        ])
    },
    Trello: {
        trigger: new Map([
            [1, 'Check Activities on List'],
        ]),
        action: new Map([
            [1, 'Create A List']
        ])
    },
    GitLab: {
        trigger: new Map([
            [1, 'New Project'],
        ]),
        action: new Map([
            [1, 'Create Branch On Project ${id} with Branch Name ${name}']
        ])
    },
    Agenda: {
        trigger: new Map([
            [1, 'Days until'],
            [2, 'Every ${recurrence}']
        ]),
    }
};

export const stringifyService = serviceId => services.get(serviceId);
export const stringifyTrigger = (id, serviceId)  => triggersActions[services.get(serviceId).name].trigger.get(id);
export const stringifyAction = (id, serviceId) => triggersActions[services.get(serviceId).name].action.get(id)

const replaceWithData = (result, data) => {
    try {
        if (!data)
            return result;
        data = JSON.parse(data)
    } catch (e) {
        if (typeof data === 'string')
            return result.replace(/\$\{[a-z]+\}/, data);
        else
            return null;
    }
    Object.entries(data).forEach(e => {
        result = result.replace('${' + e[0] + '}', e[1]);
    });
    return result;
};

export const stringifyTriggerData = (id, serviceId, data) => replaceWithData(stringifyTrigger(id, serviceId), data);
export const stringifyActionData = (id, serviceId, data) => replaceWithData(stringifyAction(id, serviceId), data);