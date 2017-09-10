var mongoose = require("mongoose");
var presentJobSchema = mongoose.Schema({
    
    position:{type:String},
    organzation:{type:String},
    startDate:{type:String},
    endDate:{type:String},
    presentlyWorking:{type:String}

});
module.exports = presentJobSchema;