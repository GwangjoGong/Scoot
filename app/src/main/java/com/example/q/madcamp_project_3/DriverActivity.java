package com.example.q.madcamp_project_3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class DriverActivity extends AppCompatActivity {

    public static Socket mSocket;

    private static TextView disable_tv, enable_tv;
    private static ImageView disable_iv, enable_iv;
    private Button btn_profile;
    private SwipeButton swipe_btn;


    private static final String URL_SERVER = "http://143.248.140.106:1580";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        initView();

        try {
            mSocket = IO.socket(URL_SERVER);
            mSocket.connect();
        }catch (URISyntaxException e){
            e.printStackTrace();
        }

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    protected void initView(){
        disable_iv = findViewById(R.id.disable_iv);
        disable_iv.setImageResource(R.drawable.disable_icon);
        enable_iv = findViewById(R.id.enable_iv);
        enable_iv.setImageResource(R.drawable.enable_image);
        disable_tv = findViewById(R.id.disable_tv);
        enable_tv = findViewById(R.id.enable_tv);
        btn_profile = findViewById(R.id.button_profile);
        swipe_btn = findViewById(R.id.swipe_btn);
    }

    public static void setEnable(){
        enable_iv.setVisibility(View.VISIBLE);
        enable_tv.setVisibility(View.VISIBLE);
        disable_iv.setVisibility(View.GONE);
        disable_tv.setVisibility(View.GONE);
    }
    public static void setDisable(){
        enable_iv.setVisibility(View.GONE);
        enable_tv.setVisibility(View.GONE);
        disable_iv.setVisibility(View.VISIBLE);
        disable_tv.setVisibility(View.VISIBLE);
    }
}
