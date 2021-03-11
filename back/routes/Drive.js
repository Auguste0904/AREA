const express = require('express');
const router = express.Router();
const driveController = require('../controllers/Drive');
const auth = require('../middleware/auth');

router.get('/getFiles', auth, driveController.GetDriveFolder);
module.exports = router;