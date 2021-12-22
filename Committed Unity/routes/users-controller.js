const User = require("../models/User");

let tags = [
  "Affordable Housing Act",
  "Budget",
  "Elections",
  "Education",
  "Healthcare",
  "HEART",
  "Housing",
  "Proposal",
  "Safety",
  "Sustainability",
  "Transportation",
  "Urban Planning",
  "Zoning",
];

let similarTags = {
  "Affordable Housing Act": ["Housing", "Urban Planning", "Zoning"],
  Budget: ["Elections", "Proposal"],
  Elections: ["Education", "Proposal"],
  Education: ["Budget", "Safety"],
  Healthcare: ["Budget", "Safety"],
  HEART: ["Safety", "Healthcare", "Education"],
  Housing: ["Affordable Housing Act", "Transportation", "Zoning"],
  Proposal: ["Budget", "Elections", "HEART"],
  Safety: ["Healthcare", "HEART", "Zoning"],
  Sustainability: ["Education", "Transportation", "Urban Planning"],
  Transportation: ["Sustainability", "Urban Planning"],
  "Urban Planning": [
    "Affordable Housing Act",
    "Housing",
    "Sustainability",
    "Transportation",
    "Zoning",
  ],
  Zoning: ["Affordable Housing Act", "Elections", "Housing", "Urban Planning"],
};

async function findOne(username) {
  try {
    const user = await User.find({ username: username });
    // returns a User object, or undefined if it doesn't exist
    return user[0];
  } catch (err) {
    return false;
  }
}

async function findOneID(id) {
  try {
    const user = await User.find({ _id: id });
    // returns a User object, or undefined if it doesn't exist
    return user[0];
  } catch (err) {
    return false;
  }
}

async function findAll() {
  try {
    const users = await User.find({});
    return users;
  } catch (err) {
    return false;
  }
}

async function findAllTags() {
  return tags;
}

async function getSimilarTags(username) {
  try {
    const user = await findOne(username);
    if (user.interests.length === 0) {
      const recTags = findAllTags();
      return recTags;
    }
    let simTags = user.interests.map((tag) => similarTags[tag]);
    let recommendedTags = [];
    simTags.forEach((tags) => {
      for (let i = 0; i < tags.length; i++) {
        if (!user.interests.includes(tags[i])) {
          recommendedTags.push(tags[i]);
        }
      }
    });
    //
    return recommendedTags;
  } catch (err) {
    return false;
  }
}

async function addTag(tag) {
  if (!tags.includes(tag)) {
    tags.push(tag);
  }
  return tags;
}

async function addSimilarTags(tag, simTags) {
  if (!(tag in similarTags)) {
    similarTags[tag] = simTags;
  }
  return similarTags;
}

async function addOne(username, password, first_name, last_name, is_admin) {
  const user = new User({
    username: username,
    password: password,
    first_name: first_name,
    last_name: last_name,
    points: 0,
    badges: ["None", undefined],
    interests: [],
    is_admin: is_admin,
  });
  try {
    await user.save();
    return user;
  } catch (err) {
    return false;
  }
}

async function deleteOne(username) {
  try {
    const user = await User.deleteOne({ username: username });
    await user.save();
    return user;
  } catch (err) {
    return false;
  }
}

async function hasAdminPrivilege(username) {
  try {
    const user = await findOne(username);
    return user.is_admin;
  } catch (err) {
    return false;
  }
}

async function updateUsername(old_username, new_username) {
  try {
    const user = await User.updateOne(
      { username: old_username },
      { $set: { username: new_username } }
    );
    return user;
  } catch (err) {
    return false;
  }
}

async function updatePassword(username, new_password) {
  try {
    const user = await User.updateOne(
      { username: username },
      { $set: { password: new_password } }
    );
    return user;
  } catch (err) {
    return false;
  }
}

async function authenticateUser(username_input, password_input) {
  try {
    const user = await findOne(username_input);
    if (user === undefined) {
      return undefined;
    }
    if (user.password === password_input) {
      return [true, user];
    }
    return [false, user];
  } catch (err) {
    return err;
  }
}

async function addInterest(username, interest) {
  try {
    const user = await User.updateOne(
      { username: username },
      { $addToSet: { interests: interest } }
    );
    return user;
  } catch (err) {
    return false;
  }
}

async function removeInterest(username, interest) {
  try {
    const user = await User.updateOne(
      { username: username },
      { $pull: { interests: interest } }
    );
    return user;
  } catch (err) {
    return false;
  }
}

async function incrementPoint(username) {
  try {
    let user = await User.findOneAndUpdate(
      { username: username },
      { $inc: { points: 1 }, $set: { badges: ["None", undefined] } }
    );
    user.points++;
    let newBadge = ["None", undefined];
    let today = new Date().toLocaleDateString();
    if (user.points >= 15) {
      newBadge = ["Gold", today];
    } else if (user.points >= 10) {
      newBadge = ["Silver", today];
    } else if (user.points >= 5) {
      newBadge = ["Bronze", today];
    }
    user = await User.findOneAndUpdate(
      { username: username },
      { $set: { badges: newBadge } }
    );
    await user.save();
    return user;
  } catch (err) {
    return err;
  }
}

module.exports = Object.freeze({
  findOne,
  findOneID,
  findAll,
  findAllTags,
  addOne,
  addTag,
  addSimilarTags,
  deleteOne,
  getSimilarTags,
  hasAdminPrivilege,
  updateUsername,
  updatePassword,
  authenticateUser,
  addInterest,
  removeInterest,
  incrementPoint,
});
