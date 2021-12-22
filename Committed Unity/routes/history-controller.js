const History = require("../models/History");

async function addOne(username, date, points) {
  const historyEntry = new History({
    username: username,
    date: date,
    points: points,
  });
  try {
    await historyEntry.save();
    return historyEntry;
  } catch (err) {
    return false;
  }
}

async function getAllHistory() {
  try {
    const allHistory = await History.find({});
    return allHistory;
  } catch (err) {
    return false;
  }
}

async function getUserLifetimePoints(username) {
  try {
    const history = await History.aggregate([
      { $match: { username: username } },
      { $group: {
          _id: null,
          total: { $sum: "$points" },
          avg: { $avg: "$points" }
        }
      }
    ])
    return history;
  } catch (err) {
    return false;
  }
}

module.exports = Object.freeze({
  getAllHistory,
  addOne,
  getUserLifetimePoints,
});
