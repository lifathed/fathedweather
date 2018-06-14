package com.example.fathead.fathedweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by fathead on 2018/6/14.
 */

public class Province extends DataSupport {
    private int id;
    private String provinceName;
    private int provinceCode;
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getprovinceName(){
        return provinceName;
    }
    public void setprovinceName(String provinceName){
        this.provinceName=provinceName;
    }
    public int getprovinceCode(){
        return provinceCode;
    }
    public void setprovinceCode(int provinceCode){
        this.provinceCode=provinceCode;
    }
}
