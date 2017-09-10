var path = require('path');
var mongoose = require('mongoose');
var userModel = mongoose.model('user');
var reportModel = mongoose.model('report');
var feedbackModel = mongoose.model('feedback');
var async = require('async');
var imguploadService = require(path.join(__dirname, '..', '..', 'service', 'user', 'imgUpload'));
var check = require(path.join(__dirname, '..', '..', 'service', 'util', 'checkValidObject'));
var mailService = require(path.join(__dirname, '..', '..', 'service', 'user', 'mailservice'));
var config = require(path.join(__dirname, '..', '..', 'bin', 'config'));
var ObjectId = require('mongoose').Types.ObjectId;
var _ = require('lodash');
var high = 9999;
var low = 1000;

var filter = {
    _id: 1,
    name: 1,
    phone: 1,
    category: 1,
    image: 1,
    userName: 1,
    certified: 1
}

function getRandom() {
    return (Math.random() * (high - low) + low).toString().substr(0, 4);
}

//add address
exports.addAddress = function(req, res, next) {
    var newAddress = {
        street: req.body.street,
        city: req.body.city,
        pincode: req.body.pincode,
        state: req.body.state,
        type: req.body.type
    };
    userModel.update({ _id: req.userId }, { $set: { address: newAddress } }, updateCallback);

    function updateCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error updating contact" });
            return next(err);
        }
        res.status(200);
        res.send({ status: true });
        return next();
    }
};
//get address
exports.showAddress = function(req, res, next) {
    userModel.findOne({ _id: req.userId }, { "address": 1, _id: 0 }, callback);

    function callback(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "contact not found" });
            return next(err);
        }
        res.status(200);
        res.send({
            address: data.address
        });
        return next();
    }
};

//add present job
exports.addPresentJob = function(req, res, next) {
    var presentJob = {
        position: req.body.position,
        organzation: req.body.organzation,
        startDate: req.body.startDate,
        endDate: req.body.endDate,
        presentlyWorking: req.body.presentlyWorking
    };
    userModel.update({ _id: req.userId }, { $set: { presentJob: presentJob } }, updateCallback);

    function updateCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error updating present Job" });
            return next(err);
        }
        res.status(200);
        res.send({ status: true });
        return next();
    }
};

//get presentJob
exports.showPressentJob = function(req, res, next) {
    userModel.findOne({ _id: req.userId }, { "presentJob": 1, _id: 0 }, callback);

    function callback(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "present job not found" });
            return next(err);
        }
        res.status(200);
        // res.send(data.presentJob);
        res.send({
            presentJob: data.presentJob
        });
        return next();
    }
};

//set qualification
exports.setQualification = function(req, res, next) {
    var qualification = JSON.parse(req.body.qualification);
    userModel.update({ _id: req.userId }, { $set: { qualification: qualification } }, updateCallback);

    function updateCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error updating Qualification" });
            return next(err);
        }
        res.status(200);
        res.send({ status: true });
        return next();
    }
};

//get presentJob
exports.showQualification = function(req, res, next) {
    userModel.findOne({ _id: req.userId }, { "qualification": 1, _id: 0 }, callback);

    function callback(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "qualification not found" });
            return next(err);
        }
        res.status(200);
        res.send({
            qualification: data.qualification
        });
        return next();
    }
};

exports.postExperience = function(req, res, next) {
    var experience = req.body.experience;
    var expArray = [];

    async.each(experience, function(exp, cb) {

        var imageFile = "images/exp/";
        var imageName = "IMG_EXP" + req.userId + "_" + getRandom() + ".jpg";

        imguploadService.upload(imageFile + imageName, exp.image, function(err, done) {
            if (err) {
                cb(err);
            }
            var temp = {
                category: exp.category,
                details: exp.details,
                link: exp.link,
                imageLink: imageName
            };
            expArray.push(temp);

            cb();
        });
    }, function(err) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error uploading" });
            return next(err);
        }
        userModel.update({ _id: req.userId }, { $set: { experience: expArray } }, updateCallback);

        function updateCallback(err, data) {
            if (err) {
                res.status(500);
                res.send({ status: false, reason: "error updating experience" });
                return next(err);
            }
            res.status(200);
            res.send({ status: true });
            return next();
        }
    });
};

