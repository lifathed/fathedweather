package com.example.fathead.fathedweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        //从SharedPreferences文件中读取缓存数据
        if (prefs.getString("weather",null)!=null){
            //如果SharedPreferences文件中缓存数据不为null，则说明已经请求过天气数据，就不用让用户再次选择城市，直接跳转到weatheractivity就可以
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
