var express = require('express');
var router = express.Router();
var path = require('path');
var publicCtrl = require(path.join(__dirname, '..', '..', 'controller', 'public', 'publicCtrl'));
var jwt = require(path.join(__dirname, '..', '..', 'service', 'util', 'jwt'));

/**
 * get all users with basic details
 * Authorization
 */
router.get('/users/all',
    jwt.validateToken,
    publicCtrl.getAllUser)

/**
 * get all users with basic details upto a limit
 * Authorization
 */
router.get('/users/limit/:limit',
    jwt.validateToken,
    publicCtrl.getAllUserLimit)

/**
 * get all users with basic details skip and limit
 * Authorization
 */
router.get('/users/skip/:skip/limit/:limit',
    jwt.validateToken,
    publicCtrl.getAllUserSkipLimit)

/**
 * get a detailed data for a user by _id
 * needed when a user gets the details for another user
 * Authorization
 * req.body :{_id }
 * res.body :{user }
 */
router.post('/users/detail',
    jwt.validateToken,
    publicCtrl.getUserPublicData
)

module.exports = router;