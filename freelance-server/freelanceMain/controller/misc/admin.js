var path = require('path');
var mongoose = require('mongoose');
var userModel = mongoose.model('user');
var projModel = mongoose.model('project');
var hireModel = mongoose.model('hire');
var reportModel = mongoose.model('report');

var check = require(path.join(__dirname, '..', '..', 'service', 'util', 'checkValidObject'));
var redisService = require(path.join(__dirname, '..', '..', 'service', 'util', 'redisToken'))

var async = require('async');
var _ = require('lodash');
var ObjectId = require('mongoose').Types.ObjectId;

var filter = {
    _id: 1,
    name: 1,
    phone: 1,
    category: 1,
    image: 1,
    userName: 1,
    certified: 1,
    active: 1
}
var sort = "_id";



exports.getAllUser = function(req, res, next) {
    userModel.find({}, filter, function(err, data) {
        if (err) {
            res.send(err);
            return next();
        }
        res.status(200);
        res.send(data);
        return next();
    })
}

exports.getUserDetails = function(req, res, next) {
    userModel.findOne({ _id: req.body.userId }, function(err, data) {
        if (err) {
            return next(err);
        }
        res.send(data);
        return next();
    })
}

exports.getAllProjectsUser = function(req, res, next) {
    projModel.find({ userId: req.body.userId }, { name: 1, description: 1, 'participant.userId': 1, category: 1 }, projectsCallback)

    function projectsCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: false,
                reason: "Error fetching projects"
            });
            return next(err);
        } else {

            var userIds = (_.uniq(_.map([].concat.apply([], _.map(data, 'participant')), 'userId')));
            userModel.find({ _id: { $in: userIds } }, { _id: 1, name: 1, image: 1 }, function(err, userData) {
                var result = [];
                _.forEach(data, function(proj) {
                    var singleproj = {
                        participant: [],
                        name: '',
                        category: '',
                        description: ''
                    };
                    _.forEach(proj.participant, function(p) {
                        var founduser = _.find(userData, function(u) {
                            return u._id == p.userId;
                        })
                        if (!check.isUndefined(founduser)) {
                            singleproj.participant.push(founduser)
                        }
                    })
                    singleproj.name = proj.name;
                    singleproj.category = proj.category;
                    singleproj.description = proj.description;
                    result.push(singleproj);

                })

                res.status(200);
                res.send({ projects: result });
                return next();
            })

        }
    }
};

exports.changeUserData = function(req, res, next) {
    var param = req.body.param;
    var change = {
        $set: {
            [param]: req.body.value
        }
    };
    userModel.update({ _id: req.body.userId }, change, function(err, data) {
        if (err) {
            res.next(err);
        }
        res.status(200);
        res.send(data);
        return next();
    })
}

exports.checkSuspend = function(req, res, next) {

    if (req.body.param == 'suspend') {
        redisService.removeToken(res, req.body.userId, next);
    } else {
        next();
    }

}

exports.getAllProjects = function(req, res, next) {
    projModel.find({}, { name: 1, description: 1, projectStatus: 1, category: 1 }, function(err, data) {
        if (err) {
            return next(err);
        } else {
            res.status(200);
            res.send(data);
            return next();
        }
    })
}

exports.getProjectDetails = function(req, res, next) {
    projModel.findOne({ _id: req.body.pId }, function(err, pdata) {
        var projectData = pdata;
        userModel.findOne({ _id: pdata.userId }, function(err, uData) {
            var userData = userData;

            var result = {};
            result.userId = pdata.userId;
            if (!check.isNull(uData)) {
                result.name = uData.name;
            }

            result.projectName = pdata.name;
            result.description = pdata.description;
            result.duration = pdata.duration;
            result.category = pdata.category;
            result.createdAt = pdata.createdAt;
            res.projectStatus = pdata.projectStatus;

            var participantIds = _.map(pdata.participant, 'userId');
            var participants = [];
            userModel.find({ _id: { $in: participantIds } }, { image: 1, userName: 1, name: 1 }, function(err, partData) {

                _.forEach(pdata.participant, function(part) {
                    var founduser = _.find(partData, function(p) {
                        return p._id == part.userId;
                    })
                    if (!check.isUndefined(founduser)) {
                        participants.push({
                            _id: part.userId,
                            image: founduser.image,
                            name: founduser.name,
                            userName: founduser.userName,
                            status: part.status
                        })
                    }
                })
                result.participant = participants;
                res.send(result);
                return next();
            })
        });
    })
}

exports.getAllHire = function(req, res, next) {
    hireModel.find({}, function(err, data) {
        if (err) {
            return next(err);
        } else {
            res.status(200);
            res.send(data);
            return next();
        }
    })
}

exports.getAllReports = function(req, res, next) {
    reportModel.find({}, function(err, data) {
        if (err) {
            errFetching("error fetching");
        }

        res.status(200);
        res.send(data);
        return next();
    })

    function errFetching(reason) {
        status(500);
        res.send({ status: false, reason: reason });
        return next();
    }
}