const mongoose = require("mongoose");

const db = {};
db.mongoose = mongoose;
db.url = process.env.MONGODB_URI || "mongodb://localhost:27017/test";
//connection to atlas or local if not in production

module.exports = db;
