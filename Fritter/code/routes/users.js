const express = require('express');
const Freets = require('../Models/Freets');
const Users = require('../Models/Users');
const checkThat = require('./middleware');

const router = express.Router();

/**
 * Create new User.
 * 
 * @name POST /api/user
 * @throws {400} - if username/password isn't valid
 */
router.post('/', [checkThat.userIsNotLoggedIn, checkThat.usernameIsValid, checkThat.passwordIsValid, checkThat.usernameDoesNotExist], (req, res) => {
  const user = Users.createUser(req.body.username, req.body.password);
  req.session.username = req.body.username; 
  res.status(200).json(user).end();
});

/**
 * Change username
 * 
 * @name PUT /api/user/:name
 * @throws {400} - if username/password isn't valid
 * @throws {403} - if user isn't logged in
 */
router.put('/:name', [checkThat.usernameDoesNotExist, checkThat.usernameIsValid], (req, res) => {
  const user = Users.updateUsername(req.session.username, req.params.name);
  req.session.username = user.username;
  res.status(200).json(user).end();
});

/** 
 * Toggle whether :name follows :followee
 * 
 * @name PATCH /api/user/toggleFollow/:followee
 * @throws {403} - if user isn't logged in
 */
 router.patch('/toggleFollow/:followee', [checkThat.userIsLoggedIn], (req, res) => {
  // Note: allows following of users that don't exist (yet).  
  let isNowFollowing = Users.toggleFollow(req.session.username, req.params.followee);
  res.status(200).json(isNowFollowing).end();
});

/**
 * Gets freets by all users that :name follows.
 * 
 * @name GET /api/user/freetsByFollowing
 * @throws {403} - if user isn't logged in
 */
 router.get('/freetsByFollowing', [checkThat.userIsLoggedIn], (req, res) => {
  const user = Users.findUser(req.session.username);
  const freetsByFollowing = Freets.findFreetsByAuthors(Array.from(user.following));
  res.status(200).json(freetsByFollowing).end();
});

/**
 * Gets users that a user follows
 * 
 * @name GET /api/user/following 
 * @throws {403} - if user isn't logged in
 */
 router.get('/following', [checkThat.userIsLoggedIn], (req, res) => {
  const user = Users.findUser(req.session.username);
  res.status(200).json(Array.from(user.following)).end();
});

/**
 * Gets a users followers
 * 
 * @name GET /api/user/followers 
 * @throws {403} - if user isn't logged in
 */
 router.get('/followers', [checkThat.userIsLoggedIn], (req, res) => {
  res.status(200).json(Users.getFollowers(req.session.username)).end();
});


/**
 * Refreet a given freet
 * 
 * @name PATCH /api/user/refreet/:id
 * @throws {403} - if user isn't logged in
 * @throws {400} - if freet does not exist or user is owner of freet.
 */
 router.patch('/refreet/:id', [checkThat.userIsLoggedIn, checkThat.freetExists, checkThat.userIsNotCreator], (req, res) => {
  let refreet = Freets.refreet(req.params.id, req.session.username);
  res.status(200).json(refreet).end();
});

/**
 * Toggle whether :name upvotes :freetId
 * 
 * @name PATCH /api/user/toggleUpvote/:id
 * @throws {403} - if user isn't logged in
 */
 router.patch('/toggleUpvote/:id', [checkThat.userIsLoggedIn, checkThat.freetExists], (req, res) => {
  let freet = Freets.toggleUpvote(req.params.id, req.session.username);
  res.status(200).json(freet).end();
});


/**
 * Change password
 * 
 * @name PUT /api/user/
 * @throws {400} - if username/password isn't valid
 * @throws {403} - if username isn't logged in
 */
router.put('/', [checkThat.userIsLoggedIn, checkThat.passwordIsValid], (req, res) => {
  const user = Users.updatePassword(req.session.username, req.body.password)
  res.status(200).json(user).end();
});

/**
 * Delete the current user signed-in.
 * 
 * @name DELETE /api/user/
 * @throws {403} - if username isn't logged in
 */
router.delete('/', [checkThat.userIsLoggedIn], (req, res) => {
  user = Users.deleteUser(req.session.username);
  req.session.username = null;
  res.status(200).json({message: `You have successfully deleted your account!`, user: user}).end();
});

/**
 * Sign-in.
 * 
 * @name POST /api/user/signin
 * @throws {400} - if username/password isn't valid
 */
router.post('/signin', [checkThat.userIsNotLoggedIn, checkThat.usernameIsValid, checkThat.passwordIsValid], (req, res) => {
  if (!Users.usernameExists(req.body.username)) {
    res.status(400).json({ error: 'Invalid user.' }).end();
  } else if (!Users.signIn(req.body.username, req.body.password)) {
    res.status(400).json({ error: 'The password is incorrect.'}).end();
  } else {
    req.session.username = req.body.username
    res.status(200).json({username: req.session.username}).end();
  }
});

/**
 * Get signed-in user
 * 
 * @name GET /api/user/signedin
 */
 router.get('/signedin', (req, res) => {
    res.status(200).json({username: req.session.username}).end();
});


/**
 * Sign-out.
 * 
 * @name POST /api/user/signout
 */
router.post('/signout', (req, res) => {
  req.session.username = null;
  res.status(200).json({message: 'You have successfully been signed out.'}).end();
});

module.exports = router;