exports.getExperience = function(req, res, next) {
    userModel.findOne({ _id: req.userId }, { "experience": 1, _id: 0 }, callback);

    function callback(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "experience not found" });
            return next(err);
        }
        res.status(200);
        res.send({
            experience: data.experience
        });
        return next();
    }
};

exports.updateExperience = function(req, res, next) {
    var exp = req.body;
    var imageFile = "images/exp/";
    var imageName = "IMG_EXP" + req.userId + "_" + getRandom() + ".jpg";
    var temp = {
        _id: new ObjectId(exp.expId),
        category: exp.category,
        details: exp.details,
        link: exp.link,
        imageLink: JSON.parse(exp.image)
    };

    userModel.update({ _id: req.userId, "experience._id": (exp.expId) }, { $set: { 'experience.$': temp } }, function(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error updating experience" });
            return next(err);
        }
        res.status(200);
        res.send({ status: true });
        return next();
    });
};

exports.insertExperience = function(req, res, next) {
    var exp = req.body;
    var imageFile = "images/exp/";
    var imageName = "IMG_EXP" + req.userId + "_" + getRandom() + ".jpg";
    var temp = {
        _id: new ObjectId(),
        category: exp.category,
        details: exp.details,
        link: exp.link,
        imageLink: JSON.parse(exp.image)
    };

    userModel.findOneAndUpdate({ _id: req.userId }, { $push: { experience: temp } }, function(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error inserting experience" });
            return next(err);
        }
        res.status(200);
        res.send({ status: true, id: data._id });
        return next();
    });
};

exports.deleteExperience = function(req, res, next) {
    userModel.findOneAndUpdate({ _id: req.userId }, { $pull: { experience: { _id: req.body.expId } } }, function(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error deleting experience" });
            return next(err);
        }
        res.status(200);
        res.send({ status: true });
        return next();
    });
};

//mode
exports.getMode = function(req, res, next) {
    userModel.findOne({ _id: req.userId }, { mode: 1, _id: 0 }, function(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error fetching mode" });
            return next(err);
        } else {
            res.status(200);
            res.send({ status: true, mode: data.mode });
            return next();
        }
    });
};

//set qualification
exports.setMode = function(req, res, next) {
    userModel.update({ _id: req.userId }, { $set: { mode: req.body.mode } }, updateCallback);

    function updateCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error updating Mode" });
            return next(err);
        }
        res.status(200);
        res.send({ status: true });
        return next();
    }
};

//set category
exports.setCategory = function(req, res, next) {
    userModel.update({ _id: req.userId }, { $set: { category: req.body.category } }, updateCallback);

    function updateCallback(err, data) {
        if (err) {
            res.status(500);
            res.send({ status: false, reason: "error updating category" });
            return next(err);
        }
        res.status(200);
        res.send({ status: true });
        return next();
    }
};

exports.setFavourite = function(req, res, next) {

    if (req.userId == req.body.userId) {
        errFetching("Cant set yourself as favourite");
    }

    userModel.findOne({ _id: req.userId }, { favourites: 1, _id: 0 }, function(err, data) {
        if (err) {
            errFetching(err);
        }
        var foundUser = _.findIndex(data.favourites, function(id) {
            return id == req.body.userId;
        })
        if (foundUser == -1) {
            userModel.findOneAndUpdate({ _id: req.userId }, { $push: { favourites: req.body.userId } }, function(err, data) {
                if (err) {
                    errFetching("error inserting favourites");
                }
                res.status(200);
                res.send({ status: true, reason: "added" });
                return next();
            });
        } else {
            userModel.findOneAndUpdate({ _id: req.userId }, { $pull: { favourites: req.body.userId } }, function(err, data) {
                if (err) {
                    errFetching("error removing favourites");
                }
                res.status(200);
                res.send({ status: true, reason: "removed" });
                return next();
            });
        }
    })

    function errFetching(reason) {
        res.status(500);
        res.send({ status: false, reason: reason });
        return next();
    }
}

exports.getFavourites = function(req, res, next) {

    userModel.findOne({ _id: req.userId }, { favourites: 1, _id: 0 }, function(err, userData) {
        var favosId = userData.favourites;
        userModel.find({ _id: { $in: favosId } }, filter, function(err, data) {
            if (err) {
                errFetching(err);
            }

            res.status(200);
            res.send(data);
            return next();
        })
    })

    function errFetching(reason) {
        status(500);
        res.send({ status: false, reason: reason });
        return next();
    }

}

