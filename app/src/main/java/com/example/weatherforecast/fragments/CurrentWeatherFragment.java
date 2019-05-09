package com.example.weatherforecast.fragments;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.weatherforecast.R;
import com.example.weatherforecast.databinding.FragmentCurrentWeatherBinding;
import com.example.weatherforecast.helperclass.TemperatureConverter;
import com.example.weatherforecast.helperclass.WeatherRetrofitClient;
import com.example.weatherforecast.helperclass.WeatherServices;
import com.example.weatherforecast.pref.WeatherPreference;
import com.example.weatherforecast.webapi.WeatherInformation;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentWeatherFragment extends Fragment {

    private WeatherPreference preference;
    private FragmentCurrentWeatherBinding binding;
    private double latitude, longitude,temperatureKelvin, temperatureCelsius;
    public String tempScale = "imperial";
    private String scale = "Celsius";

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_current_weather, container, false);
        View root = ((ViewDataBinding) binding).getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preference = new WeatherPreference(getActivity());
        Map<String, Float> latLngMap = new HashMap<>();
        latLngMap = preference.getCurrentLocation();
        latitude = (double)latLngMap.get("lat");
        longitude = (double)latLngMap.get("lng");

        getCurrentWeatherData();

        getLocationAddress();
    }

    private void getLocationAddress() {
        final Geocoder geocoder = new Geocoder(getActivity());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String street = addresses.get(0).getAddressLine(0);
            binding.addressTV.setText(street);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCurrentWeatherData() {
        final String apiKey = getString(R.string.api_key0);
        final String endUrl = String.format("weather?lat=%f&lon=%f&unit=%s&appid=%s",latitude,longitude,tempScale,apiKey);

        WeatherServices services = WeatherRetrofitClient.getClient(BASE_URL)
                .create(WeatherServices.class);

        services.getCurrentWeatherData(endUrl)
                .enqueue(new Callback<WeatherInformation>() {
                    @Override
                    public void onResponse(Call<WeatherInformation> call, Response<WeatherInformation> response) {

                        if (response.isSuccessful()){
                            WeatherInformation weather = response.body();
                            String icon = weather.getWeather().get(0).getIcon();
                            Picasso.get().load("https://openweathermap.org/img/w/"+icon+".png")
                                    .into(binding.iconIV);


                            /*-------Value Assign-------*/
                            binding.placeTV.setText(weather.getName()+", "+weather.getSys().getCountry());

                            String descriptionFromAPI = weather.getWeather().get(0).getDescription();

                            String description = capitalize(descriptionFromAPI);
                            binding.descriptionTV.setText(description);

                            /*-------Date & Time-------*/
                            Date date = new Date();
                            DateFormat dateFormat = new SimpleDateFormat("hh:mm aaa\ndd/MM/yyyy");
                            String time = dateFormat.format(date);

                            binding.dateTV.setText(time);

                            /*-------Temperature-------*/
                            temperatureKelvin = weather.getMain().getTemp();
                            temperatureCelsius = TemperatureConverter.convertToCelsius(temperatureKelvin);

                            binding.tempTV.setText(String.valueOf(temperatureCelsius+" \u2103"));

                            binding.temperatureRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                    RadioButton rb = getActivity().findViewById(i);
                                    scale = rb.getText().toString();
                                    if (scale.equals("Celsius")){

                                        temperatureCelsius = TemperatureConverter.convertToCelsius(temperatureKelvin);

                                        binding.tempTV.setText(String.valueOf(temperatureCelsius+" \u2103"));
                                    }else if (scale.equals("Kelvin")){
                                        binding.tempTV.setText(String.valueOf(temperatureKelvin+" K"));
                                    }
                                }
                            });

                            /*-------Wind-------*/
                            binding.windValueTV.setText(String.valueOf("Speed: "+weather.getWind().getSpeed()+"\nDegree: "+weather.getWind().getDeg()));

                            /*-------Cloudiness-------*/
                            binding.cloudValueTV.setText(String.valueOf(weather.getClouds().getAll())+"%");

                            /*-------Pressure-------*/
                            binding.pressureValueTV.setText(String.valueOf(weather.getMain().getPressure())+" Pa");

                            /*-------Humidity-------*/
                            binding.humidityValueTV.setText(String.valueOf(weather.getMain().getHumidity())+"%");

                            /*-------Sun Rise-------*/
                            long timeLongSR = weather.getSys().getSunrise();
                            DateFormat timeFormateSR = new SimpleDateFormat("hh:mm:ss a");
                            String sunRise = timeFormateSR.format(timeLongSR);
                            binding.sunriseValueTV.setText(sunRise);

                            /*-------Sun Set-------*/
                            long timeLongSS = weather.getSys().getSunset();
                            DateFormat timeFormateSS = new SimpleDateFormat("hh:mm:ss");
                            String sunSet = timeFormateSS.format(timeLongSS);
                            binding.sunsetValueTV.setText(sunSet+" PM");

                            /*-------Geo Coords-------*/
                            binding.geoCoordsValueTV.setText(String.valueOf("["+weather.getCoord().getLat()+", "+weather.getCoord().getLon()+"]"));
                        }

                    }

                    @Override
                    public void onFailure(Call<WeatherInformation> call, Throwable t) {
                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }
}
