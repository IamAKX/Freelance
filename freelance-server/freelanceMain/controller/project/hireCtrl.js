var path = require('path');
var mongoose = require('mongoose');
var hireModel = mongoose.model('hire');
var projModel = mongoose.model('project');
var userModel = mongoose.model('user');
var check = require(path.join(__dirname, '..', '..', 'service', 'util', 'checkValidObject'));
var _ = require('lodash');

exports.hireUser = function(req, res, next) {
    var payload = {
        cData: req.body.cDate,
        aDate: req.body.aDate,
        projectId: req.body.projectId,
        employerId: req.userId,
        employeeId: req.body.employeeId,
        amount: req.body.amount,
        chatroomId: req.body.chatroomId
    }

    projModel.update({ _id: req.body.projectId, 'participant.userId': req.body.employeeId }, { $set: { 'participant.$.status': 'hired' } },
        updateCallback)

    function updateCallback(err, data) {

        hireModel.create(payload, function(err, data) {
            if (err) {
                res.status(500);
                res.send({
                    status: false,
                    reason: "Error saving hire data"
                });
                return next(err);
            } else {
                res.status(200);
                res.send({
                    status: true
                });
                return next(err);
            }
        })

    }
}

exports.hireDetailsEmployer = function(req, res, next) {
    hireModel.find({ employerId: req.userId }, function(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: false,
                reason: "Error fetching hire data"
            });
            return next(err);
        } else {
            res.status(200);
            res.send(data);
            return next(err);
        }
    })
}

exports.hireDetailsEmployee = function(req, res, next) {
    hireModel.find({ employeeId: req.userId }, function(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: false,
                reason: "Error fetching hire data"
            });
            return next(err);
        } else {
            res.status(200);
            res.send(data);
            return next(err);
        }
    })
}

exports.workerHiredProjects = function(req, res, next) {
    hireModel.find({ employeeId: req.userId, status: 'hired' }, function(err, hireData) {
        var userIds = _.map(hireData, 'employerId');
        userModel.find({ _id: { $in: userIds } }, { userName: 1, name: 1, image: 1, phone: 1 }, function(err, userData) {
            var result = [];
            _.forEach(hireData, function(hireSingleData) {
                var payload = {};
                var found = _.find(userData, function(u) {
                    return u._id == hireSingleData.employerId;
                })

                payload.projectId = hireSingleData.projectId;
                payload.employerId = hireSingleData.employerId;
                payload.aDate = hireSingleData.aDate;
                payload.amount = hireSingleData.amount;
                payload.chatroomId = hireSingleData.chatroomId;
                payload.createdAt = hireSingleData.createdAt;

                if (!check.isUndefined(found)) {
                    payload.employerName = found.name;
                    payload.employerUserName = found.userName;
                    payload.employerImage = found.image;
                    payload.phone = found.phone
                }
                result.push(payload);
            })
            res.status(200);
            res.send(result);
            return next();
        })
    })
}

exports.recentlyWorked = function(req, res, next) {
    hireModel.find({ employeeId: req.userId }, function(err, hireData) {
        var userIds = _.map(hireData, 'employerId');
        userModel.find({ _id: { $in: userIds } }, { name: 1, image: 1 }, function(err, userData) {
            res.status(200);
            res.send(userData);
            return next();
        })
    })
}

exports.getSumEarned = function(req, res, next) {
    hireModel.aggregate([{ $match: { employeeId: req.userId } }, {
        $group: {
            _id: null,
            total: {
                $sum: "$amount"
            }
        }
    }], function(err, data) {
        res.send(data);
        return next();
    });
}

exports.doneWorking = function(req, res, next) {
    hireModel.update({ employeeId: req.userId, projectId: req.body.projectId }, { $set: { status: 'done' } }, function(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: false,
                reason: "Error changing status"
            });
            return next(err);
        } else {
            res.status(200);
            res.send({
                status: true
            });
            return next(err);
        }
    })
}