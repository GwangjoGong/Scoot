// Load Packages
var mongoose = require('mongoose');
var express = require('express');
var app = express();
var bodyParser = require('body-parser');

//configure app to use bodyParser
app.use(bodyParser.urlencoded({extended:true}));
app.use(bodyParser.json());


//run server
var server = app.listen(80, function(){
  console.log("Express server has started on port 80");
});

//configure socket.io
var io = require('socket.io').listen(server);

//configure mongoose
var db = mongoose.connection;
db.on('error', function(){
    console.log('Connection Failed!');
});
db.on('open', function() {
    console.log('Connected!');
});


//Define model
var User = require('./models/user');
var Car = require('./models/car');

//configure router
var router = require('./routes')(app, User,Car);


//------------------------------------------------------------------
//------------Internal server matching algorithm--------------------
//------------------------------------------------------------------

var passengerArray = [];
var driverArray = [];

io.sockets.on('connection',function(socket){

  socket.on('match_passenger',function(data){
    var phone = data.phone;
    var start = data.start;
    var dest = data.dest;
    var s_lat = data.s_lat;
    var s_lng = data.s_lng;
    var d_lat = data.d_lat;
    var d_lng = data.d_lng;
    passengerArray.push({socket: socket, id: socket.id,phone: phone,start: start,s_lat: s_lat,s_lng: s_lng, dest: dest,d_lat : d_lat,d_lng: d_lng});
  });
  socket.on('passenger_exit',function(){
    removePassengerByID(socket.id);
  });
  socket.on('match_driver',function(data){
    var name = data.name;
    var phone = data.phone;
    var carkind = data.carkind;
    var carnum = data.carnum;
    driverArray.push({socket: socket, id: socket.id,name: name, phone: phone, carkind: carkind, carnum: carnum});
  });
  socket.on('driver_exit',function(data){
    removeDriverByID(socket.id);
  });
  socket.on('driver_confirm',function(data){
    socket.to(data).emit('passenger_on_car');
  });
  socket.on('driver_arrive',function(data){
    socket.to(data.passenger).emit('passenger_finish');
  });
  socket.on('driver_on_drive',function(data){
    var lat = data.lat;
    var lng = data.lng;
    socket.to(data.passenger).emit('driver_location',{lat:lat, lng: lng});
  });

  socket.on('on_share',function(duration){
    var availableCars = [];
    var i;
    Car.find({},function(err,cars){
      if(err) throw err;
      for(i = 0; i<cars.length; i++){
        if(checkAvailable(cars[i].available,duration)){
          availableCars.push(cars[i]);
        }
      }
      socket.emit('data',availableCars);
    });
  });

  socket.on('on_rent',function(data){
    var carnum = data.carnum;
    var duration = data.duration;  //duration = String[]
    Car.findOne({carnum: carnum},function(err,car){
      if(err) throw err;
      if(!car) throw err;
      var available = car.available;
      var i,j;
      for(i=0; i<duration.length; i++){
        for(j=0; j<available.length; j++){
          if(duration[i].date===available[j].date){
            available.splice(j,1);
          }
        }
      }
      car.available = available;

      car.save(function(err){
        if(err) throw err;
      });
      socket.emit('rent_success');
    });
  });

});

function checkAvailable(available,duration){
  var cnt = 0;
  var i,j;
  for(i=0; i<duration.length; i++){
    for(j=0; j<available.length; j++){
      if(duration[i].date === available[j].date){
        cnt++;
        continue;
      }
    }
  }
  if(cnt === duration.length){
    return true;
  }else{
    return false;
  }
}


function doMatch(){
  var passenger = passengerArray.shift();
  var match_success = false;
  var i;
  setTimeout(function(){
    for(i = 0; i<driverArray.length; i++){
      driverArray[i].socket.emit('match_info',{start: passenger.start,s_lat: passenger.s_lat, s_lng: passenger.s_lng, dest: passenger.dest, d_lat: passenger.d_lat, d_lng : passenger.d_lng});
      driverArray[i].socket.once('driver_accept',function(data){
        var driver_name = data.name;
        var driver_phone = data.phone;
        var driver_carkind = data.carkind;
        var driver_carnum = data.carnum;
        passenger.socket.emit('passenger_match_success',{driver_name: driver_name,driver_phone: driver_phone,driver_carkind:driver_carkind,driver_carnum:driver_carnum});
        this.emit('driver_match_success',{passenger: passenger.id,passenger_phone: passenger.phone});
        match_success = true;
      });
      if(match_success) return;
    }
  },3000);
  if(!match_success){
    passengerArray.push(passenger);
  }
}

function removeDriverByID(id){
  var i;
  for(i=0;i<driverArray.length;i++){
    if(driverArray[i].id===id){
      driverArray.splice(i,1);
    }
  }
}

function removePassengerByID(id){
  var i;
  for(i=0;i<passengerArray.length;i++){
    if(passengerArray[i].id===id){
      passengerArray.splice(i,1);
    }
  }
}

setInterval(function(){
  if(passengerArray.length!=0){
    doMatch();
  }
},3200);
