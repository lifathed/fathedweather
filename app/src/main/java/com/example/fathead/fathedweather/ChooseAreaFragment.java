package com.example.fathead.fathedweather;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fathead.fathedweather.db.City;
import com.example.fathead.fathedweather.db.County;
import com.example.fathead.fathedweather.db.Province;
import com.example.fathead.fathedweather.util.HttpUtil;
import com.example.fathead.fathedweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;
/**
 * Created by fathead on 2018/6/14.
 */

//注意fragment（碎片）是不能直接显示在界面的，需要添加到活动（activity_main）

public class ChooseAreaFragment extends Fragment{
    //遍历省市县数据的碎片
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    //listview的级别，共三级，第0级为province，第1级为city，第2级为county
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String>adapter;
    private List<String> dataList=new ArrayList<>();
    private List<Province>provinceList;
    private List<City>cityList;
    private List<County>countyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;
    //选中listview的级别（是省，市还是县）
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.choose_area,container,false);
        //通过layoutInflater的inflate方法将choose_area布局加载进来
        titleText=(TextView) view.findViewById(R.id.title_text);
        backButton=(Button) view.findViewById(R.id.back_button);
        listView=(ListView) view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        //adapter的初始化，如果程序的mindsdk小于23，则getcontext出现错误
        listView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        //给listview和backbutton设置按钮点击监听
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>parent,View view,int position,long id){
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    //通过position参数判断点击的是哪一个省
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if (currentLevel==LEVEL_COUNTY){
                    //判断如果此时点击的是县这个级别，则启动weatheractivity，并把当前选中县的天气id传递过去
                    String weatherId=countyList.get(position).getWeatherId();
                    if (getActivity()instanceof MainActivity) {
                         //instanceof可以判断一个对象是否属于某个类的实例
                        //如果在mainactivity，则处理逻辑不变
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity()instanceof WeatherActivity){
                        //如果在weatheractivity，则关闭滑动菜单，显示下拉进度条，然后请求新城市等天气信息
                        WeatherActivity activity=(WeatherActivity)getActivity();
                        activity.drawerLayout.closeDrawers();
                        //关闭滑动菜单
                        activity.swipeRefresh.setRefreshing(true);
                        //显示下拉进度条
                        activity.requestWeather(weatherId);
                        //请求新城市等天气信息

                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(currentLevel==LEVEL_COUNTY){
                    //判断当前listview的列表级别，如果是县就显示市
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    //判断当前listview的列表级别，如果是市就显示省
                    queryProvinces();
                }
            }
        });
        queryProvinces();
        //开始加载省级数据
        //如果换成querycities（），因为方法的开头就是通过getProvinceName来得到省的名字来设置标题，因为此时不知道是那个省，所以程序崩溃
    }
    private void queryProvinces(){
        //查询全国的省，先从数据库查询，如果没有再从服务器查询
        titleText.setText("中国");
        //设置标题
        backButton.setVisibility(View.GONE);
        //将标题的返回按键隐藏，因为是省级，不能在返回了
        provinceList= DataSupport.findAll(Province.class);
        //使用litepal的查询接口从数据库中读取省级数据
        if (provinceList.size()>0){
            //如果数据库中有，就直接将数据显示到界面
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
                //将数据显示到界面
            }
            adapter.notifyDataSetChanged();
            //刷新listview
            listView.setSelection(0);
            //将listview的第“0”行，即第一行显示在最上面
            currentLevel=LEVEL_PROVINCE;

        }else {
            String address="http://guolin.tech/api/china";
            //从服务器上查询数据
            queryFromServer(address,"province");
        }
    }


    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        //通过点击那个省来读取市的数据（用litepal的查询接口）
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            //china后面的“/”如果没有，市就加载不出来，如果接口过期不可用也会出现同样情况
            queryFromServer(address, "city");
        }
    }
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList= DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;

        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }






    private void queryFromServer(String address, final String type) {
        //从服务器上查询数据
        showProgressDialog();
        //显示加载对话框
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            //向服务器发送请求，响应的数据会回调到onResponse方法中（即接下来的方法）
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    //if里面的是判断返回的是哪一个数据
                    result = Utility.handleProvinceResponse(responseText);
                    //调用Utility的handleProvinceResponse方法来解析和处理服务器返回的数据，并储存到数据库中
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    //如果储存到数据库后（resuit=1）
                    getActivity().runOnUiThread(new Runnable() {
                        //则用runOnUiThread方法实现从子线程切换到主线程（因为ui发生变化，需要在主线程操作）
                        //HttpUtil.java的Okhttp会在enqueue方法创建好子线程，然后在子线程中执行Http请求（避免主线程阻塞）
                        //总逻辑就是向服务器发送请求并返回数据（在子线程），返回数据后对界面进行更新改变（在主线程）
                        @Override
                        public void run() {
                            closeProgressDialog();
                            //加载完关闭对话框
                            if ("province".equals(type)) {
                                //判断返回的是哪一个数据
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                                //判断返回的是哪一个数据
                            } else if ("county".equals(type)) {
                                queryCounties();
                                //判断返回的是哪一个数据
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call call, IOException e){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载......");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
