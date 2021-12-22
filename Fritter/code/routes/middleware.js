const Users = require('../Models/Users');
const Freets = require('../Models/Freets');


// Checks that the username is set in session, i.e., user logged in
const userIsLoggedIn = (req, res, next) => {
  if (req.session.username == undefined) {
	  res.status(403).json({
		  error: 'You must be signed in!',
	  }).end();
	  return;
  }
  next();
};

// Checks that the username is not set in session
const userIsNotLoggedIn = (req, res, next) => {
  if (req.session.username != undefined) {
    res.status(400).json({
      error: 'You are already logged into an account!',
    }).end();
    return;
  }
  next();
};

// Checks that the username is not already in use
const usernameDoesNotExist = (req, res, next) => {
  if (Users.findUser(req.body.username) !== undefined) {
    res.status(400).json({
      error: 'Username already exists',
    }).end();
    return;
  }
  next();
};

// Checks that the username is already in use
const usernameExists = (req, res, next) => {
  if (Users.findUser(req.params.author) === undefined) {
    res.status(404).json({
      error: 'No user found with username',
    }).end();
    return;
  }
  next();
};

// Checks that the username is at least 1 character
const usernameIsValid = (req, res, next) => {
  if (req.body.username.length === 0) {
   	  res.status(400).json({
   	  	error: 'Username must be at least 1 character',
   	  }).end();
   	  return;
  }
  next();
}

// Checks that the password is at least 1 character
const passwordIsValid = (req, res, next) => {
  if (req.body.password.length === 0) {
   	  res.status(400).json({
   	  	error: 'Password must be at least 1 character',
   	  }).end();
   	  return;
  }
  next();
}

// Checks that the user is the author
const userIsCreator = (req, res, next) => {
  curr_freet = Freets.findFreetById(req.params.id)
  if (curr_freet.author != req.session.username) {
    res.status(403).json({
        error: `You are not the author of Freet ${req.params.id}.`,
      }).end();
   	  return;
  }
  next();
}

// Checks that the user is not the author
const userIsNotCreator = (req, res, next) => {
  curr_freet = Freets.findFreetById(req.params.id)
  if (curr_freet.author == req.session.username) {
    res.status(403).json({
        error: `You are the author of Freet ${req.params.id}.`,
      }).end();
   	  return;
  }
  next();
}

// Checks if the freet exists
const freetExists = (req, res, next) => {
  if (Freets.findFreetById(req.params.id) == undefined) {
    res.status(404).json({
      error: `Freet ${req.params.id} does not exist.`,
    }).end();
   	  return;
  }
  next();
}

module.exports = Object.freeze({
  userIsLoggedIn,
  userIsNotLoggedIn,
  usernameDoesNotExist,
  usernameIsValid,
  passwordIsValid,
  userIsCreator,
  userIsNotCreator,
  freetExists,
  usernameExists,
});
