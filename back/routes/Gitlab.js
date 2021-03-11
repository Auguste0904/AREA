const express = require('express');
const router = express.Router();
const gitlabController = require('../controllers/Gitlab');
const auth = require('../middleware/auth');

router.get('/get_projects', auth, gitlabController.GetUserProjects);
router.get('/get_branch/:id', auth, gitlabController.GetProjectBranch);
module.exports = router;