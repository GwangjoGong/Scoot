package com.example.q.madcamp_project_3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;

import org.json.JSONException;
import org.json.JSONObject;

public class DriverMatchSuccessActivity extends AppCompatActivity {

    private TextView onMatch_psg_start_tv, onMatch_psg_phone_tv;
    private Button btn_driver_call, btn_driver_navigate, btn_driver_confirm;

    public static String passenger;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_match_success);
        initView();
        Intent data = getIntent();
        final String pPhone = data.getStringExtra("phone");
        passenger = data.getStringExtra("passenger");

        onMatch_psg_phone_tv.setText(pPhone);
        onMatch_psg_start_tv.setText(DriverMatchInfoActivity.start);
        btn_driver_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DriverMatchSuccessActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DriverMatchSuccessActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+pPhone));
                    startActivity(intent);
                }
            }
        });

        btn_driver_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    JSONObject psg = new JSONObject();
                    psg.put("passenger",passenger);
                    DriverActivity.mSocket.emit("driver_confirm",passenger);
                    Intent intent = new Intent(DriverMatchSuccessActivity.this,DriverAfterConfirmActivity.class);
                    startActivity(intent);
                    finish();
                }catch(JSONException e){
                    e.printStackTrace();
                }

            }
        });

        btn_driver_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.kakao.kakaonavi.Location destination = Location.newBuilder(DriverMatchInfoActivity.start,DriverMatchInfoActivity.s_lng,DriverMatchInfoActivity.s_lat).build();
                NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84).setVehicleType(VehicleType.TWO_WHEEL).setRpOption(RpOption.SHORTEST).build();
                KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);

                KakaoNaviParams params = builder.build();

                KakaoNaviService.getInstance().navigate(DriverMatchSuccessActivity.this,params);
            }
        });

    }

    @Override
    public void onBackPressed(){

    }

    private void initView(){
        onMatch_psg_phone_tv = findViewById(R.id.onMatch_psg_phone_tv);
        onMatch_psg_start_tv = findViewById(R.id.onMatch_psg_start_tv);
        btn_driver_call = findViewById(R.id.btn_driver_call);
        btn_driver_navigate = findViewById(R.id.btn_driver_navigate);
        btn_driver_confirm = findViewById(R.id.btn_driver_confirm);
    }
}
