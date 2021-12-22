const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');
const session = require('express-session');
const db = require('./db/db_config');

const indexRouter = require('./routes/index');
const meetingsRouter = require('./routes/meetings');
const usersRouter = require('./routes/users');
const tagsRouter = require('./routes/tags');

const app = express();

require('dotenv').config();
const isProduction = process.env.NODE_ENV === 'production';
const history = require('connect-history-api-fallback');

// Set up user session
app.use(session({
    secret: 'committedunity',
    resave: true,
    saveUninitialized: true
  }));

  
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(history());
app.use(express.static(path.join(__dirname, isProduction ? 'dist' : 'public')));

db.mongoose
  .connect(db.url, {
    useNewUrlParser: true,
    useUnifiedTopology: true
  })
  .then(() => {
    console.log("Connected to the database!");
  })
  .catch(err => {
    console.log("Cannot connect to the database!", err);
    process.exit();
  });

app.use('/', indexRouter);
app.use('/api/meetings', meetingsRouter);
app.use('/api/users', usersRouter);
app.use('/api/tags', tagsRouter);

// Catch all other routes into a meaningful error message
app.all('*', (req, res) => {
    const errorMessage = `
      Cannot find the resource <b>${req.url}</b>
      <br>
      Please use only use UI provided on the home directory <b>/</b>
      <br><br>
    `;
    res.status(404).send(errorMessage);
  });
  
module.exports = app;
