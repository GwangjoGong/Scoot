package com.example.q.madcamp_project_3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;

import org.json.JSONException;
import org.json.JSONObject;

public class DriverAfterConfirmActivity extends AppCompatActivity {

    private TextView afterConfirm_dest;
    private Button btn_navigate, btn_arrived;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_after_confirm);
        initView();

        afterConfirm_dest.setText(DriverMatchInfoActivity.dest);

        btn_arrived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject passenger = new JSONObject();
                try{
                    passenger.put("passenger",DriverMatchSuccessActivity.passenger);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                DriverActivity.mSocket.emit("driver_arrive",passenger);
                Toast.makeText(DriverAfterConfirmActivity.this,"목적지에 도착하였습니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btn_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.kakao.kakaonavi.Location destination = Location.newBuilder(DriverMatchInfoActivity.dest,DriverMatchInfoActivity.d_lng,DriverMatchInfoActivity.d_lat).build();
                NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84).setVehicleType(VehicleType.TWO_WHEEL).setRpOption(RpOption.SHORTEST).build();
                KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);

                KakaoNaviParams params = builder.build();

                KakaoNaviService.getInstance().navigate(DriverAfterConfirmActivity.this,params);
            }
        });


    }

    @Override
    public void onBackPressed(){

    }

    private void initView(){
        afterConfirm_dest = findViewById(R.id.afterConfirm_dest);
        btn_navigate = findViewById(R.id.btn_afterConfirm_navigate);
        btn_arrived = findViewById(R.id.btn_afterConfirm_arrived);
    }
}
