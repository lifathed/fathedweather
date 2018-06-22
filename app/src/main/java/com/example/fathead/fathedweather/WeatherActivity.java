package com.example.fathead.fathedweather;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fathead.fathedweather.gson.Forecast;
import com.example.fathead.fathedweather.gson.Weather;
import com.example.fathead.fathedweather.service.AutoUpdateService;
import com.example.fathead.fathedweather.util.HttpUtil;
import com.example.fathead.fathedweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;
    public DrawerLayout drawerLayout;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            //判断android系统是否大于等于21，因为使用这个办法将背景图和状态栏融合在一起只有Android5.0以上才支持
            View decorView=getWindow().getDecorView();
            //得到当前活动的DecorView（DecorView即是窗口最顶层的视图）
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置活动的布局会显示在状态栏上面
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            //将状态栏设置成透明
        }



        setContentView(R.layout.activity_weather);
        //初始化各控件
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button)findViewById(R.id.nav_button);
        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        //设置下拉刷新进度条的颜色
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        weatherLayout=(ScrollView) findViewById(R.id.weather_layout);
        titleCity=(TextView) findViewById(R.id.title_city);
        titleUpdateTime=(TextView) findViewById(R.id.title_update_time);
        degreeText=(TextView) findViewById(R.id.degree_text);
        weatherInfoText=(TextView) findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout) findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        // SharedPreferences是个轻量级的存储类，特别适合用于保存软件配置参数。
        // 使用SharedPreferences 保存数据，其背后是用xml文件存放数据，文件存放在/data/data/<package name>/shared_prefs 目录下
        //每个应用都有一个默认的配置文件preferences.xml，使用getDefaultSharedPreferences获取
        String weatherString=prefs.getString("weather",null);
        if (weatherString!=null){
            //有缓存直接解析天气数据
            Weather weather= Utility.handleWeatherResponse(weatherString);
            mWeatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else {
            //无缓存到服务器查询数据
            mWeatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            //因为向服务器请求数据，此时页面为空，所以将Scrollview隐藏起来，不然看起来有点奇怪
            requestWeather( mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            //设置下拉刷新监听器
            @Override
            public void onRefresh(){
                requestWeather(mWeatherId);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        String bingPic=prefs.getString("bing_pic",null);
        if (bingPic!=null){
            //判断SharedPreferences是否有缓存图片
            Glide.with(this).load(bingPic).into(bingPicImg);
            //如果有直接用Glide加载图片
        }else {
            loadBingPic();
            //没有就请求必应的实时背景图片
        }
    }
    /*
    *根据天气id请求城市天气信息
     */

   public void requestWeather(final String weatherId) {
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=87cf23db9eea4f58aa3d702fd0b7ba56";
        //使用天气id和申请好的APIKey拼装一个接口地址
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            //发出请求
            @Override
            public void onResponse(Call call,Response response)throws IOException{
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                //用handleWeatherResponse将JSON数据转换成weather对象
                runOnUiThread(new Runnable() {
                    //切回主线程，因为发送请求一般比较耗时，在子线程进行
                    @Override
                    public void run() {
                        if (weather!=null&&"ok".equals(weather.status)){
                            //判断服务器返回的status状态是否为ok
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            //获得SharedPreferences.Editor对象
                            editor.putString("weather",responseText);
                            //通过SharedPreferences.Editor接口的putXxx（）方法存放key-value
                            editor.apply();
                            //实现shared存储（数据保存）
                            mWeatherId=weather.basic.weatherId;
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                        //表示刷新事件结束，隐藏刷新进度条
                    }
                });
                loadBingPic();
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }

        });
    }
    /*
    *处理并显示weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;

        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        //注意split(" ")双引号里面的空格，没有空格更新时间无法显示
        String degree=weather.now.temperature+"°C";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast:weather.forecastList){
            //用for循环来处理未来几天的天气预报
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            //加载forecast_item布局
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度:"+weather.suggestion.comfort.info;
        String carWash="洗车指数:"+weather.suggestion.carWash.info;
        String sport="运动建议："+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        //将Scrollview重新设成可见
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
        //激活AutoUpdateService

    }
    /*
    *加载必应每日一图
     */
    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            //获取必应图片链接
            @Override
            public void onResponse(Call call,Response response)throws IOException{
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                //将链接缓存到SharedPreferences
                runOnUiThread(new Runnable() {
                    //UI更新需在主线程（发送网络请求都在子线程）
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                        //使用Glide加载图片
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

        });
    }
}
