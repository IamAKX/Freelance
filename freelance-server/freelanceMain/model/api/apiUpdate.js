var mongoose = require('mongoose');

var apiUpdateSchema =new mongoose.Schema({
    changeDate:{type:Date}
});

module.export = mongoose.model('apiupdate',apiUpdateSchema);