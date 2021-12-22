const express = require("express");

// const validateThat = require('./middleware');
const controller = require("./users-controller");
const router = express.Router();

/**
 * Find all tags.
 *
 * @name GET /api/tags/
 *
 * @return [String] - list of all tags
 */
router.get("/", async (req, res) => {
  let users = await controller.findAllTags();
  res.status(200).json(users).end();
});

module.exports = router;
