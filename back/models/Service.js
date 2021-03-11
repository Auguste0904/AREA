const mongoose = require('mongoose');

/**
 *  Azure : 1
 *  Github : 2
 *  Discord: 3
 *  Linkedin: 4
 */
const serviceSchema = mongoose.Schema({
    serviceTrigger: {
        type: Number,
        required: true
    },
    conditions: [{
        event: {
            type: Number,
            required: true
        },
        condition : {
            type: String,
        }
    }],
    serviceAction: {
        type: Number,
        required: true
    },
    actions: [{
        event: {
            type: Number,
            required: true
        },
        action: {
            type: String,
        }
    }],
    last_check: {
        type: Date,
    }
});

module.exports = mongoose.model('services', serviceSchema, 'services');