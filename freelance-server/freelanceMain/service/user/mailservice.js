var nodemailer = require('nodemailer');
var path = require('path');
var config = require(path.join(__dirname, '..', '..', 'bin', 'config'));


var transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: config.mailuser,
        pass: config.mailpwd
    }
});


exports.sendMail = function(toEmail, fromEmail, subject, body, cb) {
    var mailOptions = {
        from: fromEmail,
        to: toEmail,
        subject: subject,
        html: body
    };

    transporter.sendMail(mailOptions, function(error, info) {
        if (error) {
            cb(error, null);
        }
        cb(null, info);
    });
}