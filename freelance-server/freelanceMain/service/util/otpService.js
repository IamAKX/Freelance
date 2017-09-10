var path = require('path');
var mongoose = require('mongoose');
var low = 100000;
var high = 999999;
var AWS = require('aws-sdk');
var config = require(path.join(__dirname, '..', '..','bin','config'));
var countryCode = '+91';

var sns = new AWS.SNS({
	accessKeyId: config.iam_s3_admin_id,
  secretAccessKey: config.iam_s3_admin_password,
	region:'ap-southeast-1'
});

exports.sendSms = function(phoneNumber,message,cb){
    var AttributeParams = {
        attributes: {
            DefaultSMSType: 'Transactional',
            DefaultSenderID:'Apnastudy'
        }
    };
    if(phoneNumber.length < 10){
        cb({
            status:'failed',
            error:{
                message:'Invalid Number'
            }
        });
    }
    else if(phoneNumber.length == 10){
        phoneNumber = countryCode+phoneNumber;
    }

    sns.setSMSAttributes(AttributeParams, function(err, data) {
        if (err) {
            cb({
                status:'failed',
                error:err
            });
        }
        else{
                var params = {
                    MessageStructure : 'String',
                    PhoneNumber : String(phoneNumber),
                    MessageAttributes: {
                        someKey: {
                        DataType: 'String',
                        StringValue: 'String'
                        }
                    },
                    Message : message
                };
                sns.publish(params, function(err, data) {
                    if (err){
                      cb({
                          status:'failed',
                          error:err
                      });
                    }

                    else {
                        cb({
                            status:'success',
                            data:data
                        });
                    }
                });
        }
    });
};
