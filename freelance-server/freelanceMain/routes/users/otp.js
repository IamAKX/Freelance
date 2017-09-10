var express = require('express');
var router = express.Router();
var path = require('path');
var otpCtrl = require(path.join(__dirname, '..', '..', 'controller', 'misc', 'otpCtrl'));

/**
 * generates an otp
 */
router.post('/genOtp', otpCtrl.generateOtp);
/**
 * verifies validity of an otp
 */
router.post('/verifyOtp', otpCtrl.verifyOtp);


module.exports = router;