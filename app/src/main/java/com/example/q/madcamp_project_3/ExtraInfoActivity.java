package com.example.q.madcamp_project_3;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;

import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;


public class ExtraInfoActivity extends AppCompatActivity {

    private EditText extra_license, extra_carkind, extra_carnum;
    private ImageButton btn_extra_confirm;

    private static final String TAG = ExtraInfoActivity.class.getSimpleName();
    private static final String API_KEY = "AIzaSyAWjKrFNUjPRufkV45CgKny6LnvH7CFK9s";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_info);
        initView();

        Intent data = getIntent();
        final String id = data.getStringExtra("id");
        final String pw = data.getStringExtra("pw");
        final String  name = data.getStringExtra("name");
        final String phone = data.getStringExtra("phone");
        final int userType = data.getIntExtra("userType",-1);

        if(userType==1){
            disableEditText(extra_carkind);
            disableEditText(extra_carnum);
        }


        btn_extra_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> data = new LinkedHashMap<>();
                data.put("id",id);
                data.put("password",pw);
                data.put("name",name);
                data.put("phone",phone);
                data.put("userType",userType);

                String license = extra_license.getText().toString().trim();
                data.put("license",license);
                if(userType==2){
                    String carkind = extra_carkind.getText().toString().trim();
                    String carnum = extra_carnum.getText().toString().trim();
                    data.put("carkind",carkind);
                    data.put("carnum",carnum);
                }

                byte[] postDataBytes = LoginActivity.parseParameter(data);
                String response = LoginActivity.sendPost(postDataBytes,LoginActivity.URL_SIGNUP);
                try {
                    JSONObject json = new JSONObject(response);
                    if(!json.getString("result").equals("0")){
                        Toast.makeText(ExtraInfoActivity.this,"SignUpFailed",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Map<String,Object> logInData = new LinkedHashMap<>();
                    logInData.put("id",id);
                    logInData.put("password",pw);

                    byte[] loginDataBytes = LoginActivity.parseParameter(logInData);
                    String loginResult = LoginActivity.sendPost(loginDataBytes,LoginActivity.URL_LOGIN);

                    JSONObject jsonObject = new JSONObject(loginResult);

                    String token = jsonObject.getString("token");
                    String name = jsonObject.getString("name");
                    int userType = jsonObject.getInt("userType");
                    String phone = jsonObject.getString("phone");
                    if(userType == 1) {
                        Intent intent = new Intent(ExtraInfoActivity.this, ExtraInfoActivity.class);
                        intent.putExtra("token", token);
                        intent.putExtra("name", name);
                        intent.putExtra("phone",phone);
                        startActivity(intent);
                    }else{
                        //userType == 2
                        final Intent mainIntent = new Intent(ExtraInfoActivity.this, ExtraInfoActivity.class);
                        final Intent driverIntent = new Intent(ExtraInfoActivity.this,DriverActivity.class);
                        mainIntent.putExtra("token",token);
                        mainIntent.putExtra("name",name);
                        mainIntent.putExtra("phone",phone);
                        driverIntent.putExtra("token",token);
                        driverIntent.putExtra("name",name);
                        driverIntent.putExtra("phone",phone);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ExtraInfoActivity.this);
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
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void initView(){
        extra_carkind = findViewById(R.id.et_pw);
        extra_carnum = findViewById(R.id.et_name);
        extra_license = findViewById(R.id.et_id);
        btn_extra_confirm = findViewById(R.id.btn_extra_confirm);
        btn_extra_confirm.setImageResource(R.drawable.confirm);
    }
    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setHint("");
    }
}
