var mongoose = require('mongoose');
var userModel = mongoose.model('user');
var path = require('path');
var hashService = require(path.join(__dirname, '..', '..', 'service', 'util', 'encryption'));
var check = require(path.join(__dirname, '..', '..', 'service', 'util', 'checkValidObject'));

/**
 * checks in database if a user exixts in database withe same username
 */
exports.checkUserExists = function(req, res, next) {
    userModel.findOne({ 'userName': req.body.username }, function(err, data) {
        if (err) {
            return next(err);
        } else {
            if (check.isNull(data)) {
                res.status(200);
                res.send(true);
                return next();
            } else {
                res.status(500);
                res.send({
                    status: false,
                    reason: "user exists"
                });
                return next();
            }
        }
    });
};

/**
 * checks if phone number exixts in the database
 */

exports.checkPhoneExists = function(req, res, next) {
    userModel.findOne({ 'phone': req.body.phoneNo }, function(err, data) {
        if (err) {
            return next(err);
        } else {
            if (check.isNull(data)) {
                res.status(200);
                res.send(true);
                return next();
            } else {
                res.status(500);
                res.send({
                    status: false,
                    reason: "phoneNo exists"
                });
                return next();
            }
        }
    });
};