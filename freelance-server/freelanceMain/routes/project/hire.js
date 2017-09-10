var express = require('express');
var router = express.Router();
var path = require('path');
var hireCtrl = require(path.join(__dirname, '..', '..', 'controller', 'project', 'hireCtrl'));
var jwt = require(path.join(__dirname, '..', '..', 'service', 'util', 'jwt'));

router.post('/addUser',
    jwt.validateToken,
    hireCtrl.hireUser);

router.get('/hiredEmployee',
    jwt.validateToken,
    hireCtrl.hireDetailsEmployee
)

router.get('/hiredEmployer',
    jwt.validateToken,
    hireCtrl.hireDetailsEmployer
)

router.get('/workerHiredProjects',
    jwt.validateToken,
    hireCtrl.workerHiredProjects
)

router.get('/getSumEarned',
    jwt.validateToken,
    hireCtrl.getSumEarned
)

router.get('/recentlyWorked',
    jwt.validateToken,
    hireCtrl.recentlyWorked);

router.post('/doneWorking',
    jwt.validateToken,
    hireCtrl.doneWorking)

module.exports = router;