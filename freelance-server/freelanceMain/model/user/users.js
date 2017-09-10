var mongoose = require('mongoose');
var path = require('path');

var addressSchema = require(path.join(__dirname, 'address'));
var currentJobSchema = require(path.join(__dirname, 'jobPresent'));
var qualificationSchema = require(path.join(__dirname, 'qualification'));
var experienceSchema = require(path.join(__dirname, 'experience'));

var userSChema = mongoose.Schema({
    name: { type: String, required: true },
    userName: { type: String, required: true },
    phone: { type: String, required: true },
    salt: { type: String, required: true },
    hashPwd: { type: String, required: true },
    image: { type: String, required: false },
    address: addressSchema,
    presentJob: currentJobSchema,
    qualification: [qualificationSchema],
    experience: [experienceSchema],
    mode: { type: String, enum: ['None', 'Work', 'Hire'], required: true },
    category: [{ type: String }],
    active: { type: Boolean, default: false },
    suspend: { type: Boolean, default: false },
    gcmToken: { type: String, default: '' },
    favourites: [{ type: String }],
    certified: { type: Boolean, default: false }
});

module.exports = mongoose.model('user', userSChema);