package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.R;
import com.example.myapplication.bean.Video;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.activity.MainActivity.getBitmap;

public class PersonalActivity extends AppCompatActivity {

    private ImageView avatar;
    private List<Video> videos;
    private TextView name;
    private TextView description;
    private ListView myposts;
    private String avatarPath;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myvideo);
        init();
    }

    void init(){
        videos = new ArrayList<>();
        avatar = findViewById(R.id.avatar1);
        name = findViewById(R.id.uname);
        description = findViewById(R.id.description);
        myposts = findViewById(R.id.mypost);
        myposts.setAdapter(new VideoAdapter());
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        name.setText(sharedPreferences.getString("uname","wxw"));
        description.setText(sharedPreferences.getString("description","这个人很懒，还没有介绍"));
        initAvatar();
        initVideo();

        myposts.setAdapter(new VideoAdapter());
        myposts.setOnItemClickListener(new VideoListener());
    }


    //新开一个线程来显示头像
    void initAvatar(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                initAvatarPAth();
            }
        });
        t.start();
    }

    //初始化头像
    public void initAvatarPAth(){
        //找头像路径
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
                    uid = sharedPreferences.getInt("uid",0);
                    String urlStr = "http://192.168.2.102:8080/avatar?uid="+uid;
                    System.out.println(urlStr);
                    //1.找水源---创建URL（统一资源定位器）
                    URL url=new URL(urlStr);
                    //2.开水闸---openCOnnection
                    HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
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
                    avatarPath = stringBuffer.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(avatarPath);

        //初始化头像
        Thread t2  = new Thread(new Runnable() {
            @Override
            public void run() {
                PersonalActivity.this.runOnUiThread(new Runnable() {
                    String photoPath = "http://39.107.124.222:8080/avatar/" + avatarPath;
                    Bitmap photo1 = getBitmap(photoPath);
                    @Override
                    public void run() {
                        avatar.setImageBitmap(photo1);
                    }
                });
            }
        });
        t2.start();
    }

    //初始化视频
    public void initVideo(){
        System.out.println("init");

        videos = new ArrayList<>();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
                    uid = sharedPreferences.getInt("uid",0);
                    String uname = sharedPreferences.getString("uname","wxw");
                    String urlStr = "http://192.168.2.102:8080/video/post?uid="+uid;
                    //1.找水源---创建URL（统一资源定位器）
                    URL url=new URL(urlStr);
                    //2.开水闸---openCOnnection
                    HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
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
                    JSONArray jsonArray = JSON.parseArray(result);
                    for (int i = 0;i < jsonArray.size();i++){
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        Video video = new Video();
                        video.setId(object.getIntValue("id"));
                        video.setUid(uid);
                        video.setPhotoPath(object.getString("photoPath"));
                        video.setPostTime(object.getTimestamp("postTime"));
                        video.setTitle(object.getString("title"));
                        video.setUname(uname);
                        video.setVideoPath(object.getString("videoPath"));
                        videos.add(video);
                        System.out.println(video.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //视频list view适配器
    class VideoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return videos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Video video = videos.get(position);
            View view;
            if (convertView == null){
                view = View.inflate(PersonalActivity.this,R.layout.video_listview,null);
            } else {
                view = convertView;
            }

            final ImageView photo = view.findViewById(R.id.photo);
            TextView title = view.findViewById(R.id.title);
            TextView up = view.findViewById(R.id.up);

            title.setText(video.getTitle());
            up.setText(video.getUname());

            Thread thread  = new Thread(new Runnable() {
                @Override
                public void run() {
                    PersonalActivity.this.runOnUiThread(new Runnable() {
                        String photoPath = "http://39.107.124.222:8080/photo/" + video.getPhotoPath();
                        Bitmap photo1 = getBitmap(photoPath);
                        @Override
                        public void run() {
                            photo.setImageBitmap(photo1);
                        }
                    });
                }
            });
            thread.start();

            return view;
        }
    }

    //点击跳转播放界面
    class VideoListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Video video = videos.get(position);
            Intent intent = new Intent();
            intent.putExtra("videoPath",video.getVideoPath());
            intent.setClass(PersonalActivity.this,VideoActivity.class);
            startActivity(intent);
        }
    }
}
