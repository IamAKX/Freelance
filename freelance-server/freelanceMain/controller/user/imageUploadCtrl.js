var mongoose = require('mongoose');
var userModel = mongoose.model('user');
var path = require("path");
var fs = require('fs');
var ObjectId = require('mongoose').Types.ObjectId;
var high = 9999;
var low = 1000;

function getRandom() {
    return (Math.random() * (high - low) + low).toString().substr(0, 4);
}

/**
 * upload a user image
 * image present as base64 encoded in req.body.imageString
 * 
 */
exports.uploadImage = function(req, res, next) {
    var body = req.body.imageString;
    var base64Data = body.replace(/^data:image\/png;base64,/, "");
    var binaryData = new Buffer(base64Data, 'base64').toString('binary');

    //image folder
    imageFile = "images/";

    //image name
    var imageName = "IMG" + req.userId + "_" + getRandom() + ".jpg";

    fs.writeFile(imageFile + imageName, binaryData, "binary", function(err) {
        if (err) {
            res.status(500);
            res.send({
                status: false,
                reason: "failed to upload"
            });
            return next();
        } else {
            var query = { _id: req.userId };
            userModel.findOne(query, { image: 1, _id: 0 }, function(err, data) {
                fs.unlink(imageFile + data.image, function(err) {
                    userModel.update(query, { $set: { image: imageName } }, function(err, data) {
                        res.status(200);
                        res.send({
                            status: true,
                            userimage: imageName
                        });
                        return next();
                    });
                });
            });
        }
    });
};