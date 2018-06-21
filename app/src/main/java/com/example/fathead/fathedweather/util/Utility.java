package com.example.fathead.fathedweather.util;

import android.text.TextUtils;

import com.example.fathead.fathedweather.db.City;
import com.example.fathead.fathedweather.db.County;
import com.example.fathead.fathedweather.db.Province;
import com.example.fathead.fathedweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fathead on 2018/6/14.
 */

public class Utility {
    //解析和处理服务器返回的省市县数据（JSON格式）
    public static  boolean handleProvinceResponse(String response){
        //解析和处理服务器返回的省数据
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces=new JSONArray(response);
                //数据传入到JSONArray对象中
                for (int i=0;i<allProvinces.length();i++){
                    //循环遍历每一个省的数据
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    //取出的每一个数据为JSONObject对象
                    Province province=new Province();
                    //新建Province对象
                    province.setProvinceName(provinceObject.getString("name"));
                    //用getString（）方法取出province的name
                    province.setProvinceCode(provinceObject.getInt("id"));
                    //用getString（）方法取出province的id
                    province.save();
                    //将数据保存到数据库
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }



    public static  boolean handleCityResponse(String response,int provinceId){
        //解析和处理服务器返回的省数据
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities=new JSONArray(response);
                //数据传入到JSONArray对象中
                for (int i=0;i<allCities.length();i++){
                    //循环遍历每一个市的数据
                    JSONObject cityObject=allCities.getJSONObject(i);
                    //取出的每一个数据为JSONObject对象
                    City city=new City();
                    //新建Province对象
                    city.setCityName(cityObject.getString("name"));
                    //用getString（）方法取出province的name
                    city.setCityCode(cityObject.getInt("id"));
                    //用getString（）方法取出province的id
                    city.setProvinceId(provinceId);
                    city.save();
                    //将数据保存到数据库
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static  boolean handleCountyResponse(String response,int cityId){
        //解析和处理服务器返回的县数据
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties=new JSONArray(response);
                //数据传入到JSONArray对象中
                for (int i=0;i<allCounties.length();i++){
                    //循环遍历每一个市的数据
                    JSONObject countyObject=allCounties.getJSONObject(i);
                    //取出的每一个数据为JSONObject对象
                    County county=new County();
                    //新疆Province对象
                    county.setCountyName(countyObject.getString("name"));
                    //用getString（）方法取出province的name
                    county.setWeatherId(countyObject.getString("weather_id"));
                    //用getString（）方法取出天气id
                    county.setCityId(cityId);
                    county.save();
                    //将数据保存到数据库
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 将返回的json数据解析成weather实体类
     */
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            //将服务器返回的数据传入到JSONObject
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            //JSONArray，是由JSONObject构成的数组
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
            //调用fromJson（）方法将JSON数据转换成weather对象
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
