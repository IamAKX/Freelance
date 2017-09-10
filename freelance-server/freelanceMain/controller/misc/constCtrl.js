var mongoose = require('mongoose');
var apiModel = mongoose.model('api');
var constantsModel = mongoose.model('constants');
var categoryModel = mongoose.model('category');

var path = require('path');
var _ = require('lodash');

exports.updateCategory = function(req, res, next) {
    var categories = req.body.values.split(',');
    constantsModel.update({ name: "category" }, { $set: { "value": categories } }, function(err, data) {

        if (err) {
            return next(err);
        } else {
            res.send(data);
            return next();
        }
    })
}

exports.getCategory = function(req, res, next) {
    constantsModel.findOne({ name: "category" }, { value: 1, _id: 0 }, function(err, data) {
        if (err) {
            res.status(500);
            res.send(err);
            return next(err);
        } else {
            res.status(200);
            res.send(data.value);
            return next();
        }
    })
}


exports.setCategory = function(req, res, next) {
    categoryModel.create(req.body, function(err, data) {
        console.log(err, data);
        if (err) {
            return next(err);
        }
        res.status(200);
        res.send(data);
        return next
    })
}

exports.updateCategory = function(req, res, next) {
    console.log(req.param, req.body);
    categoryModel.update({ _id: req.body._id }, req.body, function(err, data) {
        if (err) {
            return next(err);
        }
        res.status(200);
        res.send(data);
        return next
    })
}

exports.deleteCategory = function(req, res, next) {
    categoryModel.remove({ _id: req.body._id }, function(err, data) {
        if (err) {
            return next(err);
        }
        res.status(200);
        res.send(data);
        return next
    })
}

exports.getAllCategory = function(req, res, next) {
    categoryModel.find({}, function(err, data) {
        if (err) {
            return next(err);
        }
        res.status(200);
        res.send(data);
        return next
    })
}