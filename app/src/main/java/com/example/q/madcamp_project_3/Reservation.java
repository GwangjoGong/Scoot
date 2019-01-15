package com.example.q.madcamp_project_3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlacePicker;

public class Reservation extends AppCompatActivity {

    ImageView scooter_imageView;
    TextView scooter_number;
    TextView scooter_type;
    TextView start_time;
    TextView end_time;
    TextView scooter_location;
    TextView scooter_location2;
    TextView price;
    Button reservation_button;

    String scooter_number_str;
    String scooter_type_str;
    String start_time_str;
    String end_time_str;
    String scooter_location_str;
    String price_str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        scooter_imageView = (ImageView)findViewById(R.id.scooter_imageView);
        scooter_number = (TextView)findViewById(R.id.scooter_number);
        scooter_type = (TextView)findViewById(R.id.scooter_type);
        start_time = (TextView)findViewById(R.id.start_time);
        end_time = (TextView)findViewById(R.id.end_time);
        scooter_location = (TextView)findViewById(R.id.scooter_location);
        scooter_location2 = (TextView)findViewById(R.id.scooter_location2);
        price = (TextView)findViewById(R.id.price);
        reservation_button = (Button)findViewById(R.id.reservation_button);


        scooter_number_str = getIntent().getExtras().getString("scooter_number");
        scooter_type_str = getIntent().getExtras().getString("scooter_type");
        start_time_str = getIntent().getExtras().getString("start_time");
        end_time_str = getIntent().getExtras().getString("end_time");
        scooter_location_str = getIntent().getExtras().getString("scooter_location");
        price_str = getIntent().getExtras().get("price").toString();

        if(scooter_type_str.equals("bmw")) scooter_imageView.setImageResource(R.drawable.bmw);
        else if(scooter_type_str.equals("biggle")) scooter_imageView.setImageResource(R.drawable.biggle);
        else if(scooter_type_str.equals("yamaha")) scooter_imageView.setImageResource(R.drawable.yamaha);
        else if(scooter_type_str.equals("daelim")) scooter_imageView.setImageResource(R.drawable.daelim);
        else if(scooter_type_str.equals("pcx")) scooter_imageView.setImageResource(R.drawable.pcx);
        else if(scooter_type_str.equals("vespa")) scooter_imageView.setImageResource(R.drawable.vespa);
        else scooter_imageView.setImageResource(R.drawable.scoot_icon);

        scooter_number.setText(scooter_number_str);
        scooter_type.setText(scooter_type_str);
        start_time.setText(start_time_str);
        end_time.setText(end_time_str);
        scooter_location.setText(scooter_location_str);
        scooter_location2.setText(scooter_location_str);
        price.setText(price_str + "원/일");

        reservation_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Reservation.this,"예약이 완료되었습니다",Toast.LENGTH_SHORT).show();
                finish();

            }
        });


    }
}
