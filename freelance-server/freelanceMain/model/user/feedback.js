var mongoose = require('mongoose');

var feedbackSchema = mongoose.Schema({
    userId: { type: String },
    fileId: { type: String },
    rating: { type: Number },
    type: { type: String },
    content: { type: String },
    createdAt: { type: Date, default: Date.now() }
});

module.exports = mongoose.model('feedback', feedbackSchema);