var redis = require("redis");
var path = require("path");
var redisDb = require(path.join(__dirname, '..', '..', 'global', 'redis'));
var config = require(path.join(__dirname, '..', '..', 'bin', 'config'));
var redisTokenExpTime = config.userExp;

/**
 * saving the redis token for a user 
 * when loggin in a database
 */
exports.saveToken = function(token, user, callback) {
    //After successful sign in save userid/token in the Redis db
    var userInfo = {
        token: token,
        username: user.userName,
        name: user.name
    };

    redisDb.redisClient.set(user.id, JSON.stringify(userInfo), function(err, reply) {
        if (err) {
            callback(err, null);
        } else {
            //Login expires in 1 hours
            redisDb.redisClient.expire(user.id, redisTokenExpTime, function(err, reply) {
                if (err) {
                    callback(err, null);
                } else {
                    callback(null, token);
                }
            });
        }
    });
};


exports.getToken = function(req, res, userId, incomingToken, next) {
    //Get saved token from the Redis db and validate
    redisDb.redisClient.get(userId, function(err, userInfo) {
        if (err) {
            res.status(500);
            return res.send({
                status: false,
                reason: 'Internal Server Error'
            });
        }
        if (!userInfo) {
            res.status(401);
            return res.send({
                status: false,
                reason: 'Authentication Failed'
            });
        } else {
            var userInfoJSON = JSON.parse(userInfo);
            var token = userInfoJSON.token;
            if (!token || token !== incomingToken) {
                res.status(401);
                return res.send({
                    status: false,
                    reason: 'Authentication Failed'
                });
            }

            //Reset key for another 1 hour
            redisDb.redisClient.expire(userId, redisTokenExpTime, function(err, reply) {
                if (err) {
                    res.status(500);
                    return res.send({
                        status: false,
                        reason: 'Internal Server Error'
                    });
                }

                req.username = userInfoJSON.username;
                req.name = userInfoJSON.name;
                next();
            });
        }
    });
};

//Remove saved token during logout
exports.removeToken = function(res, userId, next) {
    redisDb.redisClient.del(userId, function(err, reply) {
        if (err) {
            res.status(500);
            return res.send({
                message: 'Internal Server Error'
            });
        }
        if (next)
            next();
    });

    return;
}