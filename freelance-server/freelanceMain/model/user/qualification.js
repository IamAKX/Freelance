var mongoose = require('mongoose');

var qualificationSchema = mongoose.Schema({
    stage: { type: String },
    instName: { type: String },
    board: { type: String },
    marksType: { type: String },
    marks: { type: Number },
    yop: { type: String }
});

module.exports = qualificationSchema;