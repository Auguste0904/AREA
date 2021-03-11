const express = require('express');
const router = express.Router();
const userController = require('../controllers/User');
const auth = require('../middleware/auth');

router.post('/signup', userController.signup);
router.post('/login', userController.login);
router.post('/authgoogle', userController.getUserInfo);
router.post('/update', auth, userController.update);
router.post('/change', auth, userController.changePwd);
router.post('/delete', auth, userController.deleteMe);

router.get('/verify/:token', userController.verify);
router.get('/getGoogle', userController.getConnexionUrl);
router.post('/loginGoogle', auth, userController.LoginGoogle);
router.get('/me', auth, userController.me);
module.exports =  router;