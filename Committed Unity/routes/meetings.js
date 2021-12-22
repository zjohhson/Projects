const express = require("express");

const controller = require("./meetings-controller");
// const validateThat = require('./middleware');

const router = express.Router();
const validateThat = require("./middleware");
const usersController = require("./users-controller");

/**
 * Get all meetings that currently exist.
 *
 * GET /api/meetings
 *
 * @return {Meeting[]} - a list of all meetings
 */
router.get("/", async (req, res) => {
  let meetings = await controller.getAll();
  res.status(200).json(meetings).end();
});

/**
 * Create a new meeting
 *
 * POST /api/meetings
 *
 * @return {Meeting} - the created meeting
 */
router.post("/", [validateThat.endAfterStart], async (req, res) => {
  const meeting = await controller.addOne(
    req.body.name,
    req.body.startDateTime,
    req.body.endDateTime,
    req.body.location,
    req.body.agenda,
    req.body.tags,
    req.body.host_id,
    req.body.attendees,
    req.body.going,
    req.body.maybe,
    req.body.notGoing,
    req.body.attendanceCode
  );
  res.status(200).json(meeting).end();
});

/**
 * Delete a new meeting by its ID
 *
 * DELETE /api/meetings
 *
 * @return {Meeting} - the deleted meeting
 */
router.delete("/:id?", [validateThat.adminIsCreator], async (req, res) => {
  const meeting = await controller.deleteOne(req.params.id);
  res.status(200).json(meeting).end();
});

/**
 * Edit a new meeting
 *
 * PATCH /api/meetings/:id
 *
 * @return {Meeting} - the edited meeting
 */
router.patch(
  "/:id?",
  [validateThat.adminIsCreator, validateThat.endAfterStart],
  async (req, res) => {
    const meeting = await controller.updateOne(
      req.params.id,
      req.body.name,
      req.body.startDateTime,
      req.body.endDateTime,
      req.body.location,
      req.body.agenda,
      req.body.tags,
      req.body.host,
      req.body.attendanceCode
    );
    res.status(200).json(meeting).end();
  }
);

/**
 * Edit a new meeting - overwrite fields
 * (used for deleting tags right now, but could be used for other fields too)
 *
 * PUT /api/meetings/:id
 *
 * @return {Meeting} - the edited meeting
 */
router.put(
  "/:id?",
  [validateThat.adminIsCreator, validateThat.endAfterStart],
  async (req, res) => {
    const meeting = await controller.patchOne(req.params.id, req.body.tags);
    res.status(200).json(meeting).end();
  }
);

/**
 * Edit the RSVP list
 *
 * PATCH /api/meetings/:id/rsvp
 *
 * @return {Meeting} - the edited meeting
 */
router.patch("/:id/rsvp", async (req, res) => {
  const meeting = await controller.updateRSVPStatus(
    req.params.id,
    req.body.goingId, // goingId, maybeId, and notGoingId with the user's ID as a string or ""
    req.body.maybeId, // depending on which RSVP was selected
    req.body.notGoingId
  );

  res.status(200).json(meeting).end();
});

/**
 * Add user to meeting's list of attendees
 *
 * PATCH /api/meetings/:id/attendance
 *
 * @return {String[]} - a list of attendance codes
 */
router.patch("/:id/attendance", async (req, res) => {
  const meeting = await controller.updateAttendees(
    req.body.userId,
    req.body.meetingId
  );

  res.status(200).json(meeting).end();
});

module.exports = router;
