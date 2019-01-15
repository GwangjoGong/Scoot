package com.example.q.madcamp_project_3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class AlarmFragment extends Fragment {

    public static Context mContext;

    public static AlarmManager alarm_manager;
    public static PendingIntent pendingIntent;

    private TimePicker alarm_timepicker;
    private ImageButton btn_start,btn_cancel;
    private EditText alarm_delay_et;
    private TextView city_name;

    public static Handler handler;

    public static double latitude, longitude;
    private int delay;


    public AlarmFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alrm, container, false);
        initView(view);
        mContext = getActivity();

        handler = new Handler();

        find_name();


        latitude = PoolFragment.curr_latlng.latitude;
        longitude =PoolFragment.curr_latlng.longitude;


        // 알람매니저 설정
        alarm_manager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        // 알람리시버 intent 생성
        final Intent my_intent = new Intent(mContext, Alarm_Receiver.class);

        //Calendar 객체 생성
        final Calendar calendar = Calendar.getInstance();


        btn_start.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.d("msg","On alarm_on button clicked");

                String delayStr = alarm_delay_et.getText().toString();

                delay = Integer.parseInt(delayStr);
                System.out.println("DELAY : "+delay);


                String weather = find_weather();
                // 시간 가져옴
                int hour = alarm_timepicker.getHour();
                int minute = alarm_timepicker.getMinute();

                if(weather.equals("Rain")||weather.equals("Snow")) {
                    if ((minute - delay) < 0) {
                        hour = hour - 1;
                        minute = 60 + minute - delay;
                    } else {
                        minute = minute - delay;
                    }
                }


                // calendar에 시간 세팅
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE,minute);


                // receiver에 string 값 넘겨주기
                my_intent.putExtra("state","alarm on");
                my_intent.putExtra("delay",delay);
                my_intent.putExtra("lat",latitude);
                my_intent.putExtra("lng",longitude);


                pendingIntent = PendingIntent.getBroadcast(mContext, 0, my_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                // 알람셋팅
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pendingIntent);

                Log.d("msg","After Alarm setting");

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm_manager.cancel(pendingIntent);

                my_intent.putExtra("state","alarm off");

                mContext.sendBroadcast(my_intent);
            }
        });

        return view;
    }

    private void initView(View view){
        btn_start = view.findViewById(R.id.btn_start);
        btn_start.setImageResource(R.drawable.alarm_icon);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_cancel.setImageResource(R.drawable.cancel_icon);
        alarm_timepicker = view.findViewById(R.id.alarm_time_picker);
        alarm_delay_et = view.findViewById(R.id.alarm_delay_et);
        alarm_delay_et.setText("30");
        city_name = view.findViewById(R.id.city_name_tv);
    }

    private void find_name() {

        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=08601ff6119001b3fc5bf2502891ebb3";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    city_name.setText(response.getString("name"));

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        });
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(jor);


    }
    public static String find_weather() {
        final String[] weatherInfo = new String[1];
        String url = "http://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=08601ff6119001b3fc5bf2502891ebb3";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray list = response.getJSONArray("list");
                    JSONObject next_day = list.getJSONObject(0);
                    JSONArray weather = next_day.getJSONArray("weather");
                    JSONObject object = weather.getJSONObject(0);
                    weatherInfo[0] = object.getString("main");
                    Log.d("MyApp", "find_weather : " + weatherInfo[0]);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error : "+error);
            }
        });
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(jor);

        return weatherInfo[0];
    }



}
