var mongoose = require('mongoose');
var path = require('path');
var config = require(path.join(__dirname, '..', 'bin', 'config'));

var userModel = require(path.join(__dirname, '..', 'model', 'user', 'users'));
var otpModel = require(path.join(__dirname, '..', 'model', 'otp'));
var apiModel = require(path.join(__dirname, '..', 'model', 'api', 'api'));
var apiUpdateModel = require(path.join(__dirname, '..', 'model', 'api', 'apiUpdate'));
var constantCatModel = require(path.join(__dirname, '..', 'model', 'constant', 'categories'));
var constantModel = require(path.join(__dirname, '..', 'model', 'constant', 'constant'));
var appModel = require(path.join(__dirname, '..', 'model', 'api', 'app'));
var projectModel = require(path.join(__dirname, '..', 'model', 'projects', 'project'));
var chatroom = require(path.join(__dirname, '..', 'model', 'projects', 'chatroom'));
var hire = require(path.join(__dirname, '..', 'model', 'projects', 'hire'));
var reportModel = require(path.join(__dirname, '..', 'model', 'user', 'report'));
var feedbackModel = require(path.join(__dirname, '..', 'model', 'user', 'feedback'));
var adminModel = require(path.join(__dirname, '..', 'model', 'user', 'admin'));

exports.initDb = function() {
    mongoose.connect(config.mongourl);
    var db = mongoose.connection;
    db.on('error', console.error.bind(console, 'connection error...'));
    db.once('open', function callback() {
        console.log('freelance mongoDb connected');
    });
};