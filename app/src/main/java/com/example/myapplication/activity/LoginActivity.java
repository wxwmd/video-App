package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.R;
import com.example.myapplication.activity.MainActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login = findViewById(R.id.login);
        login.setOnClickListener(new Listener());
    }

    class Listener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    EditText Eid = findViewById(R.id.stuId);
                    EditText Epwd = findViewById(R.id.pwd);
                    String name = Eid.getText().toString();
                    String pwd = Epwd.getText().toString();
                    try {
                        String urlStr = "http://192.168.2.102:8080/user/login?name=" +  name + "&pwd=" + pwd;
                        System.out.println(urlStr);
                        //1.找水源---创建URL（统一资源定位器）
                        URL url=new URL(urlStr);
                        //2.开水闸---openCOnnection
                        HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
                        //3.建管道---InputStream
                        InputStream inputStream=httpURLConnection.getInputStream();
                        //4.建蓄水池---InputStreamReader
                        InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"UTF-8");
                        //5.水桶盛水——BufferedReader
                        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

                        StringBuffer stringBuffer=new StringBuffer();
                        String temp=null;
                        //循环做盛水工作---while循环
                        while ((temp=bufferedReader.readLine())!=null){
                            stringBuffer.append(temp);
                        }
                        //关闭水池入口，从管道到水桶
                        bufferedReader.close();
                        inputStreamReader.close();
                        inputStream.close();
                        //打印日志
                        String result = stringBuffer.toString();
                        if (result != null){
                            JSONObject user = JSON.parseObject(result);
                        /*
                        User user1 = new User();
                        user1.setId(user.getInteger("id"));
                        user1.setName(user.getString("name"));
                        user1.setAvatarPath(user.getString("avatarPath"));
                        user1.setDescription(user.getString("description"));
                        user1.setPwd(pwd);*/
                            SharedPreferences userInfo = getSharedPreferences("userInfo",MODE_PRIVATE);
                            SharedPreferences.Editor editor = userInfo.edit();
                            editor.putInt("uid",user.getIntValue("id"));
                            editor.putString("uname",name);
                            editor.putString("pwd",pwd);
                            editor.putString("description",user.getString("description"));
                            editor.apply();
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Looper.prepare();
                            Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            t1.start();
        }
    }
}