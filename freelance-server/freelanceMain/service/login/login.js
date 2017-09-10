var path = require('path');
var hashService = require(path.join(__dirname, '..', 'util', 'encryption'));
var check = require(path.join(__dirname, '..', 'util', 'checkValidObject'));

/**
 * gets formatted data for a new user
 */
exports.getNewUser = function(user) {
    var salt = hashService.createSalt();
    var hashPwd = hashService.hashPwd(salt, user.password);
    var newUser = {
        name: user.name,
        userName: user.username,
        phone: user.phoneNo,
        salt: salt,
        hashPwd: hashPwd,
        image: "",
        qualification: [],
        mode: "None",
        gcmToken: '',
        favourites: []
    };
    return newUser;
};

/**
 * matching both the user from database and logging user
 * matches by generating the hash for the particular user by matching 
 * hash from (salt and password entered by user) and  hashed password already 
 * in database
 */

exports.matchUser = function(user, dbuser) {
    if (!check.isUndefinedOrNull(user) && !check.isUndefinedOrNull(dbuser)) {
        if (hashService.hashPwd(dbuser.salt, user.password) == dbuser.hashPwd) {
            return true;
        } else {
            return false;
        }
    } else {
        return false;
    }
};