exports.report = function(req, res, next) {
    var payload = {
        userId: req.userId,
        type: req.body.type,
        fileId: req.body.fileId,
        content: req.body.content
    };
    reportModel.create(payload, function(err, data) {
        if (err) {
            errFetching("error reporting");
        }

        res.status(200);
        res.send({
            status: true
        });
        return next();

    })

    function errFetching(reason) {
        res.status(500);
        res.send({ status: false, reason: reason });
        return next();
    }
}

exports.getReports = function(req, res, next) {
    reportModel.find({ userId: req.userId }, function() {
        if (err) {
            errFetching("error fetching");
        }

        res.status(200);
        res.send(data);
        return next();
    })

    function errFetching(reason) {
        res.status(500);
        res.send({ status: false, reason: reason });
        return next();
    }
}


exports.setFedback = function(req, res, next) {
    var payload = {
        userId: req.userId,
        fileId: req.body.fileId,
        type: req.body.type,
        rating: req.body.rating,
        content: req.body.content,
    }
    feedbackModel.create(payload, function(err, data) {
        if (err) {
            errFetching("error saving feedback");
        }

        res.status(200);
        res.send({
            status: true
        });
        return next();
    })

    function errFetching(reason) {
        res.status(500);
        res.send({ status: false, reason: reason });
        return next();
    }
}

exports.getFeedback = function(req, res, next) {

    feedbackModel.find({ fileId: req.userId }, function(err, data) {
        if (err) {
            errFetching("error fetching feedback");
        }

        var userIds = _.map(data, 'userId');
        console.log(userIds);
        userModel.find({ _id: { $in: userIds } }, { image: 1, name: 1, _id: 1 }, function(err, userData) {
            var result = [];
            _.forEach(data, function(u) {
                var found = _.find(userData, function(x) {
                    return x._id == u.userId;
                })
                result.push({
                    userId: u.userId,
                    name: found.name,
                    image: found.image,
                    fileId: u.fileId,
                    rating: u.rating,
                    content: u.content
                })
            })
            res.status(200);
            res.send(result);
            return next();
        })


    })

    function errFetching(reason) {
        res.status(500);
        res.send({ status: false, reason: reason });
        return next();
    }

}

exports.emailOtp = function(req, res, next) {
    userModel.findOne({ userName: req.body.username }, { userName: 1, name: 1 }, function(err, data) {
        if (err || check.isNull(data)) {
            errFetching('err fetching user data');
        }

        if (!check.isUndefined(data.userName)) {
            var toEmail = data.userName;
            var fromEmail = config.adminEmail;
            var subject = "Pasword recovery for Freelance";
            var otp = (Math.random() * (high - low) + low).toString().substr(0, 4);
            var body = "<h2> Hello! " + data.name + " ,</h2><p>There was a password recovery requested for your freelance account .</p>"
            body += '<p>Please enter the otp <b>' + otp + '</b> for verifying your account </p>';
            mailService.sendMail(toEmail, fromEmail, subject, body, function(err, data) {

                if (err) {
                    console.log(err);
                }

                res.status(200);
                res.send({
                    status: true,
                    otp: otp
                });
                return next();

            })
        } else {
            errFetching('err fetching user data');
        }
    })

    function errFetching(reason) {
        res.status(500);
        res.send({ status: false, reason: reason });
        return next();
    }

}

exports.getProfilePercent = function(req, res, next) {
    userModel.findOne({ _id: req.userId }, {}, function(err, data) {
        if (err || check.isNull(data)) {
            errFetching('err fetching user data');
        }
        var marks = 3;
        var arr = [
            { field: 'favourites', value: 2 },
            { field: 'active', value: 2 },
            { field: 'image', value: 4 },
            { field: 'mode', value: 4 },
            { field: 'address', value: 10 },
            { field: 'category', value: 5 },
            { field: 'presentJob', value: 5 },
            { field: 'certified', value: 5 },
            { field: 'experience', value: 10 },
            { field: 'qualification', value: 10 }
        ];

        _.forEach(arr, function(type) {
            if (!check.isUndefined(data[type.field])) {
                marks += type.value;
            }
        });
        var percent = (marks / 60) * 100;

        res.status(200);
        res.send({ percent: percent });
        return next();

    })

    function errFetching(reason) {
        res.status(500);
        res.send({ status: false, reason: reason });
        return next();
    }
}