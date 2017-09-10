var express = require('express');
var router = express.Router();
var path = require('path');
var constCtrl = require(path.join(__dirname, '..', '..', 'controller', 'misc', 'constCtrl'));


/**
 * constant category set fields 
 */
// router.post('/setCategory', constCtrl.updateCategory)

/**
 * constant category get fields 
 */
router.get('/getCategory', constCtrl.getCategory)


router.get('/getallCategory', constCtrl.getAllCategory)

router.post('/setCategory', constCtrl.setCategory)

router.post('/updateallCategory', constCtrl.updateCategory)

router.post('/deleteCategory', constCtrl.deleteCategory)

module.exports = router;