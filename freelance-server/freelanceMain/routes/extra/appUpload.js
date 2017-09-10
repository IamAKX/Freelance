var express = require('express');
var router = express.Router();
var path = require('path');
var appCtrl = require(path.join(__dirname, '..', '..', 'controller', 'misc', 'appCtrl'));


/**
 * upload the app
 */
router.post('/uploadApp', appCtrl.uploadApp)

/**
 * get all versions of app
 */
router.get('/getUploadedApp', appCtrl.getUploadedApp)

/**
 * delete the app
 */
router.post('/deleteApp', appCtrl.deleteApp)

/**
 * update appData
 */
router.post('/updateApp', appCtrl.updateApp)

module.exports = router;