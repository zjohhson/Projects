const Meeting = require("../models/Meeting");
const User = require("../models/User");
const usersController = require("./users-controller");
const mongoose = require("mongoose");
// const User = require('../models/User');

async function getAll() {
  try {
    //perform a join on the Meeting and User collections: Meeting.meeting_creator_id corresponds to User._id
    const meetings = await Meeting.aggregate([
      {
        $lookup: {
          from: "users",
          localField: "host_id",
          foreignField: "_id",
          as: "host", //create a new field 'host' in this aggregated collection: embeds documents from lookup collection into Meeting collection
        },
      },
      {
        $lookup: {
          from: "users",
          localField: "attendees_ids",
          foreignField: "_id",
          as: "attendees",
        },
      },
      {
        $lookup: {
          from: "users",
          localField: "going_ids",
          foreignField: "_id",
          as: "going",
        },
      },
      {
        $lookup: {
          from: "users",
          localField: "maybe_ids",
          foreignField: "_id",
          as: "maybe",
        },
      },
      {
        $lookup: {
          from: "users",
          localField: "not_going_ids",
          foreignField: "_id",
          as: "not_going",
        },
      },
    ]);
    return meetings.reverse();
  } catch (err) {
    return false;
  }
}

async function getRecMeetings(tags) {
  try {
    let meetings = await getAll();
    meetings = meetings.filter((meeting) =>
      meeting.tags.some((t) => tags.includes(t))
    );
    return meetings.reverse();
  } catch (err) {
    return false;
  }
}

async function findOne(id) {
  try {
    const meeting = await Meeting.findOne({ _id: id }); //We make sure that names are unique, so this will give us the meeting we want
    return meeting;
  } catch (err) {
    return false;
  }
}

async function addOne(
  name,
  startDateTime,
  endDateTime,
  location,
  agenda,
  tags,
  host_id,
  attendees,
  going,
  maybe,
  notGoing,
  attendanceCode
) {
  try {
    const meeting = new Meeting({
      name: name,
      start_date_time: startDateTime,
      end_date_time: endDateTime,
      location: location,
      agenda: agenda,
      tags: tags,
      host_id: host_id,
      attendees: attendees,
      going: going,
      maybe: maybe,
      notGoing: notGoing,
      attendance_code: attendanceCode,
    });
    await meeting.save();
    return meeting;
  } catch (err) {
    return false;
  }
}

async function updateOne(
  id,
  name,
  startDateTime,
  endDateTime,
  location,
  agenda,
  tags,
  hostId,
  attendanceCode
) {
  try {
    let original = await Meeting.findOne({ _id: mongoose.Types.ObjectId(id) });
    let m = await Meeting.findOneAndUpdate(
      { _id: mongoose.Types.ObjectId(id) },
      {
        $set: {
          name: name ? name : original.name,
          start_date_time: startDateTime
            ? startDateTime
            : original.start_date_time,
          end_date_time: endDateTime ? endDateTime : original.end_date_time,
          location: location ? location : original.location,
          agenda: agenda ? agenda : original.agenda,
          host_id: hostId ? hostId : original.host_id,
          attendance_code: attendanceCode
            ? attendanceCode
            : original.attendance_code,
        },
        $addToSet: {
          tags: tags ? tags : [],
        },
      }
    );
    return m;
  } catch (err) {
    return false;
  }
}

async function patchOne(id, tags) {
  try {
    let m = await Meeting.findOneAndUpdate(
      { _id: mongoose.Types.ObjectId(id) },
      {
        $set: {
          tags: tags,
        },
      }
    );
    return m;
  } catch (err) {
    return false;
  }
}

async function updateAttendees(userId, meetingId) {
  try {
    userId = userId ? mongoose.Types.ObjectId(userId) : null;
    meetingId = meetingId ? mongoose.Types.ObjectId(meetingId) : null;
    let m = await Meeting.findOneAndUpdate(
      { _id: meetingId },
      {
        $addToSet: { attendees_ids: userId },
      }
    );
    return m;
  } catch (err) {
    return false;
  }
}

async function updateRSVPStatus(id, goingId, maybeId, notGoingId) {
  try {
    // turn ids into mongo ObjectIds
    goingId = goingId ? mongoose.Types.ObjectId(goingId) : null;
    maybeId = maybeId ? mongoose.Types.ObjectId(maybeId) : null;
    notGoingId = notGoingId ? mongoose.Types.ObjectId(notGoingId) : null;

    // first add they user to the list of going
    // then remove the user from the other 2 (because they are mutually exclusive)
    // example: if `going` contains a user, it removes the user from maybe and
    // notGoing if they exist, because they are now going
    let m = "";
    if (goingId) {
      m = await Meeting.findOneAndUpdate(
        { _id: mongoose.Types.ObjectId(id) },
        {
          $addToSet: { going_ids: goingId },
          $pull: { maybe_ids: goingId, not_going_ids: goingId },
        },
        { multi: true }
      );
    } else if (maybeId) {
      m = await Meeting.findOneAndUpdate(
        { _id: mongoose.Types.ObjectId(id) },
        {
          $addToSet: { maybe_ids: maybeId },
          $pull: { going_ids: maybeId, not_going_ids: maybeId },
        },
        { multi: true }
      );
    } else if (notGoingId) {
      m = await Meeting.findOneAndUpdate(
        { _id: mongoose.Types.ObjectId(id) },
        {
          $addToSet: { not_going_ids: notGoingId },
          $pull: { going_ids: notGoingId, maybe_ids: notGoingId },
        },
        { multi: true }
      );
    }
    return m;
  } catch (err) {
    return false;
  }
}

async function deleteOne(id) {
  try {
    const meeting = await Meeting.deleteOne({
      _id: mongoose.Types.ObjectId(id),
    });
    return meeting;
  } catch (err) {
    return false;
  }
}

async function getAttendanceCodes() {
  try {
    const codes = await Meeting.find({}, { attendance_code: 1 });
    return codes;
  } catch (err) {
    return false;
  }
}

module.exports = Object.freeze({
  getAll,
  findOne,
  addOne,
  updateOne,
  patchOne,
  deleteOne,
  updateRSVPStatus,
  getAttendanceCodes,
  getRecMeetings,
  updateAttendees,
});
