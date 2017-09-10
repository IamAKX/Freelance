var mongoose = require("mongoose");

var chatSchema = mongoose.Schema({
    participants: [{ type: String }]
})

module.exports = mongoose.model('chat', chatSchema);