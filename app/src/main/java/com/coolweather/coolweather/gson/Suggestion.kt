package com.coolweather.coolweather.gson

import com.google.gson.annotations.SerializedName

public class Suggestion {
    @SerializedName("conf")
    var comfort=Comfort()
    inner class Comfort{
        @SerializedName("txt")
        var info=String
    }

    @SerializedName("cw")
    var carwash=Carwash()
    inner class Carwash{
        @SerializedName("txt")
        var info=String

    }
    var sport=Sport()
    inner class Sport{
        @SerializedName("txt")
        var info=String
    }
}