var express = require('express');
var router = express.Router();
var path = require('path');
var apiCtrl = require(path.join(__dirname, '..', 'controller', 'misc', 'apiCtrl'));



/**
 * get the last time an api was updated
 */
router.get('/getLastDate', apiCtrl.getLastDate);

/**
 * get all apis
 */
router.get('/allApi', apiCtrl.changeUpdateDate, apiCtrl.getAllApi);

/**
 * save a new api
 */
router.post('/saveApi', apiCtrl.changeUpdateDate, apiCtrl.saveApi);

/**
 * edit an api
 */
router.post('/editApi', apiCtrl.changeUpdateDate, apiCtrl.editApi);

/**
 * remove an api
 */
router.post('/deleteApi', apiCtrl.changeUpdateDate, apiCtrl.removeApi);



module.exports = router;