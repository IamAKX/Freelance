var path = require('path');
var mongoose = require('mongoose');
var chatModel = mongoose.model('chat');
var userModel = mongoose.model('user');
var projModel = mongoose.model('project');
var check = require(path.join(__dirname, '..', '..', 'service', 'util', 'checkValidObject'));
var _ = require('lodash')

exports.setChatroomId = function(req, res, next) {

    var payload = {
        projectId: req.body.projectId,
        chatroomId: req.body.chatroomId
    }
    chatModel.create(payload, function(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: "false",
                reason: "error setting chatroom Id"
            });
            return next();
        } else {
            res.status(200);
            res.send({
                status: true
            });
            return next();
        }
    })
}

exports.getChatroomData = function(req, res, next) {
    chatModel.find({ participants: { $in: [req.userId] } }, function(err, data) {
        var temp = [];
        var friendsArray = [],
            result = [];

        _.forEach(data, function(chatroom) {
            temp.push({
                chatroomId: chatroom._id,
                other: _.find(chatroom.participants, function(f) {
                    return f != req.userId
                })
            })
        })
        friendsArray = _.map(temp, 'other');
        userModel.find({ _id: { $in: friendsArray } }, { image: 1, name: 1, userName: 1, phone: 1 }, function(err, userData) {
            _.forEach(userData, function(user) {
                var foundUser = _.find(temp, function(u) {
                    return u.other == user._id;
                })
                result.push({
                    userId: user._id,
                    chatroomId: foundUser.chatroomId,
                    image: user.image,
                    name: user.name,
                    username: user.userName,
                    phone: user.phone
                })
            })
            res.send(result);
            return next();
        })
    })
}

exports.deleteChatroomId = function(req, res, next) {
    chatModel.remove({ projectId: req.body.projectId }, function(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: "false",
                reason: "error deleting chatroom Id"
            });
            return next();
        } else {
            res.status(200);
            res.send(true);
            return next();
        }
    })
}

exports.createChatroom = function(req, res, next) {
    projModel.findOne({ _id: req.body.projectId }, { userId: 1, _id: 0 }, function(err, data) {
        var hostId = data.userId;
        var userId = req.userId;
        var participant = [hostId, userId];
        chatModel.findOne({ participants: { $all: participant } }, function(err, data) {
            if (check.isUndefinedOrNull(data)) {
                chatModel.create({ participants: participant }, function(err, chatroomData) {
                    req.body.chatroomId = chatroomData._id;
                    next();
                })
            } else {
                req.body.chatroomId = data._id;
                next();
            }
        })
    })
}