package com.example.q.madcamp_project_3;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static Socket mSocket;

    private TextView tv_test;
    private EditText et_name,et_id,et_pw,et_ut;

    private Button btn_singup,btn_login;

    private Handler handler;

    private static final String URL_SERVER = "http://143.248.140.106:1580";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar Setting
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Tab Setting
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("스쿠터풀"));
        tabLayout.addTab(tabLayout.newTab().setText("스쿠터셰어링"));
        tabLayout.addTab(tabLayout.newTab().setText("알람"));

        viewPager = (ViewPager) findViewById(R.id.pager);

        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        try {
            mSocket = IO.socket(URL_SERVER);
            mSocket.connect();
        }catch (Exception e){
            e.printStackTrace();
        }



        /*
        handler = new Handler();

        //init views
        tv_test = findViewById(R.id.test_tv);
        et_name = findViewById(R.id.et_name);
        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        et_ut = findViewById(R.id.et_ut);
        btn_singup = findViewById(R.id.btn_signup);
        btn_login = findViewById(R.id.btn_login);

        //init socket


        mSocket.on("toclient",onConnect);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = et_id.getText().toString();
                String pw = et_id.getText().toString();

                Map<String,Object> data = new LinkedHashMap<>();
                data.put("id",id);
                data.put("password",pw);
                String userData = data.toString();

                byte[] postDatabytes = parseParameter(data);

                System.out.println("userData : "+userData);

                String result = sendPost(postDatabytes,URL_LOGIN);
                System.out.println("RESULT FROM LOGIN : "+result);

            }
        });

        btn_singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();
                String id = et_id.getText().toString();
                String pw = et_id.getText().toString();
                int userType = Integer.parseInt(et_ut.getText().toString());

                Map<String,Object> data = new LinkedHashMap<>();
                data.put("id",id);
                data.put("password",pw);
                data.put("name",name);
                data.put("userType",userType);
                String userData = data.toString();

                byte[] postDatabytes = parseParameter(data);

                System.out.println("userData : "+userData);

                String result = sendPost(postDatabytes,URL_SIGNUP);
                System.out.println("RESULT FROM SINGUP : "+result);

            }
        });
        */

    }

    @Override
    public void onBackPressed(){
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    /*
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mSocket.off("toclient");
        mSocket.close();
    }

    private String sendPost(final byte[] postDataBytes, final String key_url){
        final String[] response = new String[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL(key_url);
                    StringBuffer res = new StringBuffer();

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    //set parameters
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());

                    os.write(postDataBytes);

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());

                    int status = conn.getResponseCode();
                    if(status!=200){
                        throw new IOException("Post failed");
                    }else{
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        while ((inputLine = in.readLine())!=null){
                            res.append(inputLine);
                        }
                        in.close();
                    }

                    conn.disconnect();

                    response[0] = res.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        while(thread.isAlive()){}
        return response[0];
    }

    private byte[] parseParameter(Map<String,Object> params){
        StringBuilder postData = new StringBuilder();
        byte[] postDataBytes = null;
        try {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append("=");
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            postDataBytes = postData.toString().getBytes("UTF-8");
            Log.i("POSTDATA", Arrays.toString(postDataBytes));
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  postDataBytes;
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        JSONObject receivedData = (JSONObject) args[0];

                        String msg = receivedData.getString("msg");
                        tv_test.setText(msg);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    */
}
