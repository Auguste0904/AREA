const express = require('express');
const router = express.Router();
const trelloController = require('../controllers/Trello');
const auth = require('../middleware/auth');

router.get('/get_board', auth, trelloController.GetBoard);
router.get('/get_list/:id', auth, trelloController.GetList);

module.exports = router;