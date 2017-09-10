var express = require('express');
var router = express.Router();
var path = require('path');
var chatroomCtrl = require(path.join(__dirname, '..', '..', 'controller', 'project', 'chatroomCtrl'));
var jwt = require(path.join(__dirname, '..', '..', 'service', 'util', 'jwt'));


/**
 * set  a chatroomId against a projectId
 * req.body:{userId}
 * res.status:{status,reason}
 */
router.post('/setChatroom', jwt.validateToken, chatroomCtrl.setChatroomId)

/**
 * get a chatroomId for a projectId
 * req.body:{ }
 * res.send:{chatroomId }
 */
router.get('/getChatroom', jwt.validateToken, chatroomCtrl.getChatroomData)

/**
 * delete a projectId
 * req.body:{projectId }
 * res.send:{status }
 */
router.post('/deleteChatroom', jwt.validateToken, chatroomCtrl.deleteChatroomId)

module.exports = router;