package com.example.weatherforecast;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.weatherforecast.fragments.CurrentWeatherFragment;
import com.example.weatherforecast.fragments.ForecastWeatherFragment;
import com.example.weatherforecast.pref.WeatherPreference;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient client;

    private boolean isLocationPermissionGranted = false;

    private double latitude, longitude, temperatureKelvin, temperatureCelsius;

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    public String tempScale = "imperial";

    private String scale = "Celsius";

    private WeatherPreference preference;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private WeatherPagerAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = LocationServices.getFusedLocationProviderClient(this);

        preference = new WeatherPreference(this);

        chechkLocationPermissionGranted();

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);

        adapter = new WeatherPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.current_weather_green));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.forecast_weather_green));

        tabLayout.setTabIconTint(ColorStateList.valueOf(Color.GREEN));

        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void chechkLocationPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    007);
        } else {

            isLocationPermissionGranted = true;

            getDeviceCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 007 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isLocationPermissionGranted = true;

            getDeviceCurrentLocation();

        } else {
            Toast.makeText(this, "Please allow permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDeviceCurrentLocation() {
        if (isLocationPermissionGranted){
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location == null){
                        return;
                    }

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    preference.setCurrentLocation((float) latitude, (float) longitude);
                }
            });
        }
    }

    private class WeatherPagerAdapter extends FragmentPagerAdapter{

        public WeatherPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0: return new CurrentWeatherFragment();

                case 1: return new ForecastWeatherFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
