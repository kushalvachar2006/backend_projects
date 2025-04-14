package com.example.weather_application;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class WeatherResponse {
    @SerializedName("name")
    public String name;

    @SerializedName("main")
    public Main main;
    @SerializedName("weather")
    public List<Weather> weather;

    public class Main {
        @SerializedName("temp")
        public float temp;
        @SerializedName("humidity")
        public float hum;
        @SerializedName("country")
        public String []con;

    }
    public class Weather {
        @SerializedName("main")
        public String main;  // e.g., Clear, Clouds, Rain

        @SerializedName("description")
        public String description;  // e.g., scattered clouds
    }

}

