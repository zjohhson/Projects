const e = require("express");

let data = [];

/**
 * @typedef Freet
 * @prop {string} id - the ID associated with the freet
 * @prop {string} author - the creator of the freet
 * @prop {string} content - the content of the freet
 * @prop {number} creationDate - Unix time of creation (in ms) for this freet.
 * @prop {boolean} edited - optional; true if freet has been edited, undefined otherwise
 * @prop {id} refreeted - Set to ths.id if not refreeted. Otherwise, points to the refreeted reference of the copied freet (ie, refreeting a refreet points to the original freet).
 * @prop {string[]} upvotes - Set of users who have liked this freet. Reference is shared among all refreets
 */

/**
 * @class Freets
 * 
 * Stores all freets. Note that all methods are static.
 * Wherever you import this class, you will be accessing the same data.
 */
class Freets {
  /**
   * Add a freet to the collection.
   * 
   * @param {string} id - the ID associated with the freet
   * @param {string} author - the creator of the freet
   * @param {string} content - the content of the freet
   * @return {Freet} - the newly created freet
   */
  static createFreet(content, author) {
    const id = data.length !== 0 ? data[data.length-1].id+1 : 0;
    const freet = { id, author, content, creationDate: Date.now(), refreeted: id, upvotes: []};
    data.push(freet);
    return freet;
  }

  /**
   * Creates a refreet (a copy of the freet with id freetId that references this freet). Freets share upvotes.
   * @param {string} freetId - The freet to copy. Can be a refreet itself.
   * @param {string} refreeter - The author refreeting the tweet
   * @returns {Freet | undefined} - the refreet
   */
  static refreet(freetId, refreeter) {
    const baseFreet = this.findFreetById(freetId);
    if (baseFreet === undefined || baseFreet.author == refreeter) {
      // Can't refreet own freet or freet that doesn't exist.
      return;
    }
    const id = data[data.length - 1].id + 1;
    const refreet = {id, author : refreeter, content : baseFreet.content, creationDate: Date.now(), refreeted: baseFreet.refreeted, upvotes: baseFreet.upvotes};
    data.push(refreet);
    return refreet;
  }

  /**
   * Find a freet by id.
   * 
   * @param {string} id - the ID of the freet to find
   * @return {Freet | undefined} - the found freet with above ID
   */
  static findFreetById(id) {
    return data.filter(freet => freet.id == id)[0];
  }
  
  /**
   * Find all freets created by author.
   * 
   * @param {string} author - the creator to find
   * @returns {Freet[] | undefined} - the found freets by author
   */
  static findFreetsByAuthor(author) {
    return data.filter(freet => freet.author === author);
  }
  
  /**
   * Find all freets created by a list of authors.
   * 
   * @param {string[]} authors - the creators to find
   * @returns {Freet[] | undefined} - the found freets by authors
   */
  static findFreetsByAuthors(authors) {
    return data.filter(freet => authors.includes(freet.author));
  }
  
  /**
   * Find all freets created by author and change their username to new author.
   * 
   * @param {string} author - the creator to find
   * @returns {Freet[] | undefined} - the found freets by author
   */
  static changeCreator(old, newUsername) {
    let userFreets = data.filter(all_freet => all_freet.author === old)
    .map(function(f) {
      f.author = newUsername;
      return f;
    });
    return;
  }
  
  /**
   * Find all current freets on Fritter.
   * 
   * @return {Freet[]} - an array of all of the freets
   */
  static findAll() {
    return data;
  }
  
  /**
   * Delete all Freets created by username.
   * 
   * @param {string} username - creator of Freets to delete
   * @return {null}
   */
  static deleteByUser(username) {
    data = data.filter(freet => freet.author !== username 
                                && this.findFreetById(freet.refreeted).author !== username);
    return;
  }
  
  /**
   * Update a freet's content.
   * 
   * @param {string} id - the id of the freet to update
   * @param {string} content - the updated content of the freet
   * @return {Freet | undefined} - the updated freet
   */
  static editFreet(id, content) {
    const freet = Freets.findFreetById(id);
    freet.content = content;
    freet.edited = true;
    for (let freet of data) {
      if (freet.refreeted === id) {
        freet.content = content;
        freet.edited = true;
      }
    }
    return freet;
  }
  
  /**
   * Toggles (adds or removes) a user from the set of upvotes for a freet.
   * @param {string} freetId - The freet
   * @param {string} user - The user
   * @return {Freet} The freet data.
   */
  static toggleUpvote(freetId, user) {
    const freet = this.findFreetById(freetId);
    if (freet === undefined) {
      return;
    }
    let upvotes = freet.upvotes;
    if (upvotes.includes(user)) {
      upvotes.splice(upvotes.indexOf(user), 1);
    } else {
      upvotes.push(user);
    }
    return freet;
  }

  /**
   * Delete a freet from the collection.
   * 
   * @param {string} id - id of freet to delete
   * @return {Freet | undefined} - deleted Freet
   */
  static deleteFreet(id) {
    const freet = Freets.findFreetById(id);
    data = data.filter(s => s.id != id && s.refreeted != id);
    return freet;
  }
}

module.exports = Freets;