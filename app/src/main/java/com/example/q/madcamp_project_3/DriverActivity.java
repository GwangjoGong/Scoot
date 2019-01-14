package com.example.q.madcamp_project_3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class DriverActivity extends AppCompatActivity {

    public static Socket mSocket;

    private TextView disable_tv, enable_tv;
    private ImageView disable_iv, enable_iv;
    private Switch switch_match;

    public static String name, phone, token, carkind, carnum;


    private static final String URL_SERVER = "http://143.248.140.106:1580";
    private static final String URL_DRIVER = "http://143.248.140.106:1580/api/driver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        initView();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        Intent data = getIntent();
        name = data.getStringExtra("name");
        phone = data.getStringExtra("phone");
        token = data.getStringExtra("token");

        try{
            Map<String,Object> driver_info = new LinkedHashMap<>();
            driver_info.put("token",token);
            byte[] driverDataBytes = LoginActivity.parseParameter(driver_info);
            String response = LoginActivity.sendPost(driverDataBytes,URL_DRIVER);

            JSONObject res = new JSONObject(response);
            carkind = res.getString("carkind");
            carnum = res.getString("carnum");
        }catch (Exception e){
            e.printStackTrace();
        }


        try {
            mSocket = IO.socket(URL_SERVER);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        switch_match.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JSONObject data = new JSONObject();
                try {
                    data.put("name", name);
                    data.put("phone", phone);
                    data.put("carkind",carkind);
                    data.put("carnum",carnum);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                if(isChecked){
                    setEnable();
                    mSocket.emit("match_driver",data);
                    mSocket.on("match_info",onDriverMatch);
                }else{
                    setDisable();
                    mSocket.emit("driver_exit");
                    mSocket.off("match_info");
                }
            }
        });
    }


    protected void initView() {
        disable_iv = findViewById(R.id.disable_iv);
        Glide.with(this).load(R.drawable.disable_icon).into(disable_iv);
        enable_iv = findViewById(R.id.enable_iv);
        Glide.with(this).load(R.drawable.enable_image).into(enable_iv);
        disable_tv = findViewById(R.id.disable_tv);
        enable_tv = findViewById(R.id.enable_tv);
        switch_match = findViewById(R.id.switch_match);
    }

    private void setEnable() {
        enable_iv.setVisibility(View.VISIBLE);
        enable_tv.setVisibility(View.VISIBLE);
        disable_iv.setVisibility(View.GONE);
        disable_tv.setVisibility(View.GONE);
    }

    private void setDisable() {
        enable_iv.setVisibility(View.GONE);
        enable_tv.setVisibility(View.GONE);
        disable_iv.setVisibility(View.VISIBLE);
        disable_tv.setVisibility(View.VISIBLE);
    }

    private Emitter.Listener onDriverMatch = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject receivedData = (JSONObject) args[0];
            try {
                String start = receivedData.getString("start");
                String dest = receivedData.getString("dest");
                double s_lat = receivedData.getDouble("s_lat");
                double s_lng = receivedData.getDouble("s_lng");
                double d_lat = receivedData.getDouble("d_lat");
                double d_lng = receivedData.getDouble("d_lng");


                Intent matchinfoIntent = new Intent(DriverActivity.this,DriverMatchInfoActivity.class);
                matchinfoIntent.putExtra("start",start);
                matchinfoIntent.putExtra("dest",dest);
                matchinfoIntent.putExtra("s_lat",s_lat);
                matchinfoIntent.putExtra("s_lng",s_lng);
                matchinfoIntent.putExtra("d_lat",d_lat);
                matchinfoIntent.putExtra("d_lng",d_lng);

                mSocket.off("match_info");
                startActivity(matchinfoIntent);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        switch_match.setChecked(false);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mSocket.disconnect();
    }
}
