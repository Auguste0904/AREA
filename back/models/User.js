const { datacatalog } = require('googleapis/build/src/apis/datacatalog');
const mongoose = require('mongoose');

const userSchema = mongoose.Schema({
    firstname: {
        type: String
    },
    lastname: {
        type: String
    },
    email: {
        type: String,
        required: true
    },
    password: {
        type: String
    },
    google_token: {
        type: String,
    },
    google_refresh: {
        type: String,
    },
    azure_token: {
        type: String
    },
    trello_token: {
        type: String
    },
    epitech: {
        autologin: {
            type: String
        },
        email: {
            type: String
        }
    },
    yammer_token: {
        type: String
    },
    github_token: {
        type: String
    },
    discord_token: {
        type: String
    },
    linkedin_token: {
        type: String
    },
    gitlab_token: {
        type: String
    },
    services: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'services'
    }],
    code: {
        type: Number
    },
    active: {
        type: Boolean
    }
});

module.exports = mongoose.model('user', userSchema, 'user');