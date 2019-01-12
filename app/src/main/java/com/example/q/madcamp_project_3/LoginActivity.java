package com.example.q.madcamp_project_3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ImageView title_iv;
    private EditText login_id, login_pw;
    private ImageButton imageButton_login,imageButton_signup;

    private SessionCallback callback;

    public static String URL_LOGIN = "http://143.248.140.106:1580/api/login";
    public static String URL_SIGNUP = "http://143.248.140.106:1580/api/signup";
    public static String URL_KAKAO = "http://143.248.140.106:1580/api/kakao";

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
                String id = login_id.getText().toString().trim();
                String pw = login_pw.getText().toString().trim();

                Map<String,Object> data = new LinkedHashMap<>();
                data.put("id",id);
                data.put("password",pw);

                byte[] postDatabytes = parseParameter(data);

                String result = sendPost(postDatabytes,URL_LOGIN);
                System.out.println("RESULT FROM LOGIN : "+result);
              
                final Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                final Intent driverIntent = new Intent(LoginActivity.this,DriverActivity.class);


                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String token = jsonObject.getString("token");
                    String name = jsonObject.getString("name");
                    int userType = jsonObject.getInt("userType");
                    mainIntent.putExtra("token",token);
                    mainIntent.putExtra("name",name);
                    driverIntent.putExtra("token",token);
                    driverIntent.putExtra("name",name);
                    System.out.println("userType : "+userType);

                    if(userType==2){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);

                        alertDialogBuilder.setTitle("로그인");
                       alertDialogBuilder
                                .setMessage("유저타입을 선택해주세요.")
                               .setCancelable(true)
                               .setPositiveButton("운전자",
                                       new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               startActivity(driverIntent);
                                           }
                                       })
                               .setNegativeButton("사용자",
                                       new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               startActivity(mainIntent);
                                           }
                                       }).show();
                    }else {
                        startActivity(mainIntent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
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
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("kakao_account.email");

        final String[] userName = new String[1];
        final long[] userId = new long[1];

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onSuccess(MeV2Response result) {
                System.out.println("id : "+result.getId());
                System.out.println("email: " + result.getKakaoAccount().getEmail());
                userId[0] = result.getId();
                userName[0] = result.getNickname();
                Map<String,Object> data = new LinkedHashMap<>();
                data.put("id",userId[0]);
                data.put("name",userName[0]);

                byte[] kakaoDataBytes = parseParameter(data);
                String resultKakao = sendPost(kakaoDataBytes,URL_KAKAO);
                System.out.println("RESPONSE FROM KAKAO : "+resultKakao);
                try {
                    JSONObject json = new JSONObject(resultKakao);
                    String success = json.getString("success");
                    if (success.equals("0")) {

                        String token = json.getString("token");
                        String name = json.getString("name");
                        int userType = json.getInt("userType");

                        final Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        final Intent driverIntent = new Intent(LoginActivity.this, DriverActivity.class);
                        mainIntent.putExtra("token", token);
                        mainIntent.putExtra("name", name);
                        driverIntent.putExtra("token", token);
                        driverIntent.putExtra("name", name);


                        if (userType == 2) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);

                            alertDialogBuilder.setTitle("로그인");
                            alertDialogBuilder
                                    .setMessage("유저타입을 선택해주세요.")
                                    .setCancelable(true)
                                    .setPositiveButton("운전자",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(driverIntent);
                                                }
                                            })
                                    .setNegativeButton("사용자",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(mainIntent);
                                                }
                                            }).show();
                        } else {
                            startActivity(mainIntent);
                        }

                    } else {
                        //Not found
                        final Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                        intent.putExtra("id", userId[0]);
                        intent.putExtra("name", userName[0]);
                        startActivity(intent);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static byte[] parseParameter(Map<String,Object> params){
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
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  postDataBytes;
    }

    public static String sendPost(final byte[] postDataBytes, final String key_url){
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
