package com.coolweather.coolweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.coolweather.gson.Forecast;
import com.coolweather.coolweather.gson.Suggestion;
import com.coolweather.coolweather.gson.Weather;
import com.coolweather.coolweather.service.AutoUpdateService;
import com.coolweather.coolweather.util.HttpUtil;
import com.coolweather.coolweather.util.Utility;

import org.json.JSONException;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherScrollView;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLinearLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView imageView;

    public SwipeRefreshLayout swipeRefreshLayout;

    public DrawerLayout drawerLayout;

//    private Button navButton;

    private ImageView navButton;

    private final static String TAG="WeatherF";
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherScrollView=(ScrollView) findViewById(R.id.weather_layout);
        titleCity=(TextView) findViewById(R.id.title_city);
        titleUpdateTime=(TextView) findViewById(R.id.title_update_time);
        degreeText=(TextView) findViewById(R.id.degree_text);
        weatherInfoText=(TextView) findViewById(R.id.weather_info_text);
        forecastLinearLayout=(LinearLayout) findViewById(R.id.forecast_layout);
        aqiText=(TextView) findViewById(R.id.aqi_text);
        pm25Text=(TextView) findViewById(R.id.pm25_text);
        carWashText=(TextView) findViewById(R.id.car_wash_text);
        comfortText=(TextView) findViewById(R.id.comfort_text);
        sportText=(TextView) findViewById(R.id.sport_text);
        imageView=(ImageView) findViewById(R.id.bing_pic_img);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(com.google.android.material.R.color.design_default_color_primary);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(ImageView)findViewById(R.id.nav_button);
        SharedPreferences pres= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = pres.getString("weather",null);
        final String weatherId;
        /**
         * 进去的weather字节就是response的body部分
         */
        if (weatherString!=null){
                Weather weather= Utility.handleWeatherResponse(weatherString);
                weatherId=weather.basic.weatherId;
                showWeatherInfo(weather);
        }
        else {
            weatherId = getIntent().getStringExtra("weather_id");
            weatherScrollView.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        String bingPic=pres.getString("bing_pic",null);
        if (bingPic!=null) Glide.with(this).load(bingPic).into(imageView);
        else loadBingpic();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void requestWeather(final String weatherid){
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+
                weatherid+"&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOKHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "send Ok Failure");
                        Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                Log.d(TAG, responseText);
                Log.d(TAG, " ahead is response  "+response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather!=null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);

                        }else {
                            Log.d(TAG, "weather judge failure");
                            Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    protected void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature+"`C";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLinearLayout.removeAllViews();
        if (weather.forecastList!=null)
        for (Forecast forecast:weather.forecastList){
            Log.d(TAG, "null");
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLinearLayout,false);
            /**
             *   将获取到的布局加载到from（this）中
             */
            TextView dateText = (TextView) view.findViewById(R.id.data_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.max_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLinearLayout.addView(view);
        }
        Log.d(TAG, "weather is null "+" "+weather.suggestion.comfort.info);
        if(weather.aqi!= null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);

        }
        String comfort = "舒适度 "+ weather.suggestion.comfort.info;
        String carWash = "洗车指数 "+ weather.suggestion.carwash.info;
        String sport = "运动建议 "+ weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherScrollView.setVisibility(View.VISIBLE);
        Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
        startService(intent);
    }

    public void loadBingpic(){
        /*这里还有一个后被方案，因为在实际进入网址时，虽然确实能够返回body，但是实际上返回的是一个错误报错
        * */
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOKHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                final String bingPic = response.body().string();
//                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
//                editor.putString("bing_pic",null);
//                editor.apply();
                runOnUiThread(new Runnable() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void run() {
                        Log.d(TAG, bingPic);
                            Log.d(TAG, "use local resource");
                        int picRes = new Random().nextInt(9);
                        switch (picRes){
                            case 0 :picRes=R.drawable.a0;
                            break;
                            case 1 :picRes=R.drawable.a1;
                                break;
                            case 2 :picRes=R.drawable.a2;
                                break;
                            case 3 :picRes=R.drawable.a3;
                                break;
                            case 4 :picRes=R.drawable.a5;
                                break;
                            case 5 :picRes=R.drawable.a4;
                                break;
                            case 6 :picRes=R.drawable.a6;
                                break;
                            case 7 :picRes=R.drawable.a7;
                                break;
                            case 8 :picRes=R.drawable.a8;
                                break;
                        }
                            imageView.setImageDrawable(getResources().getDrawable(picRes));
//                        }
                    }
                });
            }
        });
    }
}