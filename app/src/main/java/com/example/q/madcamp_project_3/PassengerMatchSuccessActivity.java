package com.example.q.madcamp_project_3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class PassengerMatchSuccessActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView onMatch_driver_name, onMatch_driver_carkind, onMatch_driver_carnum;
    private Button btn_passenger_call;

    private MapView mapView;
    private GoogleMap map;

    private Handler handler;

    private double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_match_success);
        initView();
        handler = new Handler();
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        MainActivity.mSocket.on("passenger_on_car",onCar);

        MainActivity.mSocket.on("driver_location",onDriver);

        Intent data = getIntent();
        final String dPhone = data.getStringExtra("phone");

        onMatch_driver_name.setText(data.getStringExtra("name"));
        onMatch_driver_carkind.setText(data.getStringExtra("carkind"));
        onMatch_driver_carnum.setText(data.getStringExtra("carnum"));

        btn_passenger_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PassengerMatchSuccessActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PassengerMatchSuccessActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+dPhone));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap gmap) {
        map = gmap;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        MainActivity.mSocket.off("passenger_on_car");
        MainActivity.mSocket.off("driver_location");
    }

    private Emitter.Listener onCar = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Intent intent = new Intent(PassengerMatchSuccessActivity.this,PassengerAfterConfirmActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private Emitter.Listener onDriver = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data  = (JSONObject) args[0];
            try {
                double lat = data.getDouble("lat");
                double lng = data.getDouble("lng");
                final LatLng latLng = new LatLng(lat,lng);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        map.clear();
                        map.addMarker(new MarkerOptions().position(latLng).title("운전자 위치"));
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        map.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                });

            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    };

    private void initView(){
        onMatch_driver_name = findViewById(R.id.onMatch_driver_name_tv);
        onMatch_driver_carkind = findViewById(R.id.onMatch_driver_carkind_tv);
        onMatch_driver_carnum = findViewById(R.id.onMatch_driver_carnum_tv);
        btn_passenger_call = findViewById(R.id.btn_passenger_call);
        mapView = findViewById(R.id.mapView2);
    }

}
