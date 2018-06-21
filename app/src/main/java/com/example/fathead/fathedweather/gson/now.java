package com.example.fathead.fathedweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fathead on 2018/6/21.
 */

public class now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}
