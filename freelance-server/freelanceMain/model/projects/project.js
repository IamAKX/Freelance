var mongoose = require('mongoose');
var path = require('path');


var durationSchema = mongoose.Schema({
    start: { type: Number },
    end: { type: Number },
    type: { type: String, enum: ['hours', 'days', 'weeks', 'months'] },
    deadline: { type: Date, default: Date.now() }
}, { _id: false });

var participantSchema = mongoose.Schema({
    userId: { type: String },
    status: { type: String, enum: ['n/a', 'accept', 'reject', 'hired'] },
    comment: { type: String, default: '' },
    chatroomId: { type: String, default: '' }
}, { _id: false })

var projectSchema = mongoose.Schema({
    name: { type: String, required: true },
    userId: { type: String, required: true },
    projectStatus: { type: String, enum: ['in progress', 'timeout', 'done'], default: 'in progress' },
    description: { type: String, required: true },
    duration: durationSchema,
    participant: [participantSchema],
    createdAt: { type: Date, default: Date.now() },
    category: { type: String, required: true }
})

module.exports = mongoose.model('project', projectSchema);