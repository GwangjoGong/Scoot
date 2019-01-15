package com.example.q.madcamp_project_3;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class Share extends AppCompatActivity {

    Button btn_location, btn_start_date, btn_end_date, btn_share_confirm;
    EditText share_scooter_type, share_scooter_num, share_price;
    TextView start_date, end_date,share_scooter_location;
    DatePickerDialog start_date_Picker, end_date_Picker;
    int start_day, start_month, start_year, end_day, end_month, end_year, start_month1, end_month1;
    int count;
    Calendar start_cal, end_cal, minDate;

    public static LatLng share_latlng;
    public static String share_name;


    private static final int SHARE_PICKER_REQUEST = 3;


    private static final String URL_CAR_POST = "http://143.248.140.106:1580/api/car";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        btn_location = (Button)findViewById(R.id.btn_location);
        btn_start_date = (Button)findViewById(R.id.btn_start_date);
        btn_end_date = (Button)findViewById(R.id.btn_end_date);
        btn_share_confirm = (Button)findViewById(R.id.btn_share_confirm);

        share_scooter_type = findViewById(R.id.t2_1);
        share_scooter_num = findViewById(R.id.t3_1);
        share_price = findViewById(R.id.t4_1);
        start_date = findViewById(R.id.text_start_date);
        end_date = findViewById(R.id.text_end_date);
        share_scooter_location = (TextView)findViewById(R.id.t5_1);

        count = 0;

        if(count == 0) Log.d("msg","count is zero");


        minDate = Calendar.getInstance();

        start_cal = Calendar.getInstance();
        start_day = start_cal.get(Calendar.DAY_OF_MONTH);
        start_month = start_cal.get(Calendar.MONTH);
        start_month1 = start_month + 1;
        start_year = start_cal.get(Calendar.YEAR);
        start_date.setText(start_year  + " / " + start_month1  + " / " + start_day);

        end_cal = Calendar.getInstance();
        end_day = end_cal.get(Calendar.DAY_OF_MONTH);
        end_month = end_cal.get(Calendar.MONTH);
        end_month1 = end_month+1;
        end_year = end_cal.get(Calendar.YEAR);
        end_date.setText(end_year  + " / " + end_month1  + " / " + end_day);

        btn_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_date_Picker = new DatePickerDialog(Share.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
                        start_day = Day;
                        start_month = Month;
                        start_year = Year;
                        start_month1 = start_month+1;
                        start_date.setText(start_year  + " / " + start_month1  + " / " + start_day);

                    }
                }, start_year,start_month,start_day);

                start_date_Picker.getDatePicker().setMinDate(minDate.getTime().getTime());

                start_date_Picker.show();
            }
        });

        btn_end_date.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if(start_cal == null) {
                    Toast.makeText(Share.this,"시작일을 먼저 선택해 주십시오",Toast.LENGTH_SHORT).show();
                    return;
                }
                end_date_Picker = new DatePickerDialog(Share.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
                        end_day = Day;
                        end_month = Month;
                        end_year = Year;
                        end_month1 = end_month+1;
                        end_date.setText(end_year  + " / " + end_month1  + " / " + end_day);

                    }
                }, end_year,end_month,end_day);

                end_date_Picker.getDatePicker().setMinDate(start_cal.getTime().getTime());

                end_date_Picker.show();
            }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try{
                    Intent intent = intentBuilder.build(Share.this);
                    startActivityForResult(intent,SHARE_PICKER_REQUEST);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btn_share_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0) {
                    Log.d("msg","count is zero2");
                    Toast.makeText(Share.this,"스쿠터 위치를 먼저 선택해 주세요",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Share.this,"쉐어링 스쿠터의 정보가\n성공적으로 저장되었습니다",Toast.LENGTH_SHORT).show();

                    //build available
                    JSONArray available = new JSONArray();

                    for(int i = start_day; i<=end_day; i++){
                        JSONObject temp = new JSONObject();
                        try{
                            temp.put("date",start_year+"/"+start_month1+"/"+i);
                            System.out.println("DATA : "+start_year+"/"+start_month1+"/"+i);
                            available.put(temp);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }


                    Map<String,Object> data = new LinkedHashMap<>();

                    try{
                        data.put("carkind",share_scooter_type.getText().toString().trim());
                        data.put("carnum",share_scooter_num.getText().toString().trim());
                        data.put("place",share_name);
                        data.put("lat",String.valueOf(share_latlng.latitude));
                        data.put("lng",String.valueOf(share_latlng.longitude));
                        data.put("owner",MainActivity.name);
                        data.put("price",share_price.getText().toString().trim());
                        data.put("available",available);

                        byte[] carDataBytes = LoginActivity.parseParameter(data);

                        String result = LoginActivity.sendPost(carDataBytes,URL_CAR_POST);

                        System.out.println("RESULT FROM CAR POST : "+result);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    finish();
                }

            }
        });




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(share_scooter_location == null) Log.d("msg","It's null2");
        if(resultCode == Activity.RESULT_OK){
            final Place place = PlacePicker.getPlace(Share.this,data);
            final CharSequence name = place.getName();
            final LatLng latLng = place.getLatLng();
            switch (requestCode){
                case SHARE_PICKER_REQUEST:
                    share_scooter_location.setText(name);
                    if(share_scooter_location == null) Log.d("msg","It's null3");
                    share_latlng = latLng;
                    share_name = name.toString();
                    count++;
//                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                    map.animateCamera(CameraUpdateFactory.zoomTo(17));
//                    map.addMarker(new MarkerOptions()
//                            .position(latLng)
//                            .title("출발지"));
                    break;

            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }



}
