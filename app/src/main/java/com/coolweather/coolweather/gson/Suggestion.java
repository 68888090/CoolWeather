package com.coolweather.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    @SerializedName("cw")
    public Carwash carwash;
    public class Carwash{
        @SerializedName("txt")
        public String info;
    }
    public Sport sport;
    public class Sport{
        @SerializedName("txt")
                public String info;
    }
}
