package com.example.myapplication.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.myapplication.R;

public class PersonalActivity extends AppCompatActivity {

    private ImageView avatar;
    private TextView name;
    private TextView description;
    private ListView myposts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myvideo);
    }

    void init(){
        avatar = findViewById(R.id.avatar1);
        name = findViewById(R.id.uname);
        description = findViewById(R.id.description);
        myposts = findViewById(R.id.myposts);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        name.setText(sharedPreferences.getString("uname","wxw"));
        description.setText(sharedPreferences.getString("description","这个人很懒，还没有介绍"));
    }

}
