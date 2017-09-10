var path = require('path');
var mongoose = require('mongoose');
var _ = require('lodash');
var projModel = mongoose.model('project');
var userModel = mongoose.model('user');
var projService = require(path.join(__dirname, '..', '..', 'service', 'project', 'projService'));
var gcmService = require(path.join(__dirname, '..', '..', 'service', 'project', 'gcmMessage'));
var check = require(path.join(__dirname, '..', '..', 'service', 'util', 'checkValidObject'));


/**
 * post a project
 */
exports.postProject = function(req, res, next) {
    projService.getProjectDetails(req.body, req.userId, function(projectData) {
        projModel.create(projectData, function(err, data) {
            if (err) {
                res.status(500);
                return res.send({
                    status: false,
                    reason: err,
                });
                return next(err);
            } else {
                req.projectId = data._id;
                next();
            }
        })
    })
}

/**
 * notify all the freelancer for the project
 */
exports.notify = function(req, res, next) {
    token = req.body.participant.split(',');
    gcmService.getTokens(token, function(gcmTokens) {
        projService.getNotificationData(req.body, req.userId, req.projectId, function(notficationData) {
            gcmService.sendMessageToMultiple(notficationData.title, notficationData.body, gcmTokens, function(err, data) {
                if (err) {
                    console.log(err);
                }
                res.status(200);
                return res.send({ status: true, response: data, error: err, id: req.projectId });
                return next();
            })
        });

    })
}

/**
 * check the hire mode of a user 
 */
exports.checkHireMode = function(req, res, next) {
    var userId = req.userId;
    userModel.findOne({ _id: userId }, { mode: 1, _id: 0 }, modeCallback);

    function modeCallback(err, data) {
        if (err) {
            res.status(500);
            return res.send({
                status: false,
                reason: err
            });
            return next(err);
        } else {
            if (data.mode == 'hire' || data.mode == 'Hire') {
                next();
            } else {
                res.status(500);
                return res.send({
                    status: false,
                    reason: "You Are Not In Hire Mode"
                });
                return next(err);
            }
        }
    }
}

/**
 * check the work mode of a user
 */
exports.checkWorkMode = function(req, res, next) {
    var userId = req.userId;
    userModel.findOne({ _id: userId }, { mode: 1, _id: 0 }, modeCallback);

    function modeCallback(err, data) {
        if (err) {
            res.status(500);
            return res.send({
                status: false,
                reason: err
            });
            return next(err);
        } else {
            if (data.mode == 'work') {
                next();
            } else {
                res.status(500);
                return res.send({
                    status: false,
                    reason: "You Are Not In Work Mode"
                });
                return next(err);
            }
        }
    }
}

/**
 * get all projects posted for employer side
 */
exports.getAllProjects = function(req, res, next) {
    projModel.find({ userId: req.userId }, { name: 1, description: 1, projectStatus: 1, 'participant.userId': 1, category: 1, duration: 1 }, projectsCallback)

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
                        description: '',
                        _id: ''
                    };
                    _.forEach(proj.participant, function(p) {
                        var founduser = _.find(userData, function(u) {
                            return u._id == p.userId;
                        })
                        if (!check.isUndefined(founduser)) {
                            singleproj.participant.push(founduser)
                        }
                    })
                    singleproj._id = proj._id;
                    singleproj.name = proj.name;
                    singleproj.category = proj.category;
                    singleproj.description = proj.description;
                    singleproj.duration = proj.duration;
                    singleproj.projectStatus = proj.projectStatus;
                    result.push(singleproj);

                })

                res.status(200);
                res.send({ projects: result });
                return next();
            })

        }
    }
}

/**
 * check a user access for project
 */
exports.getProjectDetails = function(req, res, next) {
    projModel.findOne({ userId: req.userId, _id: req.body.projectId }, { _id: 0 }, projectsCallback)

    function projectsCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: false,
                reason: "Error fetching project details"
            });
            return next(err);
        } else {
            res.status(200);
            res.send(data);
            return next();
        }
    }
}

