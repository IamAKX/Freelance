var path = require('path');
var mongoose = require('mongoose');
var userModel = mongoose.model('user');
var projModel = mongoose.model('project');
var check = require(path.join(__dirname, '..', 'util', 'checkValidObject'));
var _ = require('lodash');

exports.getProjectDetails = function(data, id, cb) {
    var userIds = data.participant.split(',');
    var participants = [];

    _.forEach(userIds, function(id) {
        participants.push({
            userId: id,
            status: 'n/a',
            comment: ''
        })
    })

    var duration = {
        start: data.startDate,
        end: data.endDate,
        type: data.type,
        deadline: data.deadline
    }

    var projectObj = {
        userId: id,
        name: data.name,
        description: data.description,
        duration: duration,
        participant: participants,
        category: data.category
    }
    cb(projectObj);
}

exports.getNotificationData = function(data, userId, pid, cb) {
    userModel.findOne({ _id: userId }, { name: 1, image: 1 }, function(err, userData) {
        var title = "Freelance";
        var body = {
            name: data.name,
            description: data.description,
            duration: {
                startDate: data.startDate,
                endDate: data.endDate,
                type: data.type
            },
            id: pid,
            employer: {
                userId: userId,
                name: userData.name,
                image: userData.image
            },
            category: data.category
        };
        var obj = { title: title, body: body }
        return cb(obj);
    })

}

exports.checkProjectCreator = function(req, res, next) {
    var userId = req.userId;
    var projectId = req.body.projectId;
    if (!check.isUndefinedOrNull(userId)) {
        projModel.findOne({ _id: projectId }, { userId: 1 }, function(err, data) {
            if (check.isUndefinedOrNull(data)) {
                res.status(500);
                res.send({
                    status: false,
                    reason: "Project Not Found",
                });
                return next();
            } else if (data.userId == userId) {
                next();
            } else {
                res.status(500);
                res.send({
                    status: false,
                    reason: "You Have No Access To The Project",
                });
                return next();
            }
        })
    } else {
        res.status(500);
        res.send({
            status: false,
            reason: "Error fetching data",
        });
        return next();
    }

}

exports.checkWorkerAccess = function(req, res, next) {
    var userId = req.userId;
    var projectId = req.body.projectId;
    projModel.findOne({ _id: projectId }, { participant: 1, _id: 0 }, function(err, data) {

        var foundIndex = _.findIndex(data.participant, function(part) {
            return part.userId == userId
        })
        if (foundIndex == -1) {
            res.status(500);
            res.send({
                status: false,
                reason: "You are not a worker",
            });
            return next();
        } else {
            next();
        }
    })
}

exports.getUserDetails = function(ids, callback) {
    userModel.find({ _id: { $in: ids } }, { userName: 1, phone: 1, category: 1, experience: 1, qualification: 1 },
        function(err, data) {
            if (err) {
                callback(err, null);
            } else {
                callback(null, data)
            }
        })
}