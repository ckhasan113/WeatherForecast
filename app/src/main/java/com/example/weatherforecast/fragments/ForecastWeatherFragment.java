package com.example.weatherforecast.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.ForcastAdapter;
import com.example.weatherforecast.databinding.FragmentForecastWeatherBinding;
import com.example.weatherforecast.pref.WeatherPreference;
import com.example.weatherforecast.webapi.forecastweather.ForcastClient;
import com.example.weatherforecast.webapi.forecastweather.ForecasetWeather;
import com.example.weatherforecast.webapi.forecastweather.ForecastService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastWeatherFragment extends Fragment {

    private FragmentForecastWeatherBinding binding;
    private WeatherPreference preference;
    private ForcastAdapter adapter;

    private double latitude, longitude;

    public static final String BASE_URL ="https://api.openweathermap.org/data/2.5/";

    public ForecastWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forecast_weather, container, false);
        return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preference = new WeatherPreference(getActivity());
        Map<String, Float> latLngMap = new HashMap<>();
        latLngMap = preference.getCurrentLocation();
        latitude = (double)latLngMap.get("lat");
        longitude = (double)latLngMap.get("lng");

        getForcastWeather();
    }

    private void getForcastWeather() {
        ForecastService service = ForcastClient
                .getClient(BASE_URL)
                .create(ForecastService.class);
        String endUrl = String
                .format("forecast/daily?lat=%f&lon=%f&units=metric&cnt=16&appid=%s",
                        latitude,longitude,getString(R.string.weatherApi_get_from_sir));

        service.getForcaseWeather(endUrl)
                .enqueue(new Callback<ForecasetWeather>() {
                    @Override
                    public void onResponse(Call<ForecasetWeather> call, Response<ForecasetWeather> response) {
                        if (response.isSuccessful()){
                            // Toast.makeText(getActivity(), response.body().toString(), Toast.LENGTH_SHORT).show();
                            forcastweatherUIupdate(response.body());
                        }else {
                            Toast.makeText(getActivity(), String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecasetWeather> call, Throwable t) {

                    }
                });
    }

    private void forcastweatherUIupdate(ForecasetWeather forecasetWeather) {

        adapter = new ForcastAdapter(forecasetWeather);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        binding.forecastRecycler.setAdapter(adapter);
        binding.forecastRecycler.setLayoutManager(linearLayoutManager);

    }
}
