var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var passport = require('passport');
var bodyParser = require('body-parser');
var session = require('express-session');


var mongooseDb = require(path.join(__dirname, 'global', 'mongoose'));
var redisDb = require(path.join(__dirname, 'global', 'redis'));
var passportStrategy = require(path.join(__dirname, 'service', 'login', 'passport'));

var app = express();

/**
 * mongoose and redis initialization
 */

mongooseDb.initDb();
redisDb.startRedis();

/**
 * passport stuff
 */

app.use(passport.initialize());
app.use(passport.session());
app.use(session({ secret: '#hello@4433' }));
app.use(bodyParser.json());

app.use(logger('dev'));
app.use(cookieParser());
app.set('views', path.join(__dirname, 'views'));
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.static(path.join(__dirname, 'bower_components')));
app.use(express.static(path.join(__dirname, 'views')));

app.use(bodyParser.urlencoded({
    extended: true,
    parameterLimit: 10000000000,
    limit: 1024 * 1024 * 10
}));

app.use(bodyParser.json({
    extended: true,
    parameterLimit: 10000000000,
    limit: 1024 * 1024 * 10
}));




passport.use(passportStrategy.LocalStrategy);
passport.serializeUser(passportStrategy.serialize);
passport.deserializeUser(passportStrategy.deserialize);

app.use(passport.initialize());
app.use(passport.session());


/**
 * Render images 
 */
app.use('/images', express.static('images'))


app.use('/', require('./routes/index'));

/**
 * Login apis for a user
 */
app.use('/api/user', require('./routes/users/login'));


/**
 * otp for phone number validation
 */
app.use('/api/otp', require('./routes/users/otp'));


/**
 * user details apiData
 * all personal datas for a user
 * experiece qualification profile_pic etc.
 */
app.use('/api/userData', require('./routes/users/user'));


/**
 * api for apis
 */
app.use('/api/apiData', require('./routes/api'));


/**
 * public data for users
 * mainly required in dashboard
 */
app.use('/api/public', require('./routes/users/public'));

/**
 * email verification routes
 */
app.use('/api/auth', require('./routes/users/emailVerify'));


/**
 * GCM token Routes
 */
app.use('/api/token', require('./routes/users/gcmTokens'));

/**
 * set constant data for global fields
 */
app.use('/api/constant', require('./routes/extra/constants'));

/**
 * upload user data to the server
 */
app.use('/api/app', require('./routes/extra/appUpload'));

/**
 * projects realted api
 */
app.use('/api/project', require('./routes/project/projects'))

/**
 * chatroom realted api
 */
app.use('/api/chatroom', require('./routes/project/chat'))

/**
 * admin controll
 */
app.use('/api/admin', require('./routes/extra/admin.js'))

/**
 * hire data
 */
app.use('/api/hire', require('./routes/project/hire'));

module.exports = app;