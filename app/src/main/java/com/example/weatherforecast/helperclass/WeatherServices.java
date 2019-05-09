package com.example.weatherforecast.helperclass;

import com.example.weatherforecast.webapi.WeatherInformation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherServices {
    @GET
    Call<WeatherInformation> getCurrentWeatherData(@Url String endUrl);
}
