const controller = require("./users-controller");
const meetings_controller = require("./meetings-controller");
// const Users = require('../models/Users');

// Checks that the username is not an empty string and it's not already taken
const usernameIsValid = async (req, res, next) => {
  const user = await controller.findOne(req.body.username);
  if (req.body.username === "") {
    res
      .status(400)
      .json({
        error: "Username cannot be empty.",
      })
      .end();
    return;
  }
  if (req.body.username.trim().length !== req.body.username.length) {
    res
      .status(400)
      .json({
        error:
          "Please remove any leading or trailing whitespace from your username.",
      })
      .end();
    return;
  }
  if (req.body.username.length > 50) {
    res
      .status(400)
      .json({
        error: "Username cannot be more than 30 characters.",
      })
      .end();
    return;
  }
  if (user !== undefined) {
    // throw an error if the username is taken by someone else or the current user
    res
      .status(400)
      .json({
        error: `The username ${req.body.username} is taken.`,
      })
      .end();
    return;
  }
  next();
};

// Checks that the password is not an empty string or undefined
const passwordIsValid = (req, res, next) => {
  if (req.body.password === "" || req.body.password === undefined) {
    res
      .status(400)
      .json({
        error: "Password cannot be empty.",
      })
      .end();
    return;
  }
  if (req.body.password.length > 50) {
    res
      .status(400)
      .json({
        error: "Password cannot be more than 30 characters.",
      })
      .end();
    return;
  }
  next();
};

// Checks that the user's first and last name inputs are not an empty string or undefined
const nameIsValid = (req, res, next) => {
  if (req.body.firstName === "" || req.body.firstName === undefined) {
    res
      .status(400)
      .json({
        error: "First name cannot be empty.",
      })
      .end();
    return;
  }
  if (req.body.firstName.length > 50) {
    res
      .status(400)
      .json({
        error: "First name cannot be more than 30 characters.",
      })
      .end();
    return;
  }
  if (req.body.lastName === "" || req.body.lastName === undefined) {
    res
      .status(400)
      .json({
        error: "Last name cannot be empty.",
      })
      .end();
    return;
  }
  if (req.body.lastName.length > 50) {
    res
      .status(400)
      .json({
        error: "Last name cannot be more than 30 characters.",
      })
      .end();
    return;
  }
  next();
};

// Checks that the username is set in session, i.e., user logged in
const userIsLoggedIn = (req, res, next) => {
  if (req.session.username === undefined || req.session.username === null) {
    res
      .status(403)
      .json({
        error: "You must be logged in in order to perform this action.",
      })
      .end();
    return;
  }
  next();
};

// Checks that the username is not set in session, i.e., user is not logged in
const userIsNotLoggedIn = (req, res, next) => {
  if (req.session.username !== undefined && req.session.username !== null) {
    res
      .status(403)
      .json({
        error: `You are already logged in as ${req.session.username}. Please log out to perform this action.`,
      })
      .end();
    return;
  }
  next();
};

const tagIsValid = async (req, res, next) => {
  const tags = await controller.findAllTags();
  if (tags.indexOf(req.body.tag) === -1) {
    res.status(404).json({
      error: `The tag ${req.body.tag} does not exist.`,
    });
  }
  next();
};

const endAfterStart = async (req, res, next) => {
  var startTime = new Date(req.body.startDateTime);
  var endTime = new Date(req.body.endDateTime);

  if (startTime >= endTime) {
    res
      .status(400)
      .json({
        error: "Meeting cannot end before it starts.",
      })
      .end();
    return;
  }
  next();
};

const adminIsCreator = async (req, res, next) => {
  const meeting = await meetings_controller.findOne(req.params.id);
  const user = await controller.findOne(req.session.username);
  console.log(req.params.id, user._id);
  if (!meeting.host_id.equals(user._id)) {
    res
      .status(403)
      .json({
        error: `You are not the host of Meeting ${req.params.id}.`,
      })
      .end();
    return;
  }
  next();
};

const nameNotEmpty = (req, res, next) => {
  if (req.body.name.length === 0) {
    res
      .status(400)
      .json({
        error: "Meeting name must be at least 1 character",
      })
      .end();
    return;
  }
  next();
};

const locationNotEmpty = (req, res, next) => {
  if (req.body.name.location === 0) {
    res
      .status(400)
      .json({
        error: "Meeting location must be at least 1 character",
      })
      .end();
    return;
  }
  next();
};

const agendaNotEmpty = (req, res, next) => {
  if (req.body.agenda.length === 0) {
    res
      .status(400)
      .json({
        error: "Meeting agenda must be at least 1 character",
      })
      .end();
    return;
  }
  next();
};

const startDateTimeNotEmpty = (req, res, next) => {
  if (req.body.startDateTime.length === 0) {
    res
      .status(400)
      .json({
        error: "Meeting start time must not be empty.",
      })
      .end();
    return;
  }
  next();
};

const endDateTimeNotEmpty = (req, res, next) => {
  if (req.body.endDateTime.length === 0) {
    res
      .status(400)
      .json({
        error: "Meeting end time must not be empty.",
      })
      .end();
    return;
  }
  next();
};

const dateNotEmpty = (req, res, next) => {
  if (req.body.date.length === 0) {
    res
      .status(400)
      .json({
        error: "Meeting date must not be empty.",
      })
      .end();
    return;
  }
  next();
};

module.exports = Object.freeze({
  usernameIsValid,
  passwordIsValid,
  userIsNotLoggedIn,
  userIsLoggedIn,
  nameIsValid,
  tagIsValid,
  endAfterStart,
  // meetingExists,
  adminIsCreator,
  nameNotEmpty,
  locationNotEmpty,
  agendaNotEmpty,
  startDateTimeNotEmpty,
  endDateTimeNotEmpty,
  dateNotEmpty,
});
