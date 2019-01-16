var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost:27017/testUserDB');

var Schema = mongoose.Schema;

var userSchema = new Schema({
  //Needed User informations
  id : String,
  password : String,
  name : String,
  phone : String,

  //userType :=
  //no license, no vehicle : 0
  //with license, no vehicle : 1
  //with license, with vehicle : 2
  userType : {type: Number, min : 0, max : 2},
  license : String,
  carkind : String,
  carnum : String,
  //to be updated.

});


module.exports = mongoose.model('User',userSchema);
