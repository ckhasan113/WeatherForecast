package com.example.weatherforecast.pref;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class WeatherPreference {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";

    public WeatherPreference(Context context) {
        preferences = context.getSharedPreferences("weather_prefs",Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setCurrentLocation(float latitude, float longitude){
        editor.putFloat(LATITUDE, latitude);
        editor.putFloat(LONGITUDE, longitude);
        editor.commit();
    }

    public Map<String, Float> getCurrentLocation(){
        Map<String, Float> latlngMap = new HashMap<>();
        latlngMap.put("lat", preferences.getFloat(LATITUDE, 0.0f));
        latlngMap.put("lng", preferences.getFloat(LONGITUDE, 0.0f));
        return latlngMap;
    }
}
