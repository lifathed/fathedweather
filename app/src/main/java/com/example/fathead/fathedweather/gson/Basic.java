package com.example.fathead.fathedweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fathead on 2018/6/21.
 */

public class Basic {
    @SerializedName("city")
    //JSON的字段“city”作为JAVA的命名不怎么合适（cityname比较合适），所以用@SerializedName注解的方式让JSON字段和JAVA字段之间建立映射关系
    //即在使用Gson解析的时候就会将city对应的值赋值到cityName属性上
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
