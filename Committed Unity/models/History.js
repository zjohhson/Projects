const mongoose = require("mongoose");
const Schema = mongoose.Schema;

const HistorySchema = new Schema({
  // we don't have a id field, by default mongoDB creates this field, which is our Primary Key
  username: {
    type: String,
    required: true,
  },
  date: {
    type: String, // mm/yyyy
    required: true,
  },
  points: {
    type: Number,
    required: true,
  },
});

// mongoose will automatically create the collection for our DB
module.exports = mongoose.model("History", HistorySchema);
