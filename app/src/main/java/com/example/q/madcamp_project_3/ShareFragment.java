package com.example.q.madcamp_project_3;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.socket.emitter.Emitter;

public class ShareFragment extends Fragment implements ClusterManager.OnClusterClickListener, ClusterManager.OnClusterItemClickListener, OnMapReadyCallback {

    private GoogleMap map;
    private MapView mapView;

    private Handler handler;

    private Location last_location = null;

    private ImageButton btn_current_location_sharing,btn_search;
    private Button btn_match;
    private ClusterManager<MyItem> mClusterManager;

    private FusedLocationProviderClient mFusedLocationClient;
    Geocoder coder;
    double latitude, longitude;
    private static final int REQUEST_CODE_PERMISSIONS = 1000;

    private JSONArray dataSet;
    public static double[] latitudeArrary, longitudeArrary;
    public static List<MyItem> MyItemList;

    FloatingActionButton fab;
    int SHARING_REQUEST = 3;
    int day,day1,month,month1,year,tomorrow,tomonth,tomonth1,toyear;

    TextView startit, endit, nettime;
    Calendar minDate, minDate1, minDate11;

    DatePickerDialog datePickerDialog1, datePickerDialog2;
    long diff, diffHours;




    public ShareFragment() {

    }

    private Emitter.Listener onShare = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            dataSet = (JSONArray) args[0];
            System.out.println("dataSet from server : "+dataSet);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    startDemo();
                }
            });
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("onCreateView!");
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        handler = new Handler();

        //init view
        startit = view.findViewById(R.id.startit);
        endit = view.findViewById(R.id.endit);
        nettime = view.findViewById(R.id.nettime);

        btn_search = view.findViewById(R.id.btn_search);
        btn_search.setImageResource(R.drawable.loupe);

        Date currentDate = new Date();

        minDate = Calendar.getInstance();
        minDate1 = Calendar.getInstance();
        minDate11 = Calendar.getInstance();
        minDate.setTime(currentDate);
        minDate1.setTime(currentDate);
        minDate11.setTime(currentDate);

        day = minDate.get(java.util.Calendar.DAY_OF_MONTH);
        month = minDate.get(java.util.Calendar.MONTH);
        month1 = month + 1;
        year = minDate.get(java.util.Calendar.YEAR);
        startit.setText(year  + " / " + month1  + " / " + day);

        minDate1.add(Calendar.DAY_OF_MONTH, 1);

        tomorrow = minDate1.get(java.util.Calendar.DAY_OF_MONTH);
        tomonth = minDate1.get(java.util.Calendar.MONTH);
        tomonth1 = month + 1;
        toyear = minDate1.get(java.util.Calendar.YEAR);
        endit.setText(toyear  + " / " + tomonth1  + " / " + tomorrow);
        nettime.setText("총 24시간");

        startit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                datePickerDialog1 = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
                        day = Day;
                        day1 = day+1;
                        month = Month;
                        year = Year;
                        month1 = month + 1;

                        startit.setText(year + " / " + month1 + " / " + day);
                        minDate.set(year, month, day);
                        minDate11.set(year,month,day1);
                        BottomSheetDialog.start[0] = year + " / " + month1 + " / " + day;
                    }
                }, year, month, day);

                datePickerDialog1.getDatePicker().setMinDate(minDate.getTime().getTime());

                datePickerDialog1.show();


            }
        });



        endit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(datePickerDialog1 == null) Toast.makeText(getActivity(), "시작 일자를 먼저 선택하십시오", Toast.LENGTH_SHORT).show();
                else {
                    datePickerDialog2 = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
                            tomorrow = Day;
                            tomonth = Month;
                            toyear = Year;
                            tomonth1 = tomonth + 1;
                            endit.setText(toyear + " / " + tomonth1 + " / " + tomorrow);
                            minDate1.set(toyear, tomonth, tomorrow);

                            diff = minDate1.getTime().getTime() - minDate.getTime().getTime();
                            diffHours = diff / 60 / 60 / 1000;
                            nettime.setText("총 " + diffHours + "시간");
                            BottomSheetDialog.end[0] = toyear + " / " + tomonth1 + " / " + tomorrow;
                        }
                    }, toyear, tomonth, tomorrow);

                    datePickerDialog2.getDatePicker().setMinDate(minDate11.getTime().getTime());

                    datePickerDialog2.show();

                }

            }
        });


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Data format : yyyy/mm/dd

                // Data type : JSONArray {available: [{date: Data format}, ...]}

                JSONArray available = new JSONArray();

                for(int i = day; i<=tomorrow; i++){
                    JSONObject temp = new JSONObject();
                    try{
                        temp.put("date",year+"/"+month1+"/"+i);
                        available.put(temp);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                BottomSheetDialog.data[0] = available;
                MainActivity.mSocket.emit("on_share",available);

            }
        });



        btn_current_location_sharing = view.findViewById(R.id.btn_current_location_sharing);
        btn_match = view.findViewById(R.id.btn_match);
        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //서버 reject => 자차가 없으시면 등록할 수 없습니다! dialog
                Intent share_intent = new Intent(getActivity(), Share.class);
                startActivity(share_intent);
