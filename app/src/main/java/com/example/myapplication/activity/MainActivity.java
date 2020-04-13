package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class MainActivity extends AppCompatActivity {

    private List<Video> videos;
    private ListView videolist;
    private ImageView avatar;
    private ImageView make;
    private ImageView watch;
    private ImageView myposts;
    private String avatarPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            init();
            videolist = findViewById(R.id.videos);
            make = findViewById(R.id.make);
            make.setOnClickListener(new MakeListener());
            watch = findViewById(R.id.watch);
            myposts = findViewById(R.id.myposts);
            videolist.setAdapter(new VideoAdapter());
            videolist.setOnItemClickListener(new VideoListener());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //初始化头像路径
    public String initAvatarPAth() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
                    int uid = sharedPreferences.getInt("uid",0);
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
                    String result = stringBuffer.toString();
                    avatarPath = result;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        t1.join();
        System.out.println(avatarPath);
        return avatarPath;
    }

    public void init() throws InterruptedException {
        System.out.println("init");
        avatar = findViewById(R.id.avatar);
        //初始化头像
        Thread thread  = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        String photoPath = "http://39.107.124.222:8080/avatar/" + initAvatarPAth();
                        Bitmap photo1 = getBitmap(photoPath);
                        @Override
                        public void run() {
                            avatar.setImageBitmap(photo1);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        videos = new ArrayList<>();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String urlStr = "http://192.168.2.102:8080/video/all";
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
                    JSONArray jsonArray = JSON.parseArray(result);
                    for (int i = 0;i < jsonArray.size();i++){
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        Video video = new Video();
                        video.setId(object.getIntValue("id"));
                        video.setUid(object.getIntValue("uid"));
                        video.setPhotoPath(object.getString("photoPath"));
                        video.setPostTime(object.getTimestamp("postTime"));
                        video.setTitle(object.getString("title"));
                        video.setUname(object.getString("uname"));
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
        t1.join();
    }

    class VideoAdapter extends BaseAdapter{

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
                view = View.inflate(MainActivity.this,R.layout.video_listview,null);
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
                    MainActivity.this.runOnUiThread(new Runnable() {
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

    public Bitmap getBitmap(String s) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(s);
            bitmap = BitmapFactory.decodeStream(url.openStream());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    class VideoListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Video video = videos.get(position);
            Intent intent = new Intent();
            intent.putExtra("videoPath",video.getVideoPath());
            intent.setClass(MainActivity.this,VideoActivity.class);
            startActivity(intent);
        }
    }


    class MakeListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,UploadActivity.class);
            startActivity(intent);
        }
    }

}
