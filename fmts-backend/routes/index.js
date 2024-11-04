var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', (req, res, next) => {
  res.render('index', { banner: 'Fidelity Main Trading System', title: 'FMTS', version: '1.0'});
});


module.exports = router;
