const express = require('express');
const path = require('path');
const session = require('express-session');
const cookieParser = require('cookie-parser');
const logger = require('morgan');
require('dotenv').config();

const indexRouter = require('./routes/index');
const freetRouter = require('./routes/freets');
const userRouter = require('./routes/users');

const history = require('connect-history-api-fallback');

const app = express();
app.use(history());

const isProduction = process.env.NODE_ENV === 'production';

app.set('view engine', 'html');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());

app.use(session({ 
  secret: 'Fritter', 
  resave: true, 
  saveUninitialized: false
}));

app.use(function(req, res, next) {
  console.log(`Processing ${req.method} ${req.url}. Session: ${req.sessionID}`);
  req.session.isInitialized = true;
  next();
});

app.use('/', indexRouter);
app.use('/api/freets', freetRouter); // all freet API calls will go to /api/freet
app.use('/api/user', userRouter); // all user API calls will go to /api/user

app.use(express.static(path.join(__dirname, isProduction ? 'dist' : 'public')));


module.exports = app;
