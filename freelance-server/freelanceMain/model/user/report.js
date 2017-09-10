var mongoose = require('mongoose');

var reportSchema = mongoose.Schema({
    userId: { type: String },
    type: { type: String },
    fileId: { type: String },
    content: { type: String },
    createdAt: { type: Date, default: Date.now() }
});

module.exports = mongoose.model('report', reportSchema);