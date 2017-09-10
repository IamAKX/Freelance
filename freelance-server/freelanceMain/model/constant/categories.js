var mongoose = require('mongoose');

var constantSchema = mongoose.Schema({
    name: { type: String, required: true },
    url: { type: String, required: true }
});

module.exports = mongoose.model('category', constantSchema);