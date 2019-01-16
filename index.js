module.exports = function(app, User,Car)
{
  //implement APIs

  //Create User by POST
  app.post('/api/signup',function(req,res){
    var user = new User();
    user.id = req.body.id;
    user.password = req.body.password;
    user.name = req.body.name;
    user.phone = req.body.phone;
    user.userType = req.body.userType;
    user.license = req.body.license;
    user.carkind = req.body.carkind;
    user.carnum = req.body.carnum;

    user.save(function(err){
      if(err){
        console.error(err);
        res.json({result: '1'});
        return;
      }
      res.json({result: '0'});
    });
  });


  app.post('/api/kakao',function(req,res){
    User.findOne({id:req.body.id,name:req.body.name},function(err,user){
      if(err) return res.status(500).send({error: err});
      if(!user) return res.json({success: '1'});
      res.json({success: '0',token: user._id,name: user.name,phone: user.phone,userType: user.userType});
    });
  });

  app.post('/api/check',function(req,res){
    User.findOne({id:req.body.id},function(err,user){
      if(err) return res.status(500).send({errer: err});
      if(user) return res.json({success: '1'});
      res.json({success:'0'});
    });
  });

  //Login by post
  app.post('/api/login',function(req,res){
    User.findOne({id:req.body.id, password: req.body.password},function(err,user){
      if(err) return res.status(500).send({error: err});
      if(!user) return res.status(404).json({success: '1',error: 'user not found'});
      res.json({success: '0',token: user._id,name: user.name,phone:user.phone,userType: user.userType});
    });
  });

  app.post('/api/driver',function(req,res){
    User.findOne({_id:req.body.token},function(err,user){
      if(err) return res.status(500).send({error: err});
      if(!user) return res.status(404).json({error: 'user not found'});
      res.json({success: '0',carkind: user.carkind, carnum: user.carnum});
    });
  });

  //GET every users
  app.get('/api/users',function(req,res){
    User.find(function(err,users){
      if(err) return res.status(500).send({error: 'database failure'});
      res.json(users);
    });
  });

  //GET single user information(userType) with token
  app.get('/api/users/:token',function(req,res){
    User.findOne({_id:req.params.token},function(err,user){
      if(err) return res.status(500).json({error: err});
      if(!user) return res.status(404).json({error: 'user not found'});
      res.json({userType: user.userType});
    });
  });

  //UPDATE user informations
  app.put('/api/users/:user_id',function(req,res){
    User.findById(req.params.user_id, function(err,user){

      if(err) return res.status(500).json({error: 'database failure'});
      if(!user) return res.status(404).json({error: 'book not found'});

      if(req.body.userType) user.userType = req.body.userType;

      user.save(function(err){
        if(err) res.status(500).json({error : 'failed to update'});
        res.json({message: 'user updated'});
      });
    });
  });

  //DELETE all users
  app.delete('/api/users/deleteAll',function(req,res){
    User.deleteMany({},function(err,output){
      if(err) return res.status(500).json({error: err});
      res.json({message: 'All users deleted'});
      res.status(204).end();
    });
  });

  //DELETE user
  app.delete('/api/users/:user_id',function(req,res){
    User.deleteOne({_id:req.params.user_id},function(err,output){
      if(err) return res.status(500).json({error:err});
      if(output.n!=1) return res.status(404).json({error: 'user not found'});
      res.json({message: 'user deleted'});
      res.status(204).end();
    });
  });


  //------------------------------------------------------------
  //------------------Car sharing code section------------------
  //------------------------------------------------------------
  app.get('/api/car',function(req,res){
    Car.find(function(err,cars){
      if(err) return res.status(500).send({error: 'database failure'});
      res.json(cars);
    });
  });

  app.post('/api/car',function(req,res){
    var car = new Car();
    car.carkind = req.body.carkind;
    car.carnum = req.body.carnum;
    car.place = req.body.place;
    car.lat = req.body.lat;
    car.lng = req.body.lng;
    car.owner = req.body.owner;
    car.price = req.body.price;
    car.available = JSON.parse(req.body.available);

    car.save(function(err){
      if(err){
        console.error(err);
        res.json({result: '1'});
        return;
      }
      res.json({result: '0'});
    });
  });


  app.delete('/api/car/deleteAll',function(req,res){
    Car.deleteMany({},function(err,output){
      if(err) return res.status(500).json({error: err});
      res.json({message: 'All cars deleted'});
      res.status(204).end();
    });
  });

  app.delete('/api/car/:car_id',function(req,res){
    Car.deleteOne({_id:req.params.car_id},function(err,output){
      if(err) return res.status(500).json({error:err});
      if(output.n!=1) return res.status(404).json({error: 'car not found'});
      res.json({message: 'car deleted'});
      res.status(204).end();
    });
  });

  //UPDATE user informations
  app.put('/api/car/:car_kind',function(req,res){
    User.findOne({carkind:req.params.carkind},function(err,car){

      if(err) return res.status(500).json({error: 'database failure'});
      if(!car) return res.status(404).json({error: 'car not found'});

      if(req.body.place) car.place = req.body.place;

      car.save(function(err){
        if(err) res.status(500).json({error : 'failed to update'});
        res.json({message: 'car updated'});
      });
    });
  });
};
