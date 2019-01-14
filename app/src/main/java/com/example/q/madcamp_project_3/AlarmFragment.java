package com.example.q.madcamp_project_3;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
    //AlarmManager alarm_manager_snow;
    TimePicker alarm_timepicker;
    TimePicker alarm_timepicker_snow;
    Context context;
    public static PendingIntent pendingIntent, pendingIntent_snow;
    Calendar calendar, calendar_snow, minDate;
    DatePickerDialog datePickerDialog;
    DatePickerDialog datePickerDialog_snow;

    Button button_snow;
    Button button;

    ImageView ic_snow, ic_sun;
    int day,month,year,day_snow,month_snow,year_snow, month_snow1, month1;

    TextView text, text_snow;
    public static TextView your_city;

    String OPEN_WEATHER_MAP_API = "08601ff6119001b3fc5bf2502891ebb3";

    public static String city, description;
    public static double latitude, longitude;


    public AlarmFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alrm, container, false);

        mContext = getActivity();
        button_snow = (Button)view.findViewById(R.id.btn_date_snow);
        button = (Button)view.findViewById(R.id.btn_date);

        text_snow = (TextView)view.findViewById(R.id.text_snow);
        text = (TextView)view.findViewById(R.id.text);
        your_city = (TextView)view.findViewById(R.id.your_city);

        ic_snow = view.findViewById(R.id.ic_snow);
        ic_snow.setImageResource(R.drawable.ic_snow);
        ic_sun = view.findViewById(R.id.ic_sun);
        ic_sun.setImageResource(R.drawable.ic_sun);

        latitude = PoolFragment.curr_latlng.latitude;
        longitude =PoolFragment.curr_latlng.longitude;

        find_weather();
        minDate = Calendar.getInstance();

        calendar_snow = Calendar.getInstance();
        day_snow = calendar_snow.get(Calendar.DAY_OF_MONTH);
        month_snow = calendar_snow.get(Calendar.MONTH);
        month_snow1 = month_snow + 1;
        year_snow = calendar_snow.get(Calendar.YEAR);
        text_snow.setText(year_snow  + " / " + month_snow1  + " / " + day_snow);

        calendar = Calendar.getInstance();
        day = calendar_snow.get(Calendar.DAY_OF_MONTH);
        month = calendar_snow.get(Calendar.MONTH);
        month1 = month+1;
        year = calendar_snow.get(Calendar.YEAR);
        text.setText(year  + " / " + month1  + " / " + day);

        button_snow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog_snow = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
                        day_snow = Day;
                        month_snow = Month;
                        year_snow = Year;
                        month_snow1 = month_snow+1;
                        text_snow.setText(year_snow  + " / " + month_snow1  + " / " + day_snow);

                    }
                }, year_snow, month_snow, day_snow);

                datePickerDialog_snow.getDatePicker().setMinDate(minDate.getTime().getTime());

                datePickerDialog_snow.show();


            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
                        day = Day;
                        month = Month;
                        year = Year;
                        month1 = month+1;
                        text.setText(year  + " / " + month1  + " / " + day);
                    }
                }, year,month,day);
                datePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());

                datePickerDialog.show();


            }
        });


        //this.context = getActivity();

        // 알람매니저 설정
        alarm_manager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        //alarm_manager_snow = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        // 타임피커 설정
        alarm_timepicker = view.findViewById(R.id.time_picker);
        alarm_timepicker_snow = view.findViewById(R.id.time_picker_snow);

        // Calendar 객체 생성
//        calendar = Calendar.getInstance();
//        calendar_snow = Calendar.getInstance();

        // 알람리시버 intent 생성
        final Intent my_intent_snow = new Intent(getActivity(), Alarm_Receiver.class);
        final Intent my_intent = new Intent(getActivity(), Alarm_Receiver.class);

        // 알람 시작 버튼
        Button alarm_on = view.findViewById(R.id.btn_start);
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.d("msg","On alarm_on button clicked");

                // calendar에 시간 셋팅
                calendar.set(Calendar.YEAR, year);

                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                calendar.set(Calendar.MINUTE, alarm_timepicker.getMinute());

                calendar_snow.set(Calendar.YEAR, year_snow);
                calendar_snow.set(Calendar.MONTH, month_snow);
                calendar_snow.set(Calendar.DAY_OF_MONTH, day_snow);
                calendar_snow.set(Calendar.HOUR_OF_DAY, alarm_timepicker_snow.getHour());
                calendar_snow.set(Calendar.MINUTE, alarm_timepicker_snow.getMinute());

                // 시간 가져옴
                int hour = alarm_timepicker.getHour();
                int minute = alarm_timepicker.getMinute();

                int hour_snow = alarm_timepicker_snow.getHour();
                int minute_snow = alarm_timepicker_snow.getMinute();

                if(year_snow == 0) Toast.makeText(getActivity(),"눈/비 소식 시 Alarm 예정         : " +  hour_snow + "시 " + minute_snow + "분\n" + "눈/비 소식 없을 시 Alarm 예정 : " + hour + "시 " + minute + "분",Toast.LENGTH_SHORT).show();
                else Toast.makeText(getActivity(),"눈/비 소식 시 Alarm 예정 : \n" + year_snow + " / " + month_snow1 + " / " + day_snow + " " + hour_snow + "시 " + minute_snow + "분\n" + "눈/비 소식 없을 시 Alarm 예정 : \n" + year + " / " + month1 + " / " + day + " " + hour + "시 " + minute + "분",Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this,"Alarm 예정 " + hour + "시 " + minute + "분",Toast.LENGTH_SHORT).show();

                // reveiver에 string 값 넘겨주기
                my_intent_snow.putExtra("state_snow","alarm on");
                my_intent.putExtra("state","alarm on");


                pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, my_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

//
               pendingIntent_snow = PendingIntent.getBroadcast(getActivity(), 1, my_intent_snow,
                       PendingIntent.FLAG_UPDATE_CURRENT);

                // 알람셋팅
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pendingIntent);
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, calendar_snow.getTimeInMillis(),
                        pendingIntent_snow);





                Log.d("msg","After Alarm setting");

            }
        });

        // 알람 정지 버튼
        Button alarm_off = view.findViewById(R.id.btn_finish);
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pendingIntent == null) Toast.makeText(getActivity(),"예정된 알람이 없습니다",Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(getActivity(), "Alarm 종료", Toast.LENGTH_SHORT).show();
                    // 알람매니저 취소
                    //alarm_manager_snow.cancel(pendingIntent);
                    alarm_manager.cancel(pendingIntent);
                    alarm_manager.cancel(pendingIntent_snow);

                    my_intent.putExtra("state", "alarm off");
                    my_intent_snow.putExtra("state_snow","alarm off");


                    // 알람취소
                    getActivity().sendBroadcast(my_intent);
                    getActivity().sendBroadcast(my_intent_snow);

                }
            }
        });
        return view;
    }

    public static String find_weather() {

        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=08601ff6119001b3fc5bf2502891ebb3";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    description = object.getString("description");
                    Log.d("MyApp","description : "+ description);
                    city = response.getString("name");

                    your_city.setText(city);

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

        return description;

    }
}