//                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
//                try{
//                    Intent intent = intentBuilder.build(getActivity());
//                    startActivityForResult(intent,SHARING_REQUEST);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }

            }
        });
        //map initialize
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);


        MainActivity.mSocket.on("data",onShare);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                }
            }
        });

        btn_current_location_sharing.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
                }
                mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
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
        System.out.println("onMapReady!");
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

    protected void startDemo() {
        System.out.println("dataSet from server in startDemo : "+dataSet);

        map.clear();

        MyItemList = new ArrayList<>();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.374153, 127.365647), 12));

        mClusterManager = new ClusterManager<>(getActivity(), map);

        map.setOnCameraIdleListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);

            for (int i = 0; i < dataSet.length(); i++) {
                try {
                    JSONObject jsonObject = dataSet.getJSONObject(i);
                    String carkind = jsonObject.getString("carkind");
                    String carnum = jsonObject.getString("carnum");
                    double lat = Double.parseDouble(jsonObject.getString("lat"));
                    double lng = Double.parseDouble(jsonObject.getString("lng"));
                    Integer price = Integer.parseInt(jsonObject.getString("price"));
                    String place = jsonObject.getString("place");
                    JSONArray available = jsonObject.getJSONArray("available");
                    MyItem myItem = new MyItem(lat,lng,carkind,price+"원/일",carkind,carnum,price,available,place);
                    mClusterManager.addItem(myItem);
                    MyItemList.add(myItem);
                    System.out.println("After addItem");

                }catch (JSONException e){
                    System.out.println("JSONException occurs");
                    e.printStackTrace();
                }

        }

    }


    public boolean onClusterClick(Cluster cluster){

        Log.d("msg","In onClusterClick!!!!!");

        Collection<MyItem> userCollection = cluster.getItems();
        Log.d("msg","userCollection : " + userCollection);

        ArrayList<MyItem> markerList = new ArrayList<MyItem>(userCollection);
        Log.d("msg", "markerlistsize : " + markerList.size());
        Log.d("msg","markerlist : " + markerList);

        latitudeArrary = new double[markerList.size()];
        longitudeArrary = new double[markerList.size()];
        for(int i=0;i<markerList.size();i++) {
            latitudeArrary[i] = markerList.get(i).getPosition().latitude;
            longitudeArrary[i] = markerList.get(i).getPosition().longitude;
            Log.d("list", "좌표 : " + markerList.get(i).getPosition().latitude + " , " + markerList.get(i).getPosition().longitude);
        }
        Log.d("msg","latitude Arrary : " + latitudeArrary[0]);
        Log.d("msg","longitude Arrary : " + longitudeArrary[0]);
        BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDialog();
        bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());



        return false;
    }

    public boolean onClusterItemClick(ClusterItem item) {
        Log.d("msg","In onClusterItemClick!!!!!");
        latitudeArrary = new double[1];
        longitudeArrary = new double[1];
        latitudeArrary[0] = item.getPosition().latitude;
        longitudeArrary[0] = item.getPosition().longitude;
        BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDialog();
        bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        Log.d("msg","Item 좌표 : " + item.getPosition().latitude + " , " +item.getPosition().longitude);

        return false;
    }

}
