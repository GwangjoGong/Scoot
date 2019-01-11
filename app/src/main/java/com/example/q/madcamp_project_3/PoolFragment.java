package com.example.q.madcamp_project_3;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;


public class PoolFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private MapView mapView;

    private Location last_location = null;

    private TextView start_tv,dest_tv;
    private ImageButton btn_current_location;
    private Button btn_match;

    public PoolFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pool, container, false);

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


        return view;
    }


    @Override
    public void onMapReady(GoogleMap gmap) {
        map = gmap;
    }
}
