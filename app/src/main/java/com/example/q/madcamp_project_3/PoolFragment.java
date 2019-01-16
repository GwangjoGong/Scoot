package com.example.q.madcamp_project_3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;



public class PoolFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private MapView mapView;


    private TextView start_tv,dest_tv;
    private ImageButton btn_current_location;
    private Button btn_match;

    private static final int START_PICKER_REQUEST =1;
    private static final int DEST_PICKER_REQUEST =2;


    private FusedLocationProviderClient mFusedLocationClient;;
    private static final int REQUEST_CODE_PERMISSIONS = 1000;

    public static LatLng start_latlng, dest_latlng;

    public static LatLng curr_latlng;

    public static String start_name, dest_name = "undefined";

    public PoolFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pool, container, false);
        init(view,savedInstanceState);


        //configure match button
        btn_match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start_name.equals("undefined") || dest_name.equals("undefined")){
                    Toast.makeText(getActivity(),"출발/목적지를 설정해 주세요.",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getActivity(), PassengerMatchActivity.class);
                    startActivity(intent);
                    map.clear();
                }
            }
        });

        //Configure current location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }else{
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        curr_latlng = myLocation;
                        map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                        map.animateCamera(CameraUpdateFactory.zoomTo(17));
                    }
                }
            });
        }

        btn_current_location.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            curr_latlng = myLocation;
                            map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                            map.animateCamera(CameraUpdateFactory.zoomTo(17));
                        }
                    }
                });
            }
        });

        //Configure location search
        start_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try{
                    Intent intent = intentBuilder.build(getActivity());
                    startActivityForResult(intent,START_PICKER_REQUEST);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        dest_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try{
                    Intent intent = intentBuilder.build(getActivity());
                    startActivityForResult(intent,DEST_PICKER_REQUEST);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == Activity.RESULT_OK){
            final Place place = PlacePicker.getPlace(getActivity(),data);
            final CharSequence name = place.getName();
            final LatLng latLng = place.getLatLng();
            switch (requestCode){
                case START_PICKER_REQUEST:
                    start_tv.setText(name);
                    start_latlng = latLng;
                    start_name = name.toString();
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    map.animateCamera(CameraUpdateFactory.zoomTo(17));
                    map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("출발지"));
                    break;
                case DEST_PICKER_REQUEST:
                    dest_tv.setText(name);
                    dest_latlng = latLng;
                    dest_name = name.toString();
                    map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("도착지"));
                    LatLng center = new LatLng((start_latlng.latitude+dest_latlng.latitude)/2,(start_latlng.longitude+dest_latlng.longitude)/2);
                    map.moveCamera(CameraUpdateFactory.newLatLng(center));
                    map.animateCamera(CameraUpdateFactory.zoomTo(15));
                    break;
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void init(View view, Bundle savedInstanceState){
        //init view
        start_tv = view.findViewById(R.id.start_tv);
        dest_tv = view.findViewById(R.id.dest_tv);
        btn_current_location = view.findViewById(R.id.btn_current_location);
        btn_match = view.findViewById(R.id.btn_match);

        //map initialize
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap gmap) {
        map = gmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_PERMISSIONS :
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(),"권한 체크 거부 됨", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
