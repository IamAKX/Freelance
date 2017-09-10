var express = require('express');
var router = express.Router();
var path = require('path');
var loginCtrl = require(path.join(__dirname, '..', '..', 'controller', 'user', 'imageUploadCtrl'));
var jwt = require(path.join(__dirname, '..', '..', 'service', 'util', 'jwt'));
var userCtrl = require(path.join(__dirname, '..', '..', 'controller', 'user', 'userCtrl'));

/**
 * uploads image for a user 
 * req.body {uploadImage}
 * res.body {status,userimage/reason}
 */
router.post('/uploadImage', jwt.validateToken, loginCtrl.uploadImage);

/**
 * address for a user
 */
router.post('/setAddress', jwt.validateToken, userCtrl.addAddress);
router.get('/getAddress', jwt.validateToken, userCtrl.showAddress);

/**
 * present job for a user
 */
router.post('/setPresentJob', jwt.validateToken, userCtrl.addPresentJob);
router.get('/getPresentJob', jwt.validateToken, userCtrl.showPressentJob);

/**
 * qualification for a user
 */
router.post('/setQualification', jwt.validateToken, userCtrl.setQualification);
router.get('/getQualification', jwt.validateToken, userCtrl.showQualification);

/**
 * experience for a user
 */
router.post('/postExperience', jwt.validateToken, userCtrl.postExperience);
router.get('/getExperience', jwt.validateToken, userCtrl.getExperience);
router.post('/updateExperience', jwt.validateToken, userCtrl.updateExperience);
router.post('/insertExperience', jwt.validateToken, userCtrl.insertExperience);
router.post('/deleteExperience', jwt.validateToken, userCtrl.deleteExperience);

/**
 * Mode for a user
 */
router.get('/getMode', jwt.validateToken, userCtrl.getMode);
router.post('/setMode', jwt.validateToken, userCtrl.setMode);

/**
 * setCategory for a user
 */
router.post('/setCategory', jwt.validateToken, userCtrl.setCategory);


/**
 * get a user as favourite
 */
router.post('/setFavourite', jwt.validateToken, userCtrl.setFavourite);
router.get('/getFavourites', jwt.validateToken, userCtrl.getFavourites);



/**
 * report a user or a project
 */

router.post('/report', jwt.validateToken, userCtrl.report);
router.get('/getReports', jwt.validateToken, userCtrl.getReports);


/**
 * feedback froma user
 */

router.post('/feedback', jwt.validateToken, userCtrl.setFedback);
router.post('/getFeedback', jwt.validateToken, userCtrl.getFeedback);


/**
 * get an email otp for password recovery
 */
router.post('/emailOtp', userCtrl.emailOtp);

router.get('/getProfilePercent', jwt.validateToken, userCtrl.getProfilePercent);

module.exports = router;