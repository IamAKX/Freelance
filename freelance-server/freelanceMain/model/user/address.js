var mongoose = require('mongoose');

var addressSchema = mongoose.Schema({
    street:{type:String},
    city:{type:String},
    pincode:{type:String},
    state:{type:String},
    type:{type:String}
});

module.exports = addressSchema;