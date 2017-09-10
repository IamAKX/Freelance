var mongoose = require('mongoose');

var apiSchema = new mongoose.Schema({
    name : {type:String,required:true},
    category : {type:String,required:true},
    api:{type:String,required:true},
    header:{type:String,required:true},
    type:{type:String,required:true},
    reqBody:{type:String,required:true},
    resBody:{type:String,required:true},
    description:{type:String,required:true}
});

module.exports = mongoose.model('api',apiSchema);
