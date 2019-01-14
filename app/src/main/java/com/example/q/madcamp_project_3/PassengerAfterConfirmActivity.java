package com.example.q.madcamp_project_3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.socket.emitter.Emitter;

public class PassengerAfterConfirmActivity extends AppCompatActivity {

    private ImageView afterConfirm_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_after_confirm);
        afterConfirm_iv = findViewById(R.id.afterConfrim_psg_iv);
        Glide.with(this).load(R.drawable.scoot_icon).into(afterConfirm_iv);


        MainActivity.mSocket.on("passenger_finish",onFinish);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        MainActivity.mSocket.off("passenger_finish");
    }


    private Emitter.Listener onFinish = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            finish();
        }
    };

}
