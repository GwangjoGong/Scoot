package com.example.q.madcamp_project_3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import static com.example.q.madcamp_project_3.AlarmFragment.find_weather;
import static java.lang.Thread.sleep;

public class Alarm_Receiver extends BroadcastReceiver {

    Context context;

    final String[] weather = new String[1];

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        Date date = new Date();
        Log.d("MyApp", "리시버 도착 시간 " + date.toString());

        String state = intent.getExtras().getString("state");

        Log.d("msg","state : " + state);


        // RingtonePlayingService 서비스 intent 생성
        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        service_intent.putExtra("state",state);

        // start the ringtone service
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            this.context.startForegroundService(service_intent);
        }else{
            this.context.startService(service_intent);
        }
    }


}

