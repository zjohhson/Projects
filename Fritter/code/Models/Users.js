const Freets = require('./Freets');

let users = [];

/**
 * @typedef User
 * @prop {string} username - alpha numeric string
 * @prop {string} password - password associated with account
 * @prop {Set} following - Set of usernames that this account follows 
 */

class Users {
  /**
   * Create an account for a user. 
   * 
   * @param {string} username - The user's username
   * @param {string} password - The user's password
   * @return {User} - the created user
   */
  static createUser(username, password) {
    const user = {username, password, following: new Set([username])};
    users.push(user);
    return user;
  }

  /**
   * Find a User by username.
   * 
   * @param {string} username - username of desired user 
   * @return {User | undefined} - user with given username, or undefined if doesnt exist
   */
  static findUser(username) {
    return users.filter(user => user.username === username)[0];
  }

  /**
   * @return {User[]} array containing all users
   */
  static getUsers() {
    return users;
  }


  /**
   * @return {String[]} array of usernames that follow user with username
   */
   static getFollowers(username) {
    var followers = users.map(u => u.username).filter(u => Users.findUser(u).following.has(username) && u != username); 
    return followers;
  }

  /**
   * Update a user's username. 
   * 
   * @param {string} username - username of user to be updared
   * @param {string} new_username - new username to be updated to
   * @return {User | undefined} - updated user with new username
   */
  static updateUsername(username, new_username) {
    const user = users.filter(user => user.username === username)[0]
    user.following.delete(username);
    user.following.add(new_username);
    user.username = new_username;
    Freets.changeCreator(username, new_username);
    return user;
  }

  /**
   * Update a user's password. 
   * 
   * @param {string} username - username of user to be changed
   * @param {string} password - new password for user
   * @return {User | undefined} - The updated user
   */
   static updatePassword(username, new_password) {
    const user = Users.findUser(username);
    user.password = new_password;
    return user;
  }

  /**
   * Delete a User from the collection.
   * 
   * @param {string} name - name of user to delete
   * @return {User | undefined} - deleted user
   */
  static deleteUser(username) {
    const deleted = users.filter(user => user.username === username)[0];
    Freets.deleteByUser(username)
    users = users.filter(user => user.username !== username);
    for(let user of users) {
      user.following.delete(username)
    }
    return deleted;
  }

  /**
   * Checks if username exists already
   * 
   * @param {string} username - The name of the User
   * @return {Bool} - True if username exists already in data
   */
  static usernameExists(username) {
    return users.filter(user => user.username === username).length > 0;
  }

  /**
   * Sign in as a user
   * 
   * @param {string} name - The username
   * @param {string} password - The password
   * @return {Bool} - Whether the signin was successful
   */
   static signIn(username, password) {
    const user = users.filter(user => user.username === username);
    if (user.length > 0) {
      return user[0].password === password;
    }
    return false;
  }

  /**
   * Adds or removes the followee from the list of user's follows.
   * 
   * @param {string} username - User doing the following/unfollowing.
   * @param {string} followee - User getting followed/unfollowed.
   * @return {boolean | undefined} Whether the user now follows the followee. undefined if fails.
   */
  static toggleFollow(username, followee) {
    const user = this.findUser(username);
    if (user === undefined) {
      return;
    }
    const following = user.following;

    if (following.has(followee)) {
      following.delete(followee);
    } else {
      following.add(followee);
    }
    return following.has(followee);
  }
}
module.exports = Users;