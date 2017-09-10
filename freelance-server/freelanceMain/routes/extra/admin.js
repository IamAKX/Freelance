var express = require('express');
var router = express.Router();
var path = require('path');
var adminCtrl = require(path.join(__dirname, '..', '..', 'controller', 'misc', 'admin'));

router.get('/alluser', adminCtrl.getAllUser);

router.post('/getUserDetails', adminCtrl.getUserDetails)

router.post('/getAllProjectsUser', adminCtrl.getAllProjectsUser);

router.post('/changeUserData', adminCtrl.checkSuspend, adminCtrl.changeUserData);

router.get('/getAllProjects', adminCtrl.getAllProjects);

router.post('/getProjectDetails', adminCtrl.getProjectDetails);

router.get('/allHireData', adminCtrl.getAllHire);

router.get('/getAllReports', adminCtrl.getAllReports);

module.exports = router;