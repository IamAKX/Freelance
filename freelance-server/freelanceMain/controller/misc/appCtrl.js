var path = require('path');
var mongoose = require('mongoose');
var appModel = mongoose.model('app');

exports.uploadApp = function(req, res, next) {
    var app = {
        name: req.body.name,
        size: req.body.size,
        link: req.body.link,
        comment: ''
    }
    appModel.create(app, function(err, data) {
        if (err) {
            return next(err);
        } else {
            res.status(200);
            res.send(true);
            return next();
        }
    })

}

exports.getUploadedApp = function(req, res, next) {
    appModel.find({}, function(err, data) {
        if (err) {
            return next(err);
        } else {
            res.status(200);
            res.send(data);
            return next();
        }
    })
}

exports.getUploadedAppVersions = function(req, res, next) {
    appModel.find({}, function(err, data) {
        if (err) {
            return next(err);
        } else {
            res.status(200);
            res.send(data);
            return next();
        }
    })
}

exports.deleteApp = function(req, res, next) {
    appModel.remove({ _id: req.body.appId }, function(err, data) {
        if (err) {
            return next(err);
        } else {
            res.status(200);
            res.send(true);
            return next();
        }
    })
}

exports.updateApp = function(req, res, next) {
    var updateData = {
        $set: {
            name: req.body.name,
            version: req.body.version,
            comment: req.body.comment
        }
    }
    appModel.update({ _id: req.body.appId }, updateData, function(err, data) {
        if (err) {
            return next(err);
        } else {
            res.status(200);
            res.send(true);
            return next();
        }
    })
}