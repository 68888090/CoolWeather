package com.coolweather.coolweather.util;

import android.view.textclassifier.TextLinks;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    public static void sendOKHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient Client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        Client.newCall(request).enqueue(callback);
    }
}
