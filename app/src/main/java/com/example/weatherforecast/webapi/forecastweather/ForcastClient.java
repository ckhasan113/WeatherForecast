package com.example.weatherforecast.webapi.forecastweather;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForcastClient {
    public  static Retrofit getClient(String baseurl){
        return new Retrofit
                .Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
