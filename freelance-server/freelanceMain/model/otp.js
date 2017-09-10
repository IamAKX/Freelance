var mongoose = require('mongoose');

var otpSchema = new mongoose.Schema({
    phone : {type:String,required:true},
    otp : {type:String,required:true},
    expTime: {type:Date,required:true},
    status: {type:Boolean,required:true,default:false}
});

module.exports = mongoose.model('otp',otpSchema);
