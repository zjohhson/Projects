const express = require("express");

const validateThat = require("./middleware");
const controller = require("./users-controller");
const histController = require("./history-controller");
const meetingsController = require("./meetings-controller");
const router = express.Router();

const ADMIN_PASSCODE = "password";

/**
 * adds new entry to user history
 * @return total points
 */
router.post("/history", async (req, res) => {
  const _ = await histController.addOne(
    req.session.username,
    req.body.date,
    req.body.points
  );
  const points = await histController.getUserLifetimePoints(
    req.session.username
  );
  res.status(200).json(points).end();
});

router.get("/history", async (req, res) => {
  const points = await histController.getUserLifetimePoints(
    req.session.username
  );
  res.status(200).json(points).end();
});

/**
 * Create new user.
 *
 * @name POST /api/users
 *
 * @param {string} username - the user's username
 * @param {string} password - the user's password
 * @return {User} - the created User
 * @throws {400} - if the username is taken or if the username or password is empty
 */
router.post(
  "/",
  [
    validateThat.usernameIsValid,
    validateThat.passwordIsValid,
    validateThat.nameIsValid,
    validateThat.userIsNotLoggedIn,
  ],
  async (req, res) => {
    if (!req.body.isAdmin) {
      const user = await controller.addOne(
        req.body.username,
        req.body.password,
        req.body.firstName,
        req.body.lastName,
        false
      );
      req.session.username = req.body.username;
      res.status(201).json(user).end();
    } else if (req.body.isAdmin === ADMIN_PASSCODE) {
      const user = await controller.addOne(
        req.body.username,
        req.body.password,
        req.body.firstName,
        req.body.lastName,
        true
      );
      req.session.username = req.body.username;
      res.status(201).json(user).end();
    } else {
      res
        .status(401)
        .json({
          error: `Incorrect admin code`,
        })
        .end();
    }
  }
);

/**
 * See if a user is signed in or not (to match initial front end cookies to backend)
 * Does not sign someone in - if a user is signed in, the front and back ends should already match.
 * If a user is not signed in, clear the cookie on the front end too.
 *
 * GET /api/users/session
 *
 * @return {User} - the signed out User
 * @throws {403} - if not logged in
 */
router.get("/session", async (req, res) => {
  if (req.session.username === undefined || req.session.username === null) {
    res.status(200).json({ signedIn: false }).end();
  } else {
    res.status(200).json({ signedIn: true }).end();
  }
});

/**
 * Sign out of a session
 *
 * DELETE /api/users/session
 *
 * @return {User} - the signed out User
 * @throws {403} - if not logged in
 */
router.delete("/session", [validateThat.userIsLoggedIn], async (req, res) => {
  req.session.username = null;
  res.status(200).json().end();
});

/**
 * Sign into a session.
 *
 * POST /api/users/session
 *
 * @return {User} - the User signed in
 * @throws {401} - if wrong password
 * @throws {404} - if user does not exist
 */
router.post("/session", [validateThat.userIsNotLoggedIn], async (req, res) => {
  const user = await controller.authenticateUser(
    req.body.username,
    req.body.password
  );
  if (user === undefined) {
    res
      .status(404)
      .json({
        error: `The user ${req.body.username} does not exist.`,
      })
      .end();
  } else if (user[0] === true) {
    req.session.username = req.body.username;
    res.status(201).json(user[1]).end();
  } else {
    res
      .status(401)
      .json({
        error: `Password is incorrect.`,
      })
      .end();
  }
});

/**
 * Get user's similar tags
 *
 * @name GET /api/users/tags
 *
 * @return [String] - list of all tags
 * @throws {404} - if user does not exist
 */
router.get("/tags", [validateThat.userIsLoggedIn], async (req, res) => {
  const tags = await controller.getSimilarTags(req.session.username);
  res.status(201).json(tags).end();
});

/**
 * Get user's recommended meetings
 *
 * @name GET /api/users/recMeetings
 *
 * @return [Object] - list of all meetings whos tags has intersection with user's interests
 * @throws {404} - if user does not exist
 */
router.get("/recMeetings", [validateThat.userIsLoggedIn], async (req, res) => {
  const user = await controller.findOne(req.session.username);
  const meetings = await meetingsController.getRecMeetings(user.interests);
  res.status(201).json(meetings).end();
});

/**
 * Find all users.
 *
 * @name GET /api/users/
 *
 * @return [User] - list of all users
 */
router.get("/", async (req, res) => {
  let users = await controller.findAll();
  res.status(200).json(users).end();
});

router.get("/:username?", async (req, res) => {
  let user = await controller.findOne(req.params.username);
  res.status(200).json(user).end();
});

router.get("/id/:id?", async (req, res) => {
  let user = await controller.findOneID(req.params.id);
  res.status(200).json(user).end();
});

/**
 * Add a user tag interest.
 *
 * @name PUT /api/users/tags
 *
 * @param {string} tag - the user's tag
 * @return {User} - user with updated tags
 * @throws {404} - the tag doesn't exist in list for addition
 */
router.put("/tags", [validateThat.tagIsValid], async (req, res) => {
  const user = await controller.addInterest(req.session.username, req.body.tag);
  res.status(201).json(user).end();
});

router.post("/tags", async (req, res) => {
  const tags = await controller.addTag(req.body.tag);
  res.status(200).json(tags).end();
});

router.post("/similarTags", async (req, res) => {
  const similarTags = await controller.addSimilarTags(
    req.body.tag,
    req.body.simTags
  );
  res.status(200).json(similarTags).end();
});

/**
 * Delete a user tag interest.
 *
 * @name PATCH /api/users/tags
 *
 * @param {string} tag - the user's tag
 * @return {User} - user with updated tags
 * @throws {404} - the tag isn't found in user's interests
 */
router.patch("/tags", [], async (req, res) => {
  const user = await controller.removeInterest(
    req.session.username,
    req.body.tag
  );
  res.status(201).json(user).end();
});

/**
 * Increment user points.
 *
 * @name PATCH /api/users/point
 *
 * @param {string} username - the user's username
 * @return {User} - user with updated number of points
 * @throws {404} - the user isn't found
 */
router.patch("/point", async (req, res) => {
  const user = await controller.incrementPoint(req.session.username);
  if (user === undefined) {
    res
      .status(404)
      .json({
        error: `The user ${req.session.username} does not exist.`,
      })
      .end();
  } else {
    res.status(201).json(user).end();
  }
});

module.exports = router;
