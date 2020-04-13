package com.example.myapplication.activity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;
import com.example.myapplication.R;

public class VideoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        String videoPath = "http://39.107.124.222:8080/video/" + bundle.getString("videoPath");

        //横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.video);

        VideoView videoView = findViewById(R.id.videoPlayer);
        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        Uri path = Uri.parse(videoPath);
        videoView.setMediaController(controller);
        videoView.setVideoURI(path);
        videoView.start();
    }
}
