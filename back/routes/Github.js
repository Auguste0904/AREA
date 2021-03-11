const express = require('express');
const router = express.Router();
const githubController = require('../controllers/Github');
const auth = require('../middleware/auth');

router.get('/get_projects', auth, githubController.GetUserProjects);
router.get('/get_branch/:owner/:project', auth, githubController.GetProjectBranch);
module.exports = router;