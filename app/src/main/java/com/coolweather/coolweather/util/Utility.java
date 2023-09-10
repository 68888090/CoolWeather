package com.coolweather.coolweather.util;

import static android.content.ContentValues.TAG;

import android.text.TextUtils;
import android.util.Log;

import com.coolweather.coolweather.db.City;
import com.coolweather.coolweather.db.County;
import com.coolweather.coolweather.db.Province;
import com.coolweather.coolweather.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /**
     * 处理数据
     */
    private final static String TAG="Utility";

    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allowProvinces = new JSONArray(response);
                Log.d(TAG, String.valueOf(allowProvinces.length()));
                for (int i = 0; i < allowProvinces.length(); i++) {
                    JSONObject provinceobject = allowProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceobject.getString("name"));
                    province.setProvinceCode(provinceobject.getInt("id"));
                    province.save();
                    Log.d(TAG, province.getProvinceName());
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
            }
        }
        Log.d(TAG, String.valueOf(TextUtils.isEmpty(response))+"  "+response);
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allowCities = new JSONArray(response);
                for (int i = 0; i < allowCities.length(); i++) {
                    JSONObject cityobject = allowCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityobject.getString("name"));
                    city.setCityCode(cityobject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allowCounties=new JSONArray(response);
                for (int i=0;i<allowCounties.length();i++){
                    JSONObject countyobject=allowCounties.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyobject.getString("name"));
                    county.setWeatherId(countyobject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response)  {
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            Log.d(TAG, weatherContent);
            if (weatherContent==null)
                Log.d(TAG, "weather is null");
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
