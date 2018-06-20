package com.example.fathead.fathedweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by fathead on 2018/6/14.
 */

public class HttpUtil {
        //与服务器进行交互，获取省市县的数据
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        //发送HTTP请求 （okhttp3.Callback callback是OkHttp库中自带的一个回调接口）
        OkHttpClient client=new OkHttpClient();
        //创建client对象
        Request request=new Request.Builder().url(address).build();
        //创建request对象
        client.newCall(request).enqueue(callback);
        //Okhttp会在enqueue方法创建好子线程，然后在子线程中执行Http请求（避免主线程阻塞），并将最后返回的请求结果回调到okhttp3.Callback中
    }
}
