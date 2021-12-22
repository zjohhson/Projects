const mongoose = require("mongoose");
const Schema = mongoose.Schema;

const UserSchema = new Schema({
  // we don't have a id field, by default mongoDB creates this field, which is our Primary Key
  username: {
    type: String,
    required: true,
  },
  password: {
    type: String,
    required: true,
  },
  first_name: {
    type: String,
    required: true,
  },
  last_name: {
    type: String,
    required: true,
  },
  points: {
    type: Number,
    required: false,
  },
  badges: {
    type: [String],
    required: true,
  },
  interests: {
    type: [String],
    required: false,
  },
  is_admin: {
    type: Boolean,
    required: true,
  },
});
// mongoose will automatically create the collection for our DB
module.exports = mongoose.model("User", UserSchema);
