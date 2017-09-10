var express = require('express');
var router = express.Router();
var path = require('path');
var loginCtrl = require(path.join(__dirname, '..', '..', 'controller', 'user', 'loginCtrl'));
var passport = require('passport');
var loginCheck = require(path.join(__dirname, '..', '..', 'controller', 'user', 'loginCheckService'));
var mailUser = require(path.join(__dirname, '..', '..', 'controller', 'user', 'mailUser'));
var jwt = require(path.join(__dirname, '..', '..', 'service', 'util', 'jwt'));
var mongoose = require('mongoose');
var adminModel = mongoose.model('admin');

/**
 * create a new user in data base
 * checkUserExists :- checks if username for that user already exixts
 * checkPhoneExists :- checks if user's phonw number already exixts in database
 * createUser :- create the final user
 *
 * BODY : {name,username,password,phoneNo}
 * RES : {status,reason}
 */
router.post('/create',
    loginCtrl.checkUserExists,
    loginCtrl.checkPhoneExists,
    loginCtrl.createUser,
    mailUser.sendWelcomeMail
);

/**
 * checks if a username already exixts
 * mainly for front end checking
 *
 * BODY : {username}
 * RES : {status}
 */
router.post('/checkUserValidity', loginCheck.checkUserExists);

/**
 * check if phone number already exixts or not
 * for front end checking
 * BODY : {phoneNo}
 * RES : {status}
 */
router.post('/checkPhoneValidity', loginCheck.checkPhoneExists);


/**
 * logs in a user
 *
 * BODY {username,password}
 * RES {status,reason/token, name,username,userimage}
 */
router.post('/login', loginCtrl.authorize);

//Clear user session entry from session db
router.post('/logout', function(req, res, next) {
    jwt.removeToken(req, res, function() {
        res.status(200);
        res.send({
            status: true
        });
    });
});


/**
 * change a username for a user
 * BODY {oldusername,newusername}
 *
 */
router.post('/changeUsername', loginCtrl.changeUsername, mailUser.sendMailChange)

/**
 * change a password for a user
 * BODY {oldPassword,newPassword}
 */
router.post('/changePassword', jwt.validateToken, loginCtrl.changePassword)

/**
 * change a password for a user through verified otp
 * BODY {phone,oldPassword,newPassword}
 */
router.post('/changePasswordPhone', loginCtrl.changePasswordPhone)

/**
 * change a password for a user through verified otp
 * BODY {username,oldPassword,newPassword}
 */
router.post('/changePasswordUsername', loginCtrl.changePasswordUsername)


/**
 * change a phone for a user
 * BODY {phone}
 */
router.post('/changePhone', jwt.validateToken, loginCtrl.changePhone)


/**
 * change name for a user
 * BODY {name}
 */
router.post('/changeName', jwt.validateToken, loginCtrl.changeName)

/**
 * delete a user
 * BODY {userId(optional) }
 */
router.post('/deleteUser', jwt.validateToken, loginCtrl.deleteUser)




router.post('/admin/login', passport.authenticate('local'), function(req, res, next) {
    res.json(req.user);
    return next();
})

router.get('/admin/login/sample', function(req, res, next) {
    adminModel.create({ username: "admin", password: "admin" }, function(err, data) {
        if (err) {
            return next(err);
        } else {
            res.send("created a sample user");
            return next();
        }
    })
})

router.get('/admin/loggedin', function(req, res) {
    res.send(req.isAuthenticated() ? req.user : '0');
});

router.post('/admin/logout', function(req, res) {
    req.logOut();
    res.send(200);
})


router.get('/admin/sample', function(req, res) {
    var user = {
        username: "admin",
        password: "admin"
    }
    req.logIn(user, function(err) {
        if (err) {
            return next(err);
        }
        res.send("logged in");
    });
})

router.post('/test', jwt.validateToken, loginCtrl.test);

module.exports = router;