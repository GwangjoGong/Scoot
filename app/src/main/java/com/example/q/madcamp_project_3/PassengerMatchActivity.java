package com.example.q.madcamp_project_3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pools;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class PassengerMatchActivity extends AppCompatActivity {

    private ImageView match_gif;
    private TextView match_start,match_dest;
    private Socket mSocket = MainActivity.mSocket;

    private double start_lat, start_lng, dest_lat,dest_lng;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_match);
        initView();

        match_start.setText(PoolFragment.start_name);
        match_dest.setText(PoolFragment.dest_name);

        start_lat = PoolFragment.start_latlng.latitude;
        start_lng = PoolFragment.start_latlng.longitude;
        dest_lat = PoolFragment.dest_latlng.latitude;
        dest_lng = PoolFragment.dest_latlng.longitude;

        JSONObject data = new JSONObject();
        try{
            data.put("phone",MainActivity.phone);
            data.put("start",PoolFragment.start_name);
            data.put("dest",PoolFragment.dest_name);
            data.put("s_lat",start_lat);
            data.put("s_lng",start_lng);
            data.put("d_lat",dest_lat);
            data.put("d_lng",dest_lng);
        }catch (JSONException e){
            e.printStackTrace();
        }
        mSocket.emit("match_passenger",data);

        mSocket.on("passenger_match_success",onSuccess);
    }

    @Override
    protected void onDestroy(){

        super.onDestroy();
        mSocket.off("passenger_match_success");
        mSocket.emit("passenger_exit");
    }

    private void initView(){
        match_gif = findViewById(R.id.match_gif);
        match_gif.setImageResource(R.drawable.scoot_icon);
        match_dest = findViewById(R.id.match_dest);
        match_start = findViewById(R.id.match_start);
    }

    private Emitter.Listener onSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject data = (JSONObject) args[0];
                String dName = data.getString("driver_name");
                String dPhone = data.getString("driver_phone");
                String dCarkind = data.getString("driver_carkind");
                String dCarnum = data.getString("driver_carnum");

                Intent intent = new Intent(PassengerMatchActivity.this,PassengerMatchSuccessActivity.class);
                intent.putExtra("name",dName);
                intent.putExtra("phone",dPhone);
                intent.putExtra("carkind",dCarkind);
                intent.putExtra("carnum",dCarnum);

                startActivity(intent);
                finish();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
