package com.coolweather.coolweather.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coolweather.coolweather.MainActivity;
import com.coolweather.coolweather.R;
import com.coolweather.coolweather.WeatherActivity;
import com.coolweather.coolweather.db.City;
import com.coolweather.coolweather.db.County;
import com.coolweather.coolweather.db.Province;
import com.coolweather.coolweather.util.HttpUtil;
import com.coolweather.coolweather.util.Utility;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {

    private final static String TAG="TAG";
    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView textView;

    private Button button;

    //    private RecyclerView recyclerView;
    private ListView listView;
    //    private ArrayAdapter<String> adapter;
//    private ListAdapter adapter;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectProvince;
    private City selectCity;
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        textView = (TextView) view.findViewById(R.id.title_text);
        button = (Button) view.findViewById(R.id.back_button);
//        recyclerView=(RecyclerView)view.findViewById(R.id.list_view);
//        adapter=new ListAdapter(dataList, new ListAdapter.SetOnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                if (currentLevel==LEVEL_PROVINCE){
//                    selectProvince=provinceList.get(position);
//                    queryCities();
//                }else if (currentLevel==LEVEL_CITY){
//                    selectCity=cityList.get(position);
//                    queryCounties();
//                }
//            }
//        });
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        Log.d("TAG", " oncreateview");
        return view;
    }
//顺序搞错了，是先进行oncreate，然后进行oncreateview，最后在进行onactivitycreated
    @Override
    @SuppressLint("MissingSuperCall")
    public void onActivityCreated(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("TAG", " oncreate");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        requireActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity=(WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });

        //????


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//
//    }

    @SuppressLint("NotifyDataSetChanged")
    private void queryProvinces() {
//        Log.d("TAG", " province");
        textView.setText("中国");
        button.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
//        Log.d("TAG", String.valueOf(provinceList.size()));
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
//            recyclerView.scrollToPosition(0);
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {

            String address = "http://guolin.tech/api/china";
//            Log.d("TAG", address);
            queryFromServer(address, "province");
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void queryCities() {
//        Log.d("TAG", " city");
        textView.setText(selectProvince.getProvinceName());
        button.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceid= ?", String.valueOf(selectProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
//            recyclerView.scrollToPosition(0);
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            Log.d(TAG, address);
            queryFromServer(address, "city");
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void queryCounties() {
        Log.d("TAG", " COUNty");
        textView.setText(selectCity.getCityName());
        button.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityid= ?", String.valueOf(selectCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
//            recyclerView.scrollToPosition(0);
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode+"/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_LONG).show();
//                        Log.d("TAG", e.getMessage());
                    }
                });
            }
//一直在循环
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectCity.getId());
                }
//                Log.d("TAG", "onResponse "+type);
//                Log.d("TAG", responseText);
//                Log.d("TAG", String.valueOf(result));
//                try {
//                    Thread.sleep(2000);
//                    closeProgressDialog();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}


