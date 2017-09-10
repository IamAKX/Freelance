var mongoose = require('mongoose');

var experienceSchema = mongoose.Schema({
    category:{type:String},
    details:{type:String},
    link:{type:String},
    imageLink :{type:String}
});

module.exports = experienceSchema;