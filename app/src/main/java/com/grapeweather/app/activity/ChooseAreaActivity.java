package com.grapeweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grapeweather.app.R;
import com.grapeweather.app.db.City;
import com.grapeweather.app.db.County;
import com.grapeweather.app.db.GrapeWeatherDB;
import com.grapeweather.app.db.Province;
import com.grapeweather.app.utils.HttpCallBackListener;
import com.grapeweather.app.utils.HttpUtil;
import com.grapeweather.app.utils.Utility;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fancheng on 2016/6/21.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private int currentLevel;
    private ListView listView;
    private TextView titleView;
    private List<String> list = new ArrayList<String>();
    private ArrayAdapter adapter;
    private List<Province> provinceList;
    private GrapeWeatherDB grapeWeatherDB;
    private ProgressDialog progressDialog;
    private Province selectedProvince;
    private List<City> cityList;
    private City selectedCity;
    private List<County> countyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView)findViewById(R.id.list_view);
        titleView = (TextView)findViewById(R.id.text_title);
        grapeWeatherDB = GrapeWeatherDB.getInstance(this);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounty();
                }
            }
        });
        queryProvinces();
    }
    /**
     * 根据选中的市，从数据库中查询所有的县，如没有，从网络查询
     */
    private void queryCounty(){
        countyList = grapeWeatherDB.loadCounty(selectedCity.getId());
        if(countyList!=null&&countyList.size()>0){
            list.clear();
            for(County county : countyList){
                list.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleView.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.getCityCode(),"county");
        }

    }
    /**
     * 根据选中的省，从数据库中查询所有的市，如没有，从网络查询
     */
    private void queryCities(){
       cityList = grapeWeatherDB.loadCities(selectedProvince.getId());
       if(cityList != null && cityList.size()>0){
           list.clear();
           for(City city : cityList){
               list.add(city.getCityName());
           }
           adapter.notifyDataSetChanged();
           listView.setSelection(0);
           titleView.setText(selectedProvince.getProvinceName());
           currentLevel = LEVEL_CITY;
       }else {
           queryFromServer(selectedProvince.getProvinceCode(),"city");
       }
    }
    /**
     * 从数据库查询全国所有的省，如没有，从网络查询
     */
    private void queryProvinces(){
        provinceList = grapeWeatherDB.loadProvinces();
        if(provinceList!=null&&provinceList.size() > 0){
            list.clear();
            for(Province province : provinceList){
                list.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(null,"province");
        }

    }
    private void queryFromServer(String code,final String type){
        String address;
         if(!TextUtils.isEmpty(code)){
             address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
         }else {
             address = "http://www.weather.com.cn/data/list3/city.xml";
         }
         showProgressDialog();
        HttpUtil.sendHttpRequest(address,new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if(type.equals("province")) {
                   result = Utility.handleProvinceResponse(response, grapeWeatherDB);

                }else if(type.equals("city")){
                   result = Utility.handleCityResponse(response,grapeWeatherDB,
                           selectedProvince.getId());
                }else if(type.equals("county")){
                   result = Utility.handleCountyResponse(response,grapeWeatherDB,
                           selectedCity.getId());
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if(type.equals("province")){
                                queryProvinces();
                            }else if(type.equals("city")){
                                queryCities();
                            }else if(type.equals("county")){
                                queryCounty();
                            }
                        }
                    });
                }


            }

            @Override
            public void onError(Exception e) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       closeProgressDialog();
                       Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT);
                   }
               });
            }
        });

    }

    /**
     * 开启进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel ==LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }
}
