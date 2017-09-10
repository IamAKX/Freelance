var mongoose = require('mongoose');

var appSchema = new mongoose.Schema({
    name: { type: String, required: true },
    size: { type: Number, required: true },
    link: { type: String, required: true },
    created_at: { type: Date, default: Date.now() },
    comment: { type: String },
    version: { type: String, default: 'v 0.0.0' }
});

module.export = mongoose.model('app', appSchema);