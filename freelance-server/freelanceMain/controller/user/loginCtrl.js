var mongoose = require('mongoose');
var userModel = mongoose.model('user');
var projModel = mongoose.model('project');

var path = require('path');
var async = require('async');
var check = require(path.join(__dirname, '..', '..', 'service', 'util', 'checkValidObject'));
var login = require(path.join(__dirname, '..', '..', 'service', 'login', 'login'));
var jwtService = require(path.join(__dirname, '..', '..', 'service', 'util', 'jwt'));
var hashService = require(path.join(__dirname, '..', '..', 'service', 'util', 'encryption'));

//check if a user already exists
exports.checkUserExists = function(req, res, next) {
    userModel.findOne({ 'userName': req.body.username }, function(err, data) {
        if (err) {
            return next(err);
        } else {
            if (check.isNull(data)) {
                next();
            } else {
                res.status(500);
                var obj = {
                    status: false,
                    reason: 'username exixts'
                };
                res.send(obj);
                return next(obj);
            }
        }
    });
};

//check if a phonenumber already exixts
exports.checkPhoneExists = function(req, res, next) {
    userModel.findOne({ 'phone': req.body.phoneNo }, function(err, data) {
        if (err) {
            return next(err);
        } else {
            if (check.isNull(data)) {
                next();
            } else {
                res.status(500);
                var obj = {
                    status: false,
                    reason: 'phoneNo exixts'
                };
                res.send(obj);
                return next(obj);
            }
        }
    });
};

//creates a user
exports.createUser = function(req, res, next) {
    var profile = req.body;
    var user = login.getNewUser(profile);
    userModel.create(user, function(err, data) {
        if (err) {
            return next(err);
        } else {
            next();
        }
    });

};

//authorize a user during login
exports.authorize = function(req, res, next) {
    var user = req.body;
    userModel.findOne({ 'userName': user.username }, function(err, data) {
        if (err) {
            return next(err);
        } else if (check.isNull(data)) {
            res.status(500);
            res.send({
                status: "false",
                reason: "wrong username or password"
            });
            return next();
        } else {
            if (login.matchUser(user, data)) {
                if (!data.active) {
                    res.status(500);
                    res.send({ status: false, reason: 'Email not verified' });
                    return next();
                } else if (data.suspend) {
                    res.status(500);
                    res.send({ status: false, reason: 'You are suspended .Conatact the admin' });
                    return next();
                } else {
                    jwtService.createToken(data, function(err, token) {
                        res.status(200);
                        res.send({
                            status: true,
                            token: token,
                            name: data.name,
                            username: data.userName,
                            userimage: data.image,
                            mode: data.mode,
                            catergory: data.category
                        });
                        return next();
                    });
                }
            } else {
                res.status(200);
                res.send({
                    status: false,
                    reason: 'wrong username or password'
                });
                return next();
            }
        }
    });
};

exports.test = function(req, res, next) {
    res.status(200);
    res.send(" HELLO " + req.name);
    return next();
};

//change a user's username
exports.changeUsername = function(req, res, next) {
    var olduser = req.body.oldUsername;
    var newUser = req.body.newUsername;
    if (check.isUndefined(olduser) || check.isUndefined(newUser)) {
        errMsg("invalid Body");
    }
    userModel.findOne({ userName: newUser }, { _id: 1 }, function(err, data) {

        if (check.isNull(data)) {
            next();
        } else {
            errMsg("username already exists");
        }
    })

    function errMsg(err) {
        res.status(500);
        res.send({ status: false, reason: err });
        return next();
    }
}


//change a user's password
exports.changePassword = function(req, res, next) {
    var oldPassword = req.body.oldPassword;
    var newPassword = req.body.newPassword;
    var user = {
        password: oldPassword
    }

    if (check.isUndefined(oldPassword) || check.isUndefined(newPassword)) {
        errMsg("invalid Body");
    } else if (oldPassword == newPassword) {
        errMsg("old password and new passeord cant be same");
    }

    userModel.findOne({ _id: req.userId }, function(err, data) {
        if (login.matchUser(user, data)) {
            var newhashPwd = hashService.hashPwd(data.salt, newPassword);
            userModel.update({ _id: req.userId }, { $set: { hashPwd: newhashPwd } }, function(err, data) {
                if (err) {
                    errMsg(err);
                } else {
                    res.status(200);
                    res.send({
                        status: true
                    });
                    return next();
                }
            })
        } else {
            errMsg("old password is incorrect");
        }
    })

    function errMsg(err) {
        res.status(500);
        res.send({ status: false, reason: err });
        return next();
    }
}

