var express = require('express');
var router = express.Router();
var path = require('path');
var mailUser = require(path.join(__dirname, '..', '..', 'controller', 'user', 'mailUser'));

/**
 * handles email verification
 * params : token
 */
router.get('/verifyEmailAccount', mailUser.handlerAccountVerify);

/**
 * handles email verification for username change
 * params : token
 */
router.get('/verifyEmailChangeAccount', mailUser.handlerAccountVerifyChange);

/**
 * ressend verification email
 * req.body : { username}
 * res.body : {status,reason}
 */
router.post('/resendEmailVerify', mailUser.resendEmailVerify);

/**
 * verifiy email from mobile platform
 */
router.post('/verifyEmailAccountFromApp', mailUser.handlerAccountVerifyFromApp)

module.exports = router;