var mongoose = require('mongoose');
var apiModel = mongoose.model('api');
var apiUpdateModel = mongoose.model('apiupdate');
var path = require('path');
var _ = require('lodash');

exports.changeUpdateDate = function(req, res, next) {
    apiUpdateModel.create({ changeDate: Date.now() }, function(err, data) {
        next();
    });
};

exports.getLastDate = function(req, res, next) {
    apiUpdateModel.find({}, {}, { limit: 1, sort: { changeDate: -1 } }, function(err, data) {
        res.send(data);
        return next();
    });
};

exports.getAllApi = function(req, res, next) {

    apiModel.find({}, function(err, data) {
        if (err) {
            return (err);
        } else {
            var result = _.map(data, function(x) {
                var newObj = JSON.parse(JSON.stringify(x)); // Create a copy so you don't mutate the original.
                newObj._id = new mongoose.Types.ObjectId(x._id).getTimestamp()
                newObj._idd = x._id;
                return newObj;
            });

            res.send(result);
            return next();
        }
    });
};

exports.saveApi = function(req, res, next) {
    apiModel.create(req.body, function(err, data) {
        if (err) {
            return next();
        } else {
            res.send(data);
            return next();
        }
    });
};

exports.removeApi = function(req, res, next) {

    apiModel.remove({ _id: req.body._id }, function(err, data) {
        if (err) {
            return next();
        } else {
            res.send(data);
            return next();
        }
    });
};

exports.editApi = function(req, res, next) {
    console.log(req.body);
    apiModel.update({ _id: req.body._idd }, {
        $set: {
            name: req.body.name,
            category: req.body.category,
            api: req.body.api,
            header: req.body.header,
            reqBody: req.body.reqBody,
            resBody: req.body.resBody,
            description: req.body.description,
            type: req.body.type
        }
    }, function(err, data) {
        res.send(data);
        return next();
    });
};