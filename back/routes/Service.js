const express = require('express');
const router = express.Router();
const serviceController = require('../controllers/Service');
const auth = require('../middleware/auth');

router.post('/create', auth, serviceController.CreateService);
router.post('/edit', auth, serviceController.EditService);
router.post('/delete', auth, serviceController.DeleteService);

router.get('/get/:id', auth, serviceController.GetService);

module.exports = router;