//change a user's password through phonenumber
exports.changePasswordPhone = function(req, res, next) {
    var password = req.body.password;
    // var newPassword = req.body.newPassword;
    // var user = {
    //     password: oldPassword
    // }

    if (check.isUndefined(password) || check.isUndefined(req.body.phone)) {
        errMsg("invalid Body");
    }

    userModel.findOne({ phone: req.body.phone }, function(err, data) {
        if (!check.isNull(data)) {
            var newhashPwd = hashService.hashPwd(data.salt, password);
            userModel.update({ phone: req.body.phone }, { $set: { hashPwd: newhashPwd } }, function(err, data) {
                if (err) {
                    errMsg(err);
                } else {
                    res.status(200);
                    res.send({
                        status: true
                    });
                    return next();
                }
            });
        } else {
            errMsg("invalid user");
        }
    })

    function errMsg(err) {
        res.status(500);
        res.send({ status: false, reason: err });
        return next();
    }
}

//change a user's password through username
exports.changePasswordUsername = function(req, res, next) {
    // var oldPassword = req.body.oldPassword;
    var newPassword = req.body.password;
    // var user = {
    //     password: oldPassword
    // }

    // if (check.isUndefined(oldPassword) || check.isUndefined(newPassword) || check.isUndefined(req.body.phone)) {
    //     errMsg("invalid Body");
    // } else if (oldPassword == newPassword) {
    //     errMsg("old password and new passeord cant be same");
    // }

    userModel.findOne({ userName: req.body.username }, function(err, data) {
        if (!check.isNull(data)) {
            var newhashPwd = hashService.hashPwd(data.salt, newPassword);
            userModel.update({ userName: req.body.username }, { $set: { hashPwd: newhashPwd } }, function(err, data) {
                if (err) {
                    errMsg(err);
                } else {
                    res.status(200);
                    res.send({
                        status: true
                    });
                    return next();
                }
            })
        } else {
            errMsg("invalid username");
        }
    })

    function errMsg(err) {
        res.status(500);
        res.send({ status: false, reason: err });
        return next();
    }
}

//change a user's phone number
exports.changePhone = function(req, res, next) {
    var newPhoneNumber = req.body.phone;
    userModel.update({ _id: req.userId }, { $set: { phone: newPhoneNumber } }, function(err, data) {
        if (err) {
            errMsg(err);
        } else {
            res.status(200);
            res.send({
                status: true
            });
            return next();
        }
    })

    function errMsg(err) {
        res.status(500);
        res.send({ status: false, reason: err });
        return next();
    }
}

//delete a user
exports.deleteUser = function(req, res, next) {
    if (check.isUndefined(req.userId) && check.isUndefined(req.body.userId)) {
        errMsg("No userId found");
    }
    var userId = check.isUndefined(req.userId) ? req.body.userId : req.userId;
    userModel.remove({ _id: userId }, function(err, data) {
        if (err) {
            errMsg("error deleting user");
        } else {
            projModel.remove({ userId: req.userId }, function(err, data) {
                if (err) {
                    errMsg("error deleting user");
                }
                res.status(200);
                res.send({
                    status: true
                });
                return next();
            })
        }
    })

    function errMsg(err) {
        res.status(500);
        res.send({ status: false, reason: err });
        return next();
    }
}

exports.changeName = function(req, res, next) {
    var newName = req.body.name;
    userModel.update({ _id: req.userId }, { $set: { name: newName } }, function(err, data) {
        if (err) {
            errMsg(err);
        } else {
            res.status(200);
            res.send({
                status: true
            });
            return next();
        }
    })

    function errMsg(err) {
        res.status(500);
        res.send({ status: false, reason: err });
        return next();
    }
}