var express = require('express');
var router = express.Router();
var path = require('path');
var jwt = require(path.join(__dirname, '..', '..', 'service', 'util', 'jwt'));
var tokenCtrl = require(path.join(__dirname, '..', '..', 'controller', 'token', 'tokenCtrl'));


/**
 * set a user Token
 * Authorization
 * req.body : {token }
 * res.body : {status,reason}
 */
router.post('/setToken', jwt.validateToken, tokenCtrl.setToken)


/**
 * get a user token
 * Authorization
 * res.body :{token }
 */
router.get('/getToken', jwt.validateToken, tokenCtrl.getToken)

module.exports = router