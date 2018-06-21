package com.example.fathead.fathedweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fathead on 2018/6/21.
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast>forecastList;
    //用list集合来引用Forecast类
}
