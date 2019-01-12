package com.example.q.madcamp_project_3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;

public class ShareFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private MapView mapView;

    private Location last_location = null;

    private TextView start_tv, dest_tv;
    private ImageButton btn_current_location_sharing;
    private Button btn_match;

    private FusedLocationProviderClient mFusedLocationClient;
    Geocoder coder;
    double latitude, longitude;
    private static final int REQUEST_CODE_PERMISSIONS = 1000;

    public ShareFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        //init view
        start_tv = view.findViewById(R.id.start_tv);
        dest_tv = view.findViewById(R.id.dest_tv);
        btn_current_location_sharing = view.findViewById(R.id.btn_current_location_sharing);
        btn_match = view.findViewById(R.id.btn_match);

        //map initialize
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        btn_current_location_sharing.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            map.addMarker(new MarkerOptions()
                                    .position(myLocation)
                                    .title("현재 위치"));
                            map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                            map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                        }
                    }
                });

            }
        });


        return view;
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
