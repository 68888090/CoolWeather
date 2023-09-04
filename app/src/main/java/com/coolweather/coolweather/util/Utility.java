package com.coolweather.coolweather.util;

import android.text.TextUtils;

import com.coolweather.coolweather.db.City;
import com.coolweather.coolweather.db.County;
import com.coolweather.coolweather.db.Province;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /**
     * 处理数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (TextUtils.isEmpty(response)) {
            try {
                JSONArray allowProvinces = new JSONArray();
                for (int i = 0; i < allowProvinces.length(); i++) {
                    JSONObject provinceobject = allowProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceobject.getString("name"));
                    province.setProvinceCode(provinceobject.getInt("id"));
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceId) {
        if (TextUtils.isEmpty(response)) {
            try {
                JSONArray allowCities = new JSONArray();
                for (int i = 0; i < allowCities.length(); i++) {
                    JSONObject cityobject = allowCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityobject.getString("name"));
                    city.setCityCode(cityobject.getInt("id"));
                    city.setProvinceId(provinceId);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCountyResponse(String response,int cityId){
        if (TextUtils.isEmpty(response)){
            try {
                JSONArray allowCounties=new JSONArray();
                for (int i=0;i<allowCounties.length();i++){
                    JSONObject countyobject=allowCounties.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyobject.getString("name"));
                    county.setWeatherId(countyobject.getString("weather_id"));
                    county.setCityId(cityId);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
