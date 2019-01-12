package com.example.q.madcamp_project_3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.Serializable;
import java.util.Date;

public class Alarm_Receiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Date date = new Date();
        Log.d("MyApp", "리시버 도착 시간 " + date.toString());

        String state = intent.getExtras().getString("state");
        String state_snow = intent.getExtras().getString("state_snow");
        Log.d("msg","state : " + state);
        Log.d("msg","state_snow : " + state_snow);


        // RingtonePlayingService 서비스 intent 생성
        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        if(state_snow != null){
            String description = AlarmFragment.find_weather();

            if(  (description.indexOf("snow") == -1) && (description.indexOf("rain") == -1) ) {
                AlarmFragment.alarm_manager.cancel(AlarmFragment.pendingIntent_snow);
                Log.d("msg", "There is no snow or rain so alarm_snow is canceled!");
                return;
            }
            else {
                Log.d("msg", "There is snow or rain so keep going");
                service_intent.putExtra("state", state_snow);
            }
        }

        else if(state != null){
            service_intent.putExtra("state", state);
        }

        else{}

        this.context = context;

        // RingtonePlayinService로 extra string값 보내기


        // start the ringtone service

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            this.context.startForegroundService(service_intent);
        }else{
            this.context.startService(service_intent);
        }
    }
}

