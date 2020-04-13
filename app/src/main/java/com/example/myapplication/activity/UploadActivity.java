package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.myapplication.R;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class UploadActivity extends AppCompatActivity {

    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private Button upload;
    private EditText videoName;
    private Button open;
    private String path;
    private TextView showVideo;
    private Button choosePhoto;
    private String photoPath;
    private ImageView showphoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        init();
    }

    void init(){
        upload = findViewById(R.id.upload);
        showVideo = findViewById(R.id.showVideo);
        videoName = findViewById(R.id.videonameinput);
        choosePhoto = findViewById(R.id.choosePhoto);
        showphoto = findViewById(R.id.showPhoto);
        choosePhoto.setOnClickListener(new PhotoListener());
        open = findViewById(R.id.openDirectory);
        open.setOnClickListener(new Listener());
        upload.setOnClickListener(new UploadListener());
    }

    class Listener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //跳到图库
            Intent intent = new Intent(Intent.ACTION_PICK);
            //选择的格式为视频,图库中就只显示视频（如果图片上传的话可以改为image/*，图库就只显示图片）
            intent.setType("video/*");
            // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
        }
    }

    class PhotoListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //跳到图库
            Intent intent = new Intent(Intent.ACTION_PICK);
            //选择的格式为视频,图库中就只显示视频（如果图片上传的话可以改为image/*，图库就只显示图片）
            intent.setType("image/*");
            // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();
        path = getRealPathFromURI(uri);

        Log.d("path", "path==" + path);
        File file = new File(path);
        if(path.endsWith(".mp4")){
            showVideo.setText(path);
        } else if (path.endsWith(".jpg") || path.endsWith(".png")){
            photoPath = path;
            showphoto.setImageURI(Uri.fromFile(new File(photoPath)));
        }
    }

    //上传封面照片
    private void uploadPhoto(final String photoPath){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                File file = new File(photoPath);
                String filename = photoPath.substring(photoPath.lastIndexOf('/') + 1);
                System.out.println(filename);
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
                int uid = sharedPreferences.getInt("uid",0);

                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"getUpTime\""),
                                RequestBody.create(null, "2020-4-11"))
                        .addFormDataPart("filename",filename)
                        .addFormDataPart("uid",uid+"")
                        .addPart(Headers.of("Content-Disposition", "form-data; name=\"originalData\"; filename=\"" + filename + "\""), fileBody)
                        .build();

                String url = "http://192.168.2.102:8080/video/uploadPhoto";
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                Call call = okHttpClient.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Log.e("photo", "failure upload photo!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("text", "success upload video!");
                        String json = response.body().string();
                        Log.i("success........","成功"+json);
                    }
                });

            }
        }).start();
    }

    public void upload(final String filePath, final String title){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                File file = new File(filePath);
                String filename = filePath.substring(filePath.lastIndexOf('/') + 1);

                SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
                int uid = sharedPreferences.getInt("uid",0);
                String uname = sharedPreferences.getString("uname","wxw");


                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"getUpTime\""),
                                RequestBody.create(null, "2020-4-11"))
                        .addFormDataPart("filename",filename)
                        .addFormDataPart("uid",""+uid)
                        .addFormDataPart("uname",uname)
                        .addFormDataPart("title",title)
                        .addFormDataPart("photoPath",photoPath.substring(photoPath.lastIndexOf('/') + 1))
                        .addPart(Headers.of("Content-Disposition", "form-data; name=\"originalData\"; filename=\"" + filename + "\""), fileBody)
                        .build();

                String url = "http://192.168.2.102:8080/video/upload";
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                Call call = okHttpClient.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Log.e("video", "failure makevideo!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("text", "success makevideo!");
                        String json = response.body().string();
                        Log.i("success........","成功"+json);
                    }
                });

            }
        }).start();
    }


    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    //点击上传之后的事件
    class UploadListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String title = videoName.getText().toString();
            upload(path,title);
            uploadPhoto(photoPath);
        }
    }
}
