package com.example.q.madcamp_project_3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText singup_id, signup_pw, signup_name;
    private ImageButton btn_confirm;
    private Button btn_check;
    private CheckBox cb_license, cb_hascar;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();

        Intent data = getIntent();
        if(data!=null){
            Long id = data.getLongExtra("id",-1);
            String name = data.getStringExtra("name");
            singup_id.setText(id.toString());
            singup_id.setEnabled(false);
            signup_name.setText(name);
            signup_name.setEnabled(false);
        }


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = singup_id.getText().toString().trim();
                String pw = signup_pw.getText().toString().trim();
                String name = signup_name.getText().toString().trim();
                if(!cb_license.isChecked()){
                    if(!cb_hascar.isChecked()){
                        //userType 0
                        Map<String,Object> data = new LinkedHashMap<>();
                        data.put("id",id);
                        data.put("password",pw);
                        data.put("name",name);
                        data.put("userType",0);

                        byte[] postDatabytes = LoginActivity.parseParameter(data);
                        String result = LoginActivity.sendPost(postDatabytes,LoginActivity.URL_SIGNUP);

                        final Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                        final Intent driverIntent = new Intent(SignUpActivity.this,DriverActivity.class);

                        try {
                            JSONObject json = new JSONObject(result);
                            if(json.getString("result").equals("0")){
                                Toast.makeText(SignUpActivity.this,"SignUpFailed",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Map<String,Object> loginData = new LinkedHashMap<>();
                            loginData.put("id",id);
                            loginData.put("password",pw);

                            byte[] loginDataBytes = LoginActivity.parseParameter(loginData);
                            String loginResponse = LoginActivity.sendPost(loginDataBytes,LoginActivity.URL_LOGIN);

                            JSONObject jsonObject = new JSONObject(loginResponse);
                            String token = jsonObject.getString("token");
                            String uName = jsonObject.getString("name");
                            int userType = jsonObject.getInt("userType");
                            mainIntent.putExtra("token",token);
                            mainIntent.putExtra("name",uName);
                            driverIntent.putExtra("token",token);
                            driverIntent.putExtra("name",uName);

                            if(userType==2){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);

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
                    else{
                        //no license driver
                        Toast.makeText(SignUpActivity.this, "무면허 운전은 범죄입니다.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Intent intent = new Intent(SignUpActivity.this,ExtraInfoActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("pw",pw);
                    intent.putExtra("name",name);
                    if(!cb_hascar.isChecked()){
                        //userType 1
                        intent.putExtra("userType",1);
                        startActivity(intent);
                        finish();
                    }else{
                        //userType 2
                        intent.putExtra("userType",2);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });


    }

    private void initView(){
        singup_id = findViewById(R.id.extra_license);
        signup_pw = findViewById(R.id.extra_carkind);
        signup_name = findViewById(R.id.extra_carnum);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setImageResource(R.drawable.confirm);
        btn_check = findViewById(R.id.btn_ocr);
        cb_license = findViewById(R.id.checkBox_license);
        cb_hascar = findViewById(R.id.checkBox_hascar);

    }
}
