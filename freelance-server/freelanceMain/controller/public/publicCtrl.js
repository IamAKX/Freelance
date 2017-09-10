var path = require('path');
var mongoose = require('mongoose');
var userModel = mongoose.model('user');
var async = require('async');
var _ = require('lodash');
var ObjectId = require('mongoose').Types.ObjectId;
var check = require(path.join(__dirname, '..', '..', 'service', 'util', 'checkValidObject'));


var filter = {
    _id: 1,
    name: 1,
    phone: 1,
    category: 1,
    image: 1,
    userName: 1,
    certified: 1
}
var sort = "_id"

/**
 * get all users present in database at once
 */
exports.getAllUser = function(req, res, next) {
    var fav;

    userModel.findOne({ _id: req.userId }, { favourites: 1, _id: 0 }, function(err, favdata) {
        userModel
            .find({ _id: { $ne: req.userId } })
            .select(filter)
            .sort(sort)
            .find(userDataCallback)

        function userDataCallback(err, data) {
            if (err) {
                res.status(500);
                res.send({ status: false, reason: "error fetching users" });
                return next(err);
            }
            var favsArray = favdata.favourites;
            var result = [];
            _.forEach(data, function(user) {

                var found = _.find(favsArray, function(f) {
                    return f == user._id;
                })
                if (!check.isUndefined(found)) {
                    fav = true;
                } else {
                    fav = false;
                }

                var temp = {
                    "_id": user._id,
                    "name": user.name,
                    "userName": user.userName,
                    "phone": user.phone,
                    "image": user.image,
                    "certified": user.certified,
                    "category": user.category,
                    "isFavourite": fav,
                    "rate": 4.5
                }
                result.push(temp);
            })

            res.status(200);
            res.send(result);
            return next();
        }
    })


}

/**
 * get user upto a limit
 */
exports.getAllUserLimit = function(req, res, next) {
    userModel
        .find({ _id: { $ne: req.userId } })
        .select(filter)
        .sort(sort)
        .limit(parseInt(req.params.limit))
        .find(userDataCallback)

    function userDataCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error fetching users" });
            return next(err);
        }
        res.status(200);
        res.send(data);
        return next();
    }
}

/**
 * get all user skip and limit
 */
exports.getAllUserSkipLimit = function(req, res, next) {
    userModel
        .find({ _id: { $ne: req.userId } })
        .select(filter)
        .sort(sort)
        .skip(parseInt(req.params.skip))
        .limit(parseInt(req.params.limit))
        .find(userDataCallback)

    function userDataCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error fetching users" });
            return next(err);
        }
        res.status(200);
        res.send(data);
        return next();
    }
}

/**
 * details of a user by id
 * name,phone,address,image,qualification,experience,category
 * when a user clicks a user from the home
 */
exports.getUserPublicData = function(req, res, next) {
    if (req.body._id === undefined) {
        res.status(500);
        res.send({ status: false, reason: "Invalid user" });
        return next(err);
    }
    userModel.findOne({ _id: req.body._id }, {
        name: 1,
        phone: 1,
        address: 1,
        image: 1,
        qualification: 1,
        experience: 1,
        category: 1,
        userName: 1,
        certified: 1
    }, userDetailsCallback);

    function userDetailsCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error fetching User Data" });
            return next(err);
        }
        var isFavourite = false;
        userModel.findOne({ _id: req.userId }, { favourites: 1 }, function(err, ownData) {
            var favIndex = _.findIndex(ownData.favourites, function(favouriesId) {
                return favouriesId == req.body._id;
            })
            if (favIndex != -1) {
                isFavourite = true;
            }
            var obj = {
                _id: data._id,
                name: data.name,
                userName: data.userName,
                phone: data.phone,
                image: data.image,
                category: data.category,
                experience: data.experience,
                qualification: data.qualification,
                isFavourite: isFavourite,
                address: data.address
            }
            res.status(200);
            res.send(obj);
            return next();
        })
    }
}