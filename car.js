var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost:27017/testCarDB');
var Schema = mongoose.Schema;

var carSchema = new Schema({
  //Needed Car informations
  carkind : String,
  carnum : String,
  lat : String,
  lng : String,
  place : String,
  owner : String,
  price : String,
  available : Array
});


module.exports = mongoose.model('Car',carSchema);
