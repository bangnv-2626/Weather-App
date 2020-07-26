package com.example.weather.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import com.example.weather.adapters.HoursAdapter;
import com.example.weather.models.Hours;
import com.example.weather.models.SharedPref;
import com.example.weather.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    String apiid;
    String local;

    PullRefreshLayout pullRefreshLayout;
    ScrollView scrollView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigation_view;
    String searchText;
    String currentCity;

    SharedPref sharedPref;


    TextView txtCityName, txtCountry, txtTemperature, txtToday, txtWeatherStatus,
            txtFeelsLike, txtWind, txtPressure, txtHumidity, txtClouds, txtVisibility,
            txtSunrise, txtSunset, txtLastTime;
    ImageView imgIcon;
    Button btnNext7Days;

    RecyclerView rvHourly;
    HoursAdapter hoursAdapter;
    ArrayList<Hours> hoursArrayList = new ArrayList<>();


    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadThemeMode();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mapping();
        setupLeftNavigation();
        requestToday(getString(R.string.defaultCity));
        setupRecyclerView();
        seeNext7Days();
        pullRefreshAll();
    }


    public void loadThemeMode() {

        sharedPref = new SharedPref(this);

        if (sharedPref.loadDarkThemeState() && !sharedPref.loadLightThemeState()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (!sharedPref.loadDarkThemeState() && sharedPref.loadLightThemeState()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (!sharedPref.loadDarkThemeState() && !sharedPref.loadLightThemeState()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        }

    }

    private void seeNext7Days() {
        btnNext7Days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), NextDaysActivity.class);
                String city = txtCityName.getText().toString().trim();
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });
    }

    //chay trong dataProcessingToday()
    private void requestHourly(String lat, String lon) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&exclude=daily&units=metric&appid=%s", lat, lon, apiid);

        Log.d("ketqua url_hourly", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dataProcessingHourly(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }

    private void dataProcessingHourly(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArrayHourly = jsonObject.getJSONArray("hourly");
            for (int i = 1; i < 25; i++) {
                JSONObject object_i = jsonArrayHourly.getJSONObject(i);

                String hour_geted = object_i.getString("dt");
                long l = Long.parseLong(hour_geted);
                Date hour_empty = new Date(l * 1000L);
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
                String hour = hourFormat.format(hour_empty);

                String temp_geted = object_i.getString("temp");
                Double temp = Double.valueOf(temp_geted);

                JSONArray jsonArrayWeather = object_i.getJSONArray("weather");
                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                String icon = jsonObjectWeather.getString("icon");

//                Log.d("ketqua hour", hour);
//                Log.d("ketqua temp", String.valueOf(temp));
//                Log.d("ketqua icon", icon);

                Hours hours = new Hours(hour + ":00", changeIcon(icon), temp.intValue() + "°C");
                hoursArrayList.add(hours);
            }
            hoursAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        //already set in xml file
//        RecyclerView.LayoutManager layoutManager =
//                new LinearLayoutManager(getBaseContext(), RecyclerView.HORIZONTAL, false);
//        rvHourly.setLayoutManager(layoutManager);

        rvHourly.setHasFixedSize(true);
        hoursAdapter = new HoursAdapter(MainActivity.this, hoursArrayList);
        rvHourly.setAdapter(hoursAdapter);
    }

    private void requestToday(String cityName) {

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "";
        if (local.equals("vi")) {
            url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=vi&appid=%s", cityName, apiid);
        } else {
            url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=us&appid=%s", cityName, apiid);
        }

        Log.d("ketqua url_today", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dataProcessingToday(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }

    private void dataProcessingToday(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            //Coord
            JSONObject jsonObjectCoord = jsonObject.getJSONObject("coord");

            String lat = jsonObjectCoord.getString("lat");
            String lon = jsonObjectCoord.getString("lon");


            //Weather
            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
            // //WeatherStatus
            String weatherStatus = jsonObjectWeather.getString("description");
            weatherStatus = weatherStatus.substring(0, 1).toUpperCase() + weatherStatus.substring(1).toLowerCase(); //UpperCase s0
            // //Icon
            String icon_name = jsonObjectWeather.getString("icon");  //ex: 04d
//                            Log.d("Ketqua icon_name", icon_name);


            //Main
            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
            // //Temperature + FeelLike + Humidity + Pressure
            String temp_geted = jsonObjectMain.getString("temp");
            String feel_geted = jsonObjectMain.getString("feels_like");
            String pressure_geted = jsonObjectMain.getString("pressure");
            String humidity_geted = jsonObjectMain.getString("humidity");
            //
            double d_temp = Double.parseDouble(temp_geted);
            double d_feel = Double.parseDouble(feel_geted);
            Double d_pres = Double.valueOf(Math.round(0.75 * Double.valueOf(pressure_geted))); //convert units: hPa => mmHg
            double d_humi = Double.parseDouble(humidity_geted);

            String temperature = String.valueOf((int) d_temp);
            String feelLike = String.valueOf((int) d_feel);
            String pressure = String.valueOf(d_pres.intValue());
            String humidity = String.valueOf((int) d_humi);

//                            Log.d("ketqua temperature", temperature);
//                            Log.d("ketqua feelLike", feelLike);
//                            Log.d("ketqua pressure", pressure + "mm(Hg)");
//                            Log.d("ketqua humidity", humidity);


            //Visibility
            String visibility_geted = jsonObject.getString("visibility");
            double d_visi = 0.001f * Double.parseDouble(visibility_geted); //convert units: m => Km
            String visibility = String.valueOf((int) d_visi);

//                            Log.d("ketqua visibility", visibility + "Km");

            //Wind
            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
            String wind_geted = jsonObjectWind.getString("speed");
            double d_wind = Math.round(Double.valueOf(wind_geted) * 10) / 10.0; //lay 1 chu so thap phan
            String windSpeed = String.valueOf(d_wind);
//            Log.d("ketqua wind_geted", wind_geted);
//            Log.d("ketqua d_wind", String.valueOf(d_wind));
//            Log.d("ketqua windSpeed", windSpeed);


            //Cloud
            JSONObject jsonObjectClouds = jsonObject.getJSONObject("clouds");
            String cloudsPercent = jsonObjectClouds.getString("all");


            //dt
            String day_geted = jsonObject.getString("dt");
            // //Today + LastTime (LastUpdate)
            long l = Long.parseLong(day_geted);
            Date day_empty = new Date(l * 1000L);
//                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE  MM/dd/yyyy  HH:mm");
            SimpleDateFormat toDayFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy");
            SimpleDateFormat lastTimeFormat = new SimpleDateFormat("HH:mm:ss EEEE, dd/MM/yyyy");
            String toDay = toDayFormat.format(day_empty);
            String lastTime = lastTimeFormat.format(day_empty);


            //Sys
            JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
            // //Country
            String country = jsonObjectSys.getString("country");
            // //Sunrise + Sunset
            String sunrise_geted = jsonObjectSys.getString("sunrise");
            String sunset_geted = jsonObjectSys.getString("sunset");

            long l1 = Long.parseLong(sunrise_geted);
            long l2 = Long.parseLong(sunset_geted);
            Date sunrise_empty = new Date(l1 * 1000L);
            Date sunset_empty = new Date(l2 * 1000L);
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");

            String sunrise = hourFormat.format(sunrise_empty);
            String sunset = hourFormat.format(sunset_empty);
//                            Log.d("ketqua sunrise", sunrise);
//                            Log.d("ketqua sunset", sunset);

            //name
            String cityName = jsonObject.getString("name");


            txtCityName.setText(cityName);
            txtCountry.setText(country);
            txtTemperature.setText(String.format("%s%s", temperature, getString(R.string.temp_unit))); //Alt + 248 = °
            //set icon
            imgIcon.setImageResource(changeIcon(icon_name));
            txtToday.setText(toDay);
            txtWeatherStatus.setText(weatherStatus);

            txtFeelsLike.setText(String.format("%s%s", feelLike, getString(R.string.temp_unit)));
            txtWind.setText(String.format("%s%s", windSpeed, getString(R.string.wind_unit)));
            txtPressure.setText(String.format("%s%s", pressure, getString(R.string.pressure_unit)));
            txtHumidity.setText(String.format("%s%s", humidity, getString(R.string.percent_unit)));
            txtClouds.setText(String.format("%s%s", cloudsPercent, getString(R.string.percent_unit)));
            txtVisibility.setText(String.format("%s%s", visibility, getString(R.string.visibility_unit)));

            txtSunrise.setText(sunrise);
            txtSunset.setText(sunset);
            txtLastTime.setText(lastTime);


            //Call hourly
            requestHourly(lat, lon);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static int changeIcon(String icon_name) {
        switch (icon_name) {

            case "01d":
                return R.drawable.ic_01d_clear_sky;

            case "01n":
                return R.drawable.ic_01n_clear_sky;

            case "02d":
                return R.drawable.ic_02d_few_clouds;

            case "02n":
                return R.drawable.ic_02n_few_clouds;

            case "03d":
            case "03n":
                return R.drawable.ic_03dn_scattered_clouds;

            case "04d":
            case "04n":
                return R.drawable.ic_04dn_broken_clouds;

            case "09d":
                return R.drawable.ic_09d_shower_rain;

            case "09n":
                return R.drawable.ic_09n_shower_rain;

            case "10d":
            case "10n":
                return R.drawable.ic_10dn_rain;

            case "11d":
            case "11n":
                return R.drawable.ic_11dn_thunderstorms;

            case "13d":
            case "13n":
                return R.drawable.ic_13dn_snow;

            case "50d":
            case "50n":
                return R.drawable.ic_50dn_mist;
        }
        return R.drawable.ic_50dn_mist;
    }

    private void pullRefreshAll() {
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.setRefreshing(false);
                        //no call recreate(), cuz it is bad to refresh

                        hoursArrayList.clear();
                        if (searchText.equals("")) {
                            // have never submitted text in searchView (chua tung sumit text trong searchView)
                            requestToday(getString(R.string.defaultCity));
                        } else {
                            // used to submit searchText;
                            // searchText # current text in searchView
                            requestToday(searchText);
                        }

                    }
                }, 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                requestToday(query.trim());
                hoursArrayList.clear();
                hoursAdapter.notifyDataSetChanged();

                //get searchText
                searchText = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (!searchView.isIconified()) {
//            searchView.setIconified(true);
            searchView.onActionViewCollapsed();
        } else {
            super.onBackPressed();
        }

    }

    private void setupLeftNavigation() {
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.addCity) {
                    Toast.makeText(MainActivity.this, "Add city", Toast.LENGTH_SHORT).show();
                }
                if (id == R.id.notification) {
                    Toast.makeText(MainActivity.this, "Notification", Toast.LENGTH_SHORT).show();
                }
                if (id == R.id.setting) {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void mapping() {
        local = Locale.getDefault().getLanguage();
        apiid = "53fbf527d52d4d773e828243b90c1f8e";

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        navigation_view = findViewById(R.id.navigation_view);
        pullRefreshLayout = findViewById(R.id.pull_refresh_layout);
        scrollView = findViewById(R.id.scroll_view);


        txtCityName = findViewById(R.id.txt_city_name);
        txtCountry = findViewById(R.id.txt_country);
        imgIcon = findViewById(R.id.img_icon);
        txtTemperature = findViewById(R.id.txt_temperature);
        txtToday = findViewById(R.id.txt_today);
        txtWeatherStatus = findViewById(R.id.txt_weather_status);

        btnNext7Days = findViewById(R.id.btn_next_7days);

        txtFeelsLike = findViewById(R.id.txt_feels_like);
        txtWind = findViewById(R.id.txt_wind);
        txtPressure = findViewById(R.id.txt_pressure);
        txtHumidity = findViewById(R.id.txt_humidity);
        txtClouds = findViewById(R.id.txt_clouds);
        txtVisibility = findViewById(R.id.txt_visibility);
        txtSunrise = findViewById(R.id.txt_sunrise);
        txtSunset = findViewById(R.id.txt_sunset);
        txtLastTime = findViewById(R.id.txt_last_time);

        rvHourly = findViewById(R.id.rv_hourly);

        searchText = "";
        currentCity = getString(R.string.defaultCity);

    }


    //cac ham k can, can ghi lai de tham khao
    //    Showing Soft Keyboard
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    //    Hiding Soft Keyboard
    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
//            // Usage Showing/Hiding Soft Keyboard
//            EditText editText = (EditText) findViewById(R.id.editText);
//            // hiding the soft keyboard
//            hideSoftKeyboard(editText);
//            // showing the soft keyboard
//            showSoftKeyboard(editText);


}