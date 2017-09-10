var mongoose = require('mongoose');
var userModel = mongoose.model('user');
var path = require("path");
var fs = require('fs');
var ObjectId = require('mongoose').Types.ObjectId;
var high = 9999;
var low = 1000;

function getRandom(){
  return (Math.random() * (high - low) + low).toString().substr(0,4);
}

function convertImg(body,cb){
    var base64Data = body.replace(/^data:image\/png;base64,/,"");
  	var binaryData = new Buffer(base64Data, 'base64').toString('binary');
    cb(binaryData);
}

exports.upload = function(path,img,cb){
     convertImg(img,function(binaryData){
        //  console.log(binaryData);
         fs.writeFile(path,binaryData,"binary",function(err){
             if(err){
                 console.log(err);
                 cb(err,null);
             }
             else{
                 cb(null,true);
             }
         });
     });
};