var path = require('path');
var check = require(path.join(__dirname, '..', '..', 'service', 'util', 'checkValidObject'));
var mongoose = require('mongoose');
var userModel = mongoose.model('user');
var ObjectId = require('mongoose').Types.ObjectId;


exports.setToken = function(req, res, next) {
    userModel.update({ _id: req.userId }, { $set: { gcmToken: req.body.token } }, tokenUpdateCallback);

    function tokenUpdateCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: false,
                reason: "error updating gcm Token"
            });
            return next();
        } else {
            res.status(200);
            res.send({ status: true });
            return next();
        }
    }
}

exports.getToken = function(req, res, next) {
    userModel.findOne({ _id: new ObjectId(req.userId) }, { gcmToken: 1, _id: 0 }, tokenFindCallback);

    function tokenFindCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: false,
                reason: "error fetching gcm Token"
            });
            return next();
        } else {
            res.status(200);
            res.send({ token: data.gcmToken });
            return next();
        }
    }
}