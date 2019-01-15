package com.example.q.madcamp_project_3;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Obaro on 01/08/2016.
 */
public class BottomSheetDialog extends BottomSheetDialogFragment {


    //int[] IMAGES = {R.drawable.bmw, R.drawable.yamaha, R.drawable.daelim, R.drawable.pcx};
    int[] IMAGES;
    String[] NAMES;
    String[] CARNUM;


    public static String[] start = new String[1];
    public static String[] end = new String[1];
    public static JSONArray[] data = new JSONArray[1];
    public static List<MyItem> Revised_MyItemList;

    private BottomSheetBehavior.BottomSheetCallback
            mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {


        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        Log.d("msg", "In setupDialog!!!!!!!");
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.listitem, null);
        dialog.setContentView(contentView);

        Revised_MyItemList = new ArrayList<>();

        IMAGES = new int[ShareFragment.longitudeArrary.length];
        NAMES = new String[ShareFragment.longitudeArrary.length];
        CARNUM = new String[ShareFragment.longitudeArrary.length];

        ListView listView = (ListView)contentView.findViewById(R.id.listview);

        CustomAdapter customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(),"hello~", Toast.LENGTH_SHORT).show();

                //면허가 없으시면 예약이 불가능 합니다!

                Intent intent = new Intent(getActivity(), Reservation.class);

                intent.putExtra("scooter_number",Revised_MyItemList.get(position).getCarnum());
                intent.putExtra("start_time", start[0]);
                intent.putExtra("end_time", end[0]);
                intent.putExtra("scooter_type", Revised_MyItemList.get(position).getCarkind());
                intent.putExtra("scooter_location",Revised_MyItemList.get(position).getPlace());
                intent.putExtra("price",Revised_MyItemList.get(position).getPrice());
                startActivity(intent);
                dismiss();
            }
        });
        Log.d("msg","longtitudeArrary length : " + ShareFragment.longitudeArrary.length);
        for(int i=0;i<ShareFragment.longitudeArrary.length;i++) {
            Log.d("msg","MyItemList : " + ShareFragment.MyItemList);
            for (MyItem myItem : ShareFragment.MyItemList) {
                Log.d("msg", "i : " + i);
                if (myItem.getPosition().latitude == ShareFragment.latitudeArrary[i] && myItem.getPosition().longitude == ShareFragment.longitudeArrary[i]){
                    if(myItem.getCarkind().equals("bmw")) IMAGES[i] = (R.drawable.bmw);
                    else if(myItem.getCarkind().equals("biggle")) IMAGES[i] =(R.drawable.biggle);
                    else if(myItem.getCarkind().equals("yamaha")) IMAGES[i] =(R.drawable.yamaha);
                    else if(myItem.getCarkind().equals("daelim")) IMAGES[i] =(R.drawable.daelim);
                    else if(myItem.getCarkind().equals("pcx")) IMAGES[i] =(R.drawable.pcx);
                    else if(myItem.getCarkind().equals("vespa")) IMAGES[i] =(R.drawable.vespa);
                    else IMAGES[i] =(R.drawable.scoot_icon);

                    NAMES[i] = myItem.getCarkind();
                    CARNUM[i] = myItem.getCarnum();
                    Revised_MyItemList.add(myItem);
                    break;
                }
            }
        }
        Log.d("msg", "End of setupDialog!!!!!!!");

    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() { return IMAGES.length;  }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Log.d("msg", "I'm in getView!");
            view = getLayoutInflater().inflate(R.layout.customlayout,null);

            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            TextView textView_name = (TextView)view.findViewById(R.id.textView_name);
            TextView textView_description = (TextView)view.findViewById(R.id.textView_description);
            Log.d("msg","Before for clause!");


            imageView.setImageResource(IMAGES[position]);
            textView_name.setText(NAMES[position].toUpperCase());
            textView_description.setText(CARNUM[position]);
            return view;
        }
    }

}
