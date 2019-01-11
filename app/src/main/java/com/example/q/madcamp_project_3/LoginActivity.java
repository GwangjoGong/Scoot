package com.example.q.madcamp_project_3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ImageView title_iv;
    private EditText login_id, login_pw;
    private ImageButton imageButton_login,imageButton_signup;

    private SessionCallback callback;

    private static final String URL_LOGIN = "http://143.248.140.106:1580/api/login";
    private static final String URL_SIGNUP = "http://143.248.140.106:1580/api/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_login);
        initView();

        //Callback Setting
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        imageButton_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = login_id.getText().toString();
                String pw = login_pw.getText().toString();

                Map<String,Object> data = new LinkedHashMap<>();
                data.put("id",id);
                data.put("password",pw);

                byte[] postDatabytes = parseParameter(data);

                String result = sendPost(postDatabytes,URL_LOGIN);
                System.out.println("RESULT FROM LOGIN : "+result);
            }
        });

        imageButton_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(Session.getCurrentSession().handleActivityResult(requestCode,resultCode,data)){
            return;
        }

        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private void initView(){
        title_iv = findViewById(R.id.imageView_title);
        title_iv.setImageResource(R.drawable.scoot_icon);
        login_id = findViewById(R.id.login_id);
        login_pw = findViewById(R.id.login_pw);
        imageButton_login = findViewById(R.id.imageButton_login);
        imageButton_login.setImageResource(R.drawable.login);
        imageButton_signup = findViewById(R.id.imageButton_signup);
        imageButton_signup.setImageResource(R.drawable.signup);
    }
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }
    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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

}
