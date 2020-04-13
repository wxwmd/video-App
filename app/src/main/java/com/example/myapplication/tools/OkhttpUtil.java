package com.example.myapplication.tools;

import android.util.Log;

import java.io.File;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class OkhttpUtil {

    private static OkhttpUtil instance;

    private OkhttpUtil(){

    }
    public static OkhttpUtil getInstance(){
        if(instance == null){
            instance = new OkhttpUtil();
        }
        return instance;
    }

    /**
     * 通过get 提交
     * @param url
     * @return
     */
    public Call get(String url){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        return call;
    }

    /**
     * 通过post方式提交表单
     * @param url 提交地址
     * @param map 表单数据
     * @return Call
     */
    public Call post(String url, Map<String,Object> map){
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        Set<String> keys = map.keySet();
        for(String k:keys){
            formBody.add(k,map.get(k).toString());
        }

        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        return client.newCall(request);
    }

    /**
     * 通过post提交json数据
     * @param url 提交地址
     * @param json 提交的实例
     * @return call
     */
    public Call post(String url,String json){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);

        Request request = new Request.Builder()
                .url(url)//请求的url
                .post(requestBody)
                .build();
        return client.newCall(request);
    }

    /**
     * okhttp对文件的操作
     * @param filepath 文件路径
     * @param url 文件地址
     * @param filename //文件名称
     * @return
     */
    public Call file_submit(String filepath,String url,String filename){
        OkHttpClient client = new OkHttpClient();
        File file = new File(filepath);
        Log.i("text",filepath);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        //请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"filename\""),
                        RequestBody.create(null, "lzr"))//这里是携带上传的其他数据
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"mFile\"; filename=\"" + filename + "\""), fileBody)
                .build();
        //请求的地址
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return client.newCall(request);
    }
}
