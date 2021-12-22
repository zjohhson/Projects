const express = require('express');
const checkThat = require('./middleware');
const Freets = require('../Models/Freets');

const router = express.Router();

/**
 * List all freets.
 * 
 * @name GET /api/freets
 * 
 * @return {Freet[]} - list of all stored freets
 */
 router.get('/', (req, res) => {
  res.status(200).json(Freets.findAll()).end();
});

/**
 * View all freets by author.
 * 
 * @name GET /api/freets/:author
 * 
 * @return {Freet[]} - list of all stored freets by specified author
 */
 router.get('/:author?', [checkThat.usernameExists], (req, res) => {
    res.status(200).json(Freets.findFreetsByAuthor(req.params.author)).end();
  });

/**
 * Get Freet with particular Id. 
 * 
 * @name GET /api/freets/id/:id
 * 
 * @return {Freet | undefined} - freet with :id id 
 */
 router.get('/id/:id?', (req, res) => {
    res.status(200).json(Freets.findFreetById(req.params.id)).end();
  });

/**
 * Create a freet.
 * 
 * @name POST /api/freets
 * @param {string} author - author of the freet
 * @param {string} content - content of the freet
 * @return {Freet} - the created freet
 * @throws {400} - if user is not logged in
 * @throws {403} - if freet is too long
 */
router.post('/', (req, res) => {
    if (req.session.username == undefined) {
        res.status(403).json({
        error: 'You must be logged in in order to post a freet!'
        }).end();
    }
    else if (req.body.content.length > 140) {
        res.status(400).json({
            error: `Freet is longer than 140 characters.`
        }).end();
    } else {
        const freet = Freets.createFreet(req.body.content, req.session.username); 
        res.status(200).json(freet).end();
    }
});

/**
 * Edit a freet.
 * 
 * @id PUT /api/freets/:id
 * 
 * @param {string} content - the new content for the freet
 * @return {Freet} - the updated freet
 * @throws {404} - if freet does not exist
 * @throws {403} - if user is not the creator of the freet
 */
router.put('/:id?', [checkThat.freetExists, checkThat.userIsCreator], (req, res) => {
    const freet = Freets.editFreet(req.body.id, req.body.content);
    res.status(200).json(freet).end();
});

/**
 * Delete a freet.
 * 
 * @name DELETE /api/freets/:id
 * 
 * @return {Freet} - the deleted freet
 * @throws {404} - if freet does not exist
 * @throws {403} - if freet does not exist
 */
router.delete('/:id?', [checkThat.freetExists, checkThat.userIsCreator], (req, res) => {
    const freet = Freets.deleteFreet(req.params.id);
    res.status(200).json(freet).end();
});

module.exports = router;
