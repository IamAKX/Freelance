var path = require('path');
var request = require('request');
var mongoose = require('mongoose');
var otpModel = mongoose.model('otp');
var moment = require('moment');
var check = require(path.join(__dirname,'..','..','service','util','checkValidObject'));
var config = require(path.join(__dirname,'..','..','bin','config'));
var low = 1000;
var high = 9999;
var smsService = require(path.join(__dirname,'..','..','service','util','otpService'));
function getRandom(){
  return (Math.random() * (high - low) + low).toString().substr(0,4);
}

function sendSms (phone,otp,callback){
  var stringPayload = "Your Freelance mobile verification OTP is "+ otp +".It will expire in next "+config.OtpExpTime+" minutes.";
  request.post('https://smsany.herokuapp.com/api/sms/sendsms')

  var postBody = {
    method: 'post',
    url: 'https://smsany.herokuapp.com/api/sms/sendsms',
    body: {
      'phoneNumber' : phone,
      'message' : stringPayload,
      'accessKey' : config.SMS_ANY_APIKEY
    },
    	json: true,
  };
  request(postBody,function(err,res,body){
      callback(err,res,body);
  })
}

exports.generateOtp = function(req,res,next){

  var payload = {
    phone : req.body.phoneNo,
    otp : getRandom(),
    expTime: moment(new Date()  ).add(config.OtpExpTime, 'minutes').valueOf(),
    status: false
  }
    // var stringPayload = "Your OTP for freelancer is "+ payload.otp +".It will expire in next "+config.OtpExpTime+" minutes";

  var query = {phone:req.body.phoneNo};
  otpModel.findOne(query,function(err,data){
    if(err){
      res.status(500);
      return next(err);
    }
    if(!check.isNull(data)){
      if(data.status == true){
        res.status(200);
        res.send({status:false,reason:'already verified'});
        return next();
      }
      else{
        otpModel.update(query,{$set:{otp:payload.otp,expTime:payload.expTime}},function(err,data){
            if(err){
              res.status(500);
              return next(err);
            }
            sendSms(req.body.phoneNo,payload.otp,function(a,b,body){
              if(body.status == 'success'){
                res.status(200);
                res.send({
                  status:true,
                });
                return next();
              }
              else{
                res.status(500);
                res.send({
                  status:false,
                  reason:"failed to reach the number"
                });
                return next();
              }
            });

        });
      }
    }
    else{
      otpModel.create(payload,function(err,data){
          if(err){
            res.status(500);
            return next(err);
          }
          sendSms(req.body.phoneNo,stringPayload,function(a,b,body){
            if(body.status == 'success'){
              res.status(200);
              res.send({
                status:true,
              });
              return next();
            }
            else{
              res.status(500);
              res.send({
                status:false,
                reason:"failed to reach the number"
              });
              return next();
            }
          });
      });
    }
  });
}

exports.verifyOtp = function(req,res,next){

    query = {
      phone:req.body.phoneNo
    }
    otpModel.findOne(query,function(err,data){
      // res.send("ok");
        if (data.expTime <= Date.now()) {
            res.status(500);
             res.send({
                status:false,
                reason:"Verification timeout"
            });
            return next();
        }
        else if(!req.body.otp){
            res.status(500);
            res.send({
                status:false,
                reason:"OTP not found"
            });
            return next();
        }
        else{
            if(req.body.otp == data.otp){
                // data.status = true;
                otpModel.remove(query,function(err,updateData){

                      res.status(200);
                        res.send({
                            status:true,
                            reason:"Successfully verified"
                        });
                        return next();

                });
            }
            else{
              res.status(500);
                res.send({
                    status:false,
                    reason:"Invalid Otp, Retry"
                });
                return next();
            }
        }

    });
};
