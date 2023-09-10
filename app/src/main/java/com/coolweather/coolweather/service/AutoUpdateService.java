package com.coolweather.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.coolweather.coolweather.WeatherActivity;
import com.coolweather.coolweather.gson.Weather;
import com.coolweather.coolweather.util.HttpUtil;
import com.coolweather.coolweather.util.Utility;

import java.io.IOException;
import java.security.Provider;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
//    /**
//     * Construct a new service.
//     *
//     * @param provider   the provider that offers this service
//     * @param type       the type of this service
//     * @param algorithm  the algorithm name
//     * @param className  the name of the class implementing this service
//     * @param aliases    List of aliases or null if algorithm has no aliases
//     * @param attributes Map of attributes or null if this implementation
//     *                   has no attributes
//     * @throws NullPointerException if provider, type, algorithm, or
//     *                              className is null
//     */
//    public AutoUpdateService(Provider provider, String type, String algorithm, String className, List<String> aliases, Map<String, String> attributes) {
//        super(provider, type, algorithm, className, aliases, attributes);
//    }
    private final static String TAG = "aaa";
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG, "enter the Service");
        updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int alarmHour = 8*60*60*1000;
        long trigggerTime = SystemClock.elapsedRealtime()+alarmHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,trigggerTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }


    public void updateWeather(){
        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = pres.getString("weather",null);
        if (weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherid = weather.basic.weatherId;

            String weatherUrl = "http://guolin.tech/api/weather?cityid="+
                    weatherid+"&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtil.sendOKHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather1 = Utility.handleWeatherResponse(responseText);
                    if (weather!=null && "ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                        Log.d(TAG, responseText+"enter the Service");
                    }
                }
            });
        }
    }

}

