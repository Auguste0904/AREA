const express = require('express');
const router = express.Router();
const connexionController = require('../controllers/Connection');
const auth = require('../middleware/auth');

router.get('/getGithub', auth, connexionController.GetGithub);
router.post('/loginGithub', auth, connexionController.LoginGithub);

router.get('/getGitlab', auth, connexionController.GetGitLab);
router.post('/loginGitlab', auth, connexionController.LoginGitLab);

router.get('/getTrello', auth, connexionController.GetTrello);
router.post('/loginTrello', auth, connexionController.LoginTrello);

router.post('/loginEpitech', auth, connexionController.LoginEpitech);

module.exports = router;