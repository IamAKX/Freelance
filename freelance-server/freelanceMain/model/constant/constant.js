var mongoose = require('mongoose');

var constantSchema = mongoose.Schema({
    name: { type: String, required: true },
    value: [{ type: String, required: true }]
});

module.exports = mongoose.model('constants', constantSchema);