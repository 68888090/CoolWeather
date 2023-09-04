package com.coolweather.coolweather.gson

import com.google.gson.annotations.SerializedName

public class Forecast {
    @SerializedName("tmp")
    var temperature=Temperature()
    inner class Temperature{
        var max=String
        var min=String
    }
    @SerializedName("cond")
    var more=More()
    inner class More{
        @SerializedName("txt_d")
        var info=String
    }
}