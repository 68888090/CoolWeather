package com.coolweather.coolweather.gson

public class AQI {
    val AQIcity=AQICity()
    inner class AQICity{
        var api: String = ""
        var pm25:String = ""
    }
}