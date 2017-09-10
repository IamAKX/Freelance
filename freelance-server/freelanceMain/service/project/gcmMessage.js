var path = require('path');
var mongoose = require('mongoose');
var FCM = require('fcm-node');
var _ = require('lodash');
var userModel = mongoose.model('user');
var config = require(path.join(__dirname, '..', '..', 'bin', 'config'));
var serverKey = config.fcmServerKey;
var fcm = new FCM(serverKey);

var getTokens = function(ids, tokenCallback) {
    userModel.find({ _id: { $in: ids } }, { gcmToken: 1, _id: 0 }, function(err, data) {
        tokenCallback(_.map(data, 'gcmToken'));
    })
}

var sendMessageToMultiple = function(title, body, tokens, cb) {

    var message = { //this may vary according to the message type (single recipient, multicast, topic, et cetera) 
        registration_ids: tokens,
        notification: {
            title: title,
            body: "New Job Posted By " + body.employer.name,
            sound: "default"
        },
        data: {
            my_key: config.webApiKey,
            id: body.id,
            name: body.employer.name,
        }
    };

    fcm.send(message, function(err, response) {
        if (err) {
            cb(err, null);
        } else {
            cb(null, response);
        }
    });

}

exports.sendMessageToMultiple = sendMessageToMultiple;
exports.getTokens = getTokens;