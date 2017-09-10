var jwt = require('jwt-simple');
var moment = require('moment');
var path = require('path');
var config = require(path.join(__dirname, '..', '..', 'bin', 'config'));
var redisService = require(path.join(__dirname, 'redisToken'))
var check = require(path.join(__dirname, 'checkValidObject'));
exports.createToken = function(user, callback) {
    var payload = {
        userId: user._id,
        secret: config.ENV_MIX
    };
    var token = encodeToken(payload, config.JWT_ENCODE);
    redisService.saveToken(token, user, callback);
};


function encodeToken(payload, key) {
    return jwt.encode(payload, key);
}
exports.encodeToken = encodeToken;

function decodeToken(token, key) {
    return jwt.decode(token, key);
}
exports.decodeToken = decodeToken;

//During logout remove token from Redis
//TODO: invalidate token from JWT
exports.removeToken = function(req, res, next) {
    validateHeader(req, res, function() {
        redisService.removeToken(res, req.userId, next);
    });
};

exports.validateToken = function(req, res, next) {
    validateHeader(req, res, function(token) {
        redisService.getToken(req, res, req.userId, token, next);
    });
};

//Validate token and user info in http header
function validateHeader(req, res, next) {
    if (!req.headers.authorization) {
        res.status(401);
        return res.send({
            status: false,
            reason: 'You are not authorized'
        });
    }
    //Recover token from the http header
    var token = req.headers.authorization.split(' ')[1];
    verifyToken(req, res, token, next);
}

//Decode and verify the token and match with the payload
function verifyToken(req, res, token, callback) {

    var payload = decodeToken(token, config.JWT_ENCODE);
    if (check.isUndefinedOrNullOrEmptyOrNoLen(payload) ||
        check.isUndefinedOrNullOrEmptyOrNoLen(payload.userId) ||
        check.isUndefinedOrNullOrEmptyOrNoLen(payload.secret) ||
        payload.secret != config.ENV_MIX) {
        res.status(401);
        return res.send({
            status: false,
            reason: 'Authentication failed'
        });
    }

    req.userId = payload.userId;
    callback(token);
}