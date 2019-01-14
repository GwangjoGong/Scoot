package com.example.q.madcamp_project_3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


import io.socket.emitter.Emitter;

public class DriverMatchInfoActivity extends AppCompatActivity {

    private TextView start_tv,dest_tv;
    private ImageView arrow_iv;
    private Button btn_accept,btn_deny;

    public static String start, dest;
    public static double s_lat,s_lng,d_lat,d_lng;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_match_info);
        initView();

        Intent data = getIntent();
        start = data.getStringExtra("start");
        dest = data.getStringExtra("dest");
        s_lat = data.getDoubleExtra("s_lat",-1);
        s_lng = data.getDoubleExtra("s_lng",-1);
        d_lat = data.getDoubleExtra("d_lat",-1);
        d_lng = data.getDoubleExtra("d_lng",-1);

        start_tv.setText(start);
        dest_tv.setText(dest);

        DriverActivity.mSocket.on("driver_match_success",onSuccess);

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data = new JSONObject();
                try {
                    data.put("name",DriverActivity.name);
                    data.put("phone",DriverActivity.phone);
                    data.put("carkind",DriverActivity.carkind);
                    data.put("carnum",DriverActivity.carnum);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                DriverActivity.mSocket.emit("driver_accept",data);
            }
        });

        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        DriverActivity.mSocket.off("driver_match_success");
        DriverActivity.mSocket.emit("driver_exit");
    }

    private void initView(){
        start_tv = findViewById(R.id.start_tv);
        dest_tv = findViewById(R.id.dest_tv);
        arrow_iv = findViewById(R.id.arrow_iv);
        arrow_iv.setImageResource(R.drawable.arrow_icon);
        btn_accept = findViewById(R.id.btn_accept);
        btn_deny = findViewById(R.id.btn_deny);
    }

    private Emitter.Listener onSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                System.out.println("Driver Match Success");
                String pNum = data.getString("passenger_phone");
                String pid = data.getString("passenger");
                Intent intent = new Intent(DriverMatchInfoActivity.this,DriverMatchSuccessActivity.class);
                intent.putExtra("phone",pNum);
                intent.putExtra("passenger",pid);
                finish();
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

}
