var express = require('express');
var router = express.Router();
var path = require('path');
var projectCtrl = require(path.join(__dirname, '..', '..', 'controller', 'project', 'projectCtrl'));
var chatroomCtrl = require(path.join(__dirname, '..', '..', 'controller', 'project', 'chatroomCtrl'));
var jwt = require(path.join(__dirname, '..', '..', 'service', 'util', 'jwt'));
var projService = require(path.join(__dirname, '..', '..', 'service', 'project', 'projService'));



/**
 * post a project
 * 
 * req.body: {name,description,startDate,endDate,type,participant,category,duration},
 * res.body: {status,reason}
 */
router.post('/postProject',
    jwt.validateToken,
    projectCtrl.checkHireMode,
    projectCtrl.postProject,
    projectCtrl.notify
)

/**
 * list of project for a particular user
 * res.body:[projects]
 */
router.get('/getAllProjects',
    jwt.validateToken,
    // projectCtrl.checkHireMode,
    projectCtrl.getAllProjects
)

/**
 * project details for a paricular project
 * 
 * req.body :{projectId },
 * res.body:{projectdetails }
 */
router.post('/getProjectDetails/employer',
    jwt.validateToken,
    projService.checkProjectCreator,
    projectCtrl.getProjectDetails
)


/**
 * check weather a worker belongs to that project
 * then change his status
 * 
 * req.body { projectId }
 * res.status : {status,reason}
 */
router.post('/acceptProject',
    jwt.validateToken,
    projService.checkWorkerAccess,
    chatroomCtrl.createChatroom,
    projectCtrl.acceptProject
)

/**
 * check weather a worker belongs to that project
 * then change his status
 * 
 * req.body { projectId }
 * res.status : {status,reason}
 */
router.post('/rejectProject',
    jwt.validateToken,
    projService.checkWorkerAccess,
    projectCtrl.rejectProject
)

/**
 * projects where the worker is present
 * 
 * res.body : [  ]
 */
router.get('/allProjects',
    jwt.validateToken,
    projectCtrl.allProjects

)

/**
 * change a project status
 * 
 * req.body:{ projectId}
 * res.body:{ status,reason}
 */

router.post('/completed',
    projectCtrl.changeStatusProject
)

/**
 * change a project status
 * 
 * req.body:{ projectId}
 * res.body:{ status,reason}
 */

router.post('/timeout',
    projectCtrl.changeStatusProjectTimeout
)

/**
 * get all workers who accepted the project
 * can only be seen by the employer
 * 
 * req.body : {projectId }
 * res.body : {[particpants]}
 */

router.post('/accepted',
    jwt.validateToken,
    // projService.checkProjectCreator,
    projectCtrl.getAccepteds
)

/**
 * 
 * get project details from worker perspective
 * req.body :{projectId }
 */

router.post('/getProjectDetails/worker',
    jwt.validateToken,
    projectCtrl.getProjectDetailsWorker
)

module.exports = router;