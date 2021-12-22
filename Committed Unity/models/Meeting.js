const mongoose = require("mongoose");
const Schema = mongoose.Schema;

const MeetingSchema = new Schema({
  // note that we don't have a id field, by default mongoDB creates this field, which is our Primary Key
  name: {
    type: String,
    required: true,
  },
  start_date_time: {
    type: Date,
    required: true,
  },
  end_date_time: {
    type: Date,
    required: true,
  },
  location: {
    type: String,
    required: true,
  },
  agenda: {
    type: String,
    required: true,
  },
  tags: {
    type: [String], // TODO: make a tag type
    required: false,
  },
  host_id: {
    // reference to _id field in the User collection
    type: Schema.Types.ObjectId,
    ref: "User",
    required: true,
  },
  attendees_ids: {
    type: [Schema.Types.ObjectId],
    ref: "User",
    required: false,
  },
  going_ids: {
    type: [Schema.Types.ObjectId],
    ref: "User",
    required: false,
  },
  maybe_ids: {
    type: [Schema.Types.ObjectId],
    ref: "User",
    required: false,
  },
  not_going_ids: {
    type: [Schema.Types.ObjectId],
    ref: "User",
    required: false,
  },
  attendance_code: {
    type: String,
    required: true,
  },
});

// mongoose will automatically create the collection for our DB
module.exports = mongoose.model("Meeting", MeetingSchema);
