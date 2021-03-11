const express = require('express');
const bodyParser = require('body-parser');
const mongoose = require('mongoose');
const cors = require('cors');
const RequestIp = require('@supercharge/request-ip');
const { MONGODB_URL } = require("./Config")

const userRoutes = require('./routes/User');
const serviceRoutes = require('./routes/Service');
const connexionRoutes = require('./routes/Connection');
const githubRoutes = require('./routes/Github');
const gitlabRoutes = require('./routes/Gitlab');
const driveRoutes = require('./routes/Drive');
const trelloRoutes = require('./routes/Trello');

const app = express();
app.use(cors());
mongoose.connect(MONGODB_URL, {
        useNewUrlParser: true,
        useFindAndModify: false,
        promiseLibrary: global.Promise,
        useUnifiedTopology: true,
    })
    .then(() => console.log('Connexion à MongoDB réussie !'))
    .catch(() => console.log('Connexion à MongoDB échouée !'));

app.use(bodyParser.json());
app.use((req, res, next) => {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content, Accept, Content-Type, Authorization');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, PATCH, OPTIONS');
    next();
});
app.get('/', function(req, res) {
    res.send("Serv Up !");
})

app.use('/api/user', userRoutes);
app.use('/api/connexion', connexionRoutes);
app.use('/api/service', serviceRoutes);
app.use('/api/github', githubRoutes);
app.use('/api/gitlab', gitlabRoutes);
app.use('/api/drive', driveRoutes);
app.use('/api/trello', trelloRoutes);
app.use('/about.json', function(req, res, next) {
    return res.status(200).json({
        client: {
            host: RequestIp.getClientIp(req)
        },
        server: {
            current_time: Math.floor(Date.now() / 1000),
            services: [{
                name: "Agenda",
                actions: [{
                    name: "Days Until",
                    description: "Triggerd on X Days until Date"
                }, {
                    name: "Every X Y",
                    description: "Triggerd Every X (number) Y (type : Days / Month)"
                }],
                reactions: []
            }, {
                name: "Drive",
                actions: [],
                reactions: [{
                    name: "Share Files",
                    description: "Share mutliple files to multiple email"
                }]
            }, {
                name: "Epitech",
                actions: [{
                    name: "New Meeting",
                    description: "Trigger on New Meeting Alert"
                }, {
                    name: "New Alert",
                    description: "Trigger on New Alert"
                }, {
                    name: "New Missing",
                    description: "Trigger on Recent Missing"
                }],
                reactions: []
            }, {
                name: "Github",
                actions: [{
                    name: "New Project",
                    description: "Trigger on Project Creation"
                }, {
                    name: "New Commit",
                    description: "Trigger on new Commit for preselected Project"
                }, {
                    name: "New Pull Request",
                    description: "Triggerd on New Pull Request for preselected Project"
                }],
                reactions: [{
                    name: "Pull Request",
                    description: "Create new Pull Request for preselected Project and refs"
                }, {
                    name: "Create Branch",
                    description: "Create new Branch for preselected Project"
                }]
            }, {
                name: "Gitlab",
                actions: [{
                    name: "New Project",
                    description: "Trigger on Project Creation"
                }],
                reactions: [{
                    name: "New Branch",
                    description: "Create new Branch for preselected Project"
                }]
            }, {
                name: "Gmail",
                actions: [{
                    name: "Contain KeyWord",
                    description: "Trigger if Subject contain KeyWord"
                }, {
                    name: "Receive From",
                    description: "Trigger if email is receive from preselected recipe"
                }, {
                    name: "Receive In Time",
                    description: "Trigger if receive before or after X (time)"
                }],
                reactions: [{
                    name: "Send",
                    description: "Send Mail to target"
                }]
            }, {
                name: "Trello",
                actions: [{
                    name: "Check Activities",
                    description: "Trigger if new activities are detected on preselected list"
                }],
                reactions: [{
                    name: "Create List",
                    description: "Create new list on preselected Board"
                }]
            }]
        }
    })
})
module.exports = app;