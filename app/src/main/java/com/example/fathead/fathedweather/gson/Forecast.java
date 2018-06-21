package com.example.fathead.fathedweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fathead on 2018/6/21.
 */

//当JSON数据为一个数组时，只需要定义一个实体类就可以，然后在声明实体类引用的时候使用集合类型来进行声明
public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;
    public class Temperature{
        public String max;
        public String min;
    }
    public class More{
        @SerializedName("txt_d")
        public String info;
        //汉字的时候为info
    }
}