/**
 * when a worker accepts a project
 */
exports.acceptProject = function(req, res, next) {
    projModel.update({ _id: req.body.projectId, 'participant.userId': req.userId }, { $set: { 'participant.$.status': 'accept', 'participant.$.chatroomId': req.body.chatroomId } },
        updateCallback)

    function updateCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: false,
                reason: "Error accepting project"
            });
            return next(err);
        } else {
            res.status(200);
            res.send({
                status: true,
                chatroomId: req.body.chatroomId
            });
            return next();
        }
    }
}

/**
 * when a worker accepts a project
 */
exports.rejectProject = function(req, res, next) {
    projModel.update({ _id: req.body.projectId, 'participant.userId': req.userId }, { $set: { 'participant.$.status': 'reject' } },
        updateCallback)

    function updateCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: false,
                reason: "Error accepting project"
            });
            return next(err);
        } else {
            res.status(200);
            res.send({
                status: true
            });
            return next();
        }
    }
}

/**
 * all projects of a worker
 */
exports.allProjects = function(req, res, next) {
    projModel.find({ 'participant.userId': req.userId }, { name: 1, description: 1, createdAt: 1, duration: 1, category: 1, participant: 1, projectStatus: 1 }, projectsCallback);

    function projectsCallback(err, data) {
        _.forEach(data, function(proj) {
            proj.status = proj.participant[0].status;
        })
        res.send(data);
        return next();
    }

}

/**
 * when bidding ends call the change status
 */
exports.changeStatusProject = function(req, res, next) {
    projModel.update({ _id: req.body.projectId }, { $set: { projectStatus: 'done' } }, updateCallback);

    function updateCallback(err, data) {
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
            return next();
        }
    }
}

exports.changeStatusProjectTimeout = function(req, res, next) {
    projModel.update({ _id: req.body.projectId }, { $set: { projectStatus: 'timeout' } }, updateCallback);

    function updateCallback(err, data) {
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
            return next();
        }
    }
}

/**
 * get list of participants who have accepted
 */
exports.getAccepteds = function(req, res, next) {
    projModel.find({ _id: req.body.projectId }, { participant: 1, _id: 0 }, accpetCallback)

    function accpetCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: false,
                reason: "Error fetching participants"
            });
            return next(err);
        } else {
            var participant = _.filter(data[0].participant, function(o) { return o.status == 'accept' || o.status == 'hired' });
            participantIds = _.map(participant, 'userId')
            var participants = [];

            userModel.find({ _id: { $in: participantIds } }, { name: 1, image: 1 }, function(err, userData) {
                var result = [];
                _.forEach(userData, function(user) {
                    var chatroom = _.find(participant, function(p) { return p.userId == user._id });
                    result.push({
                        _id: user._id,
                        name: user.name,
                        image: user.image,
                        chatroomId: chatroom.chatroomId,
                        status: chatroom.status
                    });
                })
                res.status(200);
                res.send(result);
                return next();
            })
        }
    }
}

/**
 * get project details for worker side
 * req.body : {projectId }
 * res.body : {name,description,duration,{employerDetails}}
 */
exports.getProjectDetailsWorker = function(req, res, next) {
    projModel.findOne({ _id: req.body.projectId, 'participant.userId': req.userId }, { name: 1, description: 1, duration: 1, userId: 1, category: 1, projectStatus: 1, 'participant.$.chatroomId': 1 }, projectCallback)

    function projectCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({
                status: false,
                reason: "Error fetching participants"
            });
            return next(err);
        } else {
            userModel.findOne({ _id: data.userId }, { name: 1, image: 1, phone: 1 }, function(err, employerData) {
                var proj = {
                    name: data.name,
                    description: data.description,
                    duration: data.duration,
                    employer: employerData,
                    category: data.category,
                    projectStatus: data.projectStatus,
                    userStatus: data.participant[0].status,
                    chatroomId: data.participant[0].chatroomId
                }
                res.status(200);
                res.send(proj);
                return next();
            })
        }
    }
}