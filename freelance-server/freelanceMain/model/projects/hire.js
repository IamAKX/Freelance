var mongoose = require("mongoose");

var hireSchema = mongoose.Schema({
    cData: { type: Date, required: true },
    aDate: { type: Date, required: true },
    createdAt: { type: Date, required: true, default: Date.now() },
    employerId: { type: String, required: true },
    employeeId: { type: String, required: true },
    projectId: { type: String, required: true },
    amount: { type: Number, required: true },
    chatroomId: { type: String, required: true },
    status: { type: String, default: 'hired' }
})

module.exports = mongoose.model('hire', hireSchema);