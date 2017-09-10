var path = require('path');
var config = require(path.join(__dirname, '..', '..', 'bin', 'config'));
var mailService = require(path.join(__dirname, '..', '..', 'service', 'user', 'mailservice'));
var check = require(path.join(__dirname, '..', '..', 'service', 'util', 'checkValidObject'));
var moment = require('moment');
var jwt = require('jwt-simple');
var mongoose = require('mongoose');
var userModel = mongoose.model('user');

//during account creation
function getCbUrl(email, phoneNo) {
    var payload = {
        sub: {
            email: email,
            phoneNo: phoneNo
        },
        exp: moment(new Date()).add(config.emailVerifyExpTime, 'minutes').valueOf()
    };
    var token = jwt.encode(payload, config.EMAIL_SECRET);
    var cbUrl = config.EMAIL_CB_VERIFY + token;
    return cbUrl;
}

//during account email change
function getCbUrlEmailChange(newEmail, oldEmail) {
    var payload = {
        sub: {
            newEmail: newEmail,
            oldEmail: oldEmail
        },
        exp: moment(new Date()).add(config.emailVerifyExpTime, 'minutes').valueOf()
    };
    var token = jwt.encode(payload, config.EMAIL_SECRET);
    var cbUrl = config.EMAIL_CB_VERIFY_CHANGE + token;
    return cbUrl;
}

//mail during signup
exports.sendWelcomeMail = function(req, res, next) {
    var toEmail = req.body.username;
    var fromEmail = config.adminEmail;
    var cbUrl = getCbUrl(req.body.username, req.body.phoneNo);
    var subject = "FreeLance Account Created";
    var body = "<h2> Hello! " + req.body.name + " ,</h2><p>Thanks for creating an account at freelancer . Hope you are doing well . Please Click on the link below to verify your EmailId .</p>"
    body += '<h3><a href="' + cbUrl + '"> Click Here </a><h3>';
    body += '<p><b>Note: </b>Email will expire after ' + (config.emailVerifyExpTime / 60) + ' hrs</p>'

    mailService.sendMail(toEmail, fromEmail, subject, body, function(err, data) {
        if (err) {
            console.log(err);
        }
        res.status(200);
        res.send({
            status: true
        });
        return next();
    })
}

//mail during email change
exports.sendMailChange = function(req, res, next) {
    var toEmail = req.body.newUsername;
    var fromEmail = config.adminEmail;
    var cbUrl = getCbUrlEmailChange(req.body.newUsername, req.body.oldUsername);
    var subject = "FreeLance Account Created";
    var body = "<h2> Hello User! ,</h2><p>There's a request for change in your emailId  . Please Click on the link below to verify your EmailId .</p>"
    body += '<h3><a href="' + cbUrl + '"> Click Here </a><h3>';
    body += '<p><b>Note: </b>Email will expire after ' + (config.emailVerifyExpTime / 60) + ' hrs</p>'

    mailService.sendMail(toEmail, fromEmail, subject, body, function(err, data) {
        if (err) {
            console.log(err);
        }
        res.status(200);
        res.send({
            status: true
        });
        return next();
    })
}

exports.handlerAccountVerify = function(req, res, next) {
    var token = req.query.token;
    var payload = jwt.decode(token, config.EMAIL_SECRET);

    var email = payload.sub.email;
    var phoneNo = payload.sub.phoneNo;
    if (!email || payload.exp <= Date.now()) {
        res.status(200);
        res.send("EMAIL VERIFICATION FAILED");
        return next();
    }
    var query = { userName: email, phone: phoneNo };
    userModel.findOne(query, { active: 1, _id: 0 },
        function(err, data) {
            if (data.active) {
                res.status(200);
                res.send("EMAIL ALREADY VERIFIED");
                return next();
            } else {
                userModel.update(query, { $set: { active: true } }, function(err, updateData) {
                    res.status(200);
                    res.send("EMAIL SUCCESSFULLY VERIFIED");
                    return next();
                })
            }

        })
}

exports.handlerAccountVerifyChange = function(req, res, next) {
    var token = req.query.token;
    var payload = jwt.decode(token, config.EMAIL_SECRET);

    var newEmail = payload.sub.newEmail;
    var oldEmail = payload.sub.oldEmail;

    if (!newEmail || !oldEmail || payload.exp <= Date.now()) {
        res.status(200);
        res.send("EMAIL VERIFICATION FAILED");
        return next();
    }
    var query = { userName: oldEmail };
    userModel.findOne(query, { _id: 0 },
        function(err, data) {
            if (!check.isUndefinedOrNull(data)) {
                userModel.update(query, { $set: { userName: newEmail } }, function(err, updateData) {
                    res.status(200);
                    res.send("EMAIL SUCCESSFULLY CHAGED");
                    return next();
                })
            } else {
                res.status(200);
                res.send("EMAIL ALREADY VERIFIED");
                return next();
            }

        })
}

exports.handlerAccountVerifyFromApp = function(req, res, next) {
    var token = req.body.token;
    var payload = jwt.decode(token, config.EMAIL_SECRET);

    var email = payload.sub.email;
    var phoneNo = payload.sub.phoneNo;
    if (!email || payload.exp <= Date.now()) {
        res.status(200);
        res.send("EMAIL VERIFICATION FAILED");
        return next();
    }
    var query = { userName: email, phone: phoneNo };
    userModel.findOne(query, { active: 1, _id: 0 },
        function(err, data) {
            if (data.active) {
                res.status(200);
                res.send("EMAIL ALREADY VERIFIED");
                return next();
            } else {
                userModel.update(query, { $set: { active: true } }, function(err, updateData) {
                    res.status(200);
                    res.send("EMAIL SUCCESSFULLY VERIFIED");
                    return next();
                })
            }

        })
}

exports.resendEmailVerify = function(req, res, next) {
    userModel.findOne({ userName: req.body.username }, { userName: 1, phone: 1, name: 1, _id: 0 }, function(err, data) {
        if (!check.isUndefinedOrNullOrEmpty(data)) {
            var toEmail = req.body.username;
            var fromEmail = config.adminEmail;
            var cbUrl = getCbUrl(req.body.username, data.phone);
            var subject = "Verify mail";
            var body = "<h2> Hello! " + data.name + " ,</h2><p>Here is a link to Verify your mail</p>"
            body += '<h3><a href="' + cbUrl + '"> Click Here </a><h3>';
            body += '<p><b>Note: </b>Email will expire after ' + (config.emailVerifyExpTime / 60) + ' hrs</p>'
            mailService.sendMail(toEmail, fromEmail, subject, body, function(err, data) {
                if (err) {
                    console.log(err);
                }

                res.status(200);
                res.send({
                    status: true
                });
            })
        } else {
            res.status(500);
            res.send({ stats: false, reason: "email " + req.body.username + " not found" });
            return next();
        }
    })

}