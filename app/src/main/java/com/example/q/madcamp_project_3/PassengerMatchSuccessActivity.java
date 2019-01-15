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

import io.socket.emitter.Emitter;

public class PassengerMatchSuccessActivity extends AppCompatActivity {

    private TextView onMatch_driver_name, onMatch_driver_carkind, onMatch_driver_carnum;
    private Button btn_passenger_call;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_match_success);
        initView();

        MainActivity.mSocket.on("passenger_on_car",onCar);

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
    protected void onDestroy(){
        super.onDestroy();
        MainActivity.mSocket.off("passenger_on_car");
    }

    private Emitter.Listener onCar = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Intent intent = new Intent(PassengerMatchSuccessActivity.this,PassengerAfterConfirmActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private void initView(){
        onMatch_driver_name = findViewById(R.id.onMatch_driver_name_tv);
        onMatch_driver_carkind = findViewById(R.id.onMatch_driver_carkind_tv);
        onMatch_driver_carnum = findViewById(R.id.onMatch_driver_carnum_tv);
        btn_passenger_call = findViewById(R.id.btn_passenger_call);
    }
}
