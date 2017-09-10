var bodyParser = require('body-parser');
var passport = require('passport');
var LocalStrategy = require('passport-local').Strategy;
var session = require('express-session');
var mongoose = require('mongoose');
var adminModel = mongoose.model('admin');
var path = require('path');

module.exports = {
    LocalStrategy: new LocalStrategy(
        function(username, password, done) {
            console.log(username, password);

            adminModel.find({ username: username, password: password }, function(err, res) {
                if (res) {
                    return done(null, res)
                } else {
                    return done(null, false, { message: 'unable to login' });

                }
            })
        }
    ),
    serialize: function(user, done) {
        done(null, user);
    },

    deserialize: function(user, done) {
        done(null, user);
    }
}