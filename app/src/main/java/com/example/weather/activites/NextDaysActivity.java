package com.example.weather.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weather.adapters.DaysAdapter;
import com.example.weather.models.Days;
import com.example.weather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NextDaysActivity extends AppCompatActivity {

    String apiid;
    String local;

    TextView txtCityName;
    String city;

    RecyclerView rvNextDays;
    DaysAdapter daysAdapter;
    ArrayList<Days> daysArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_days);


        Intent intent = getIntent();
        city = intent.getStringExtra("city");
//        Log.d("ketqua city fr Intent", city);

        mapping();
        setCityName();
        daysArrayList.clear();
        requestNextDays(city);

    }

    @Override
    protected void onStart() {
        super.onStart();

        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_from_right);
        rvNextDays.setLayoutAnimation(animationController);
        rvNextDays.getAdapter().notifyDataSetChanged();
        rvNextDays.scheduleLayoutAnimation();
    }

    private void requestNextDays(String cityName) {
        RequestQueue requestQueue = Volley.newRequestQueue(NextDaysActivity.this);
        String url = "";
        if (local.equals("vi")) {
            url = String.format("https://api.openweathermap.org/data/2.5/forecast/daily?q=%s&units=metric&cnt=8&lang=vi&appid=%s", cityName, apiid);
        } else {
            url = String.format("https://api.openweathermap.org/data/2.5/forecast/daily?q=%s&units=metric&cnt=8&appid=%s", cityName, apiid);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.d("ketqua response 7Days", response);
                        dataProcessingNextDays(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);

    }

    private void dataProcessingNextDays(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArrayDays = jsonObject.getJSONArray("list");
            for (int i = 1; i < 8; i++) {
                JSONObject object_i = jsonArrayDays.getJSONObject(i);

                //Date
                String date_geted = object_i.getString("dt");
                long l = Long.parseLong(date_geted);
                Date date_empty = new Date(l * 1000L);
                SimpleDateFormat dateFormatTomorrow = new SimpleDateFormat(", dd/MM");
                SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd/MM/yyyy");
                String dateTomorrow = getString(R.string.str_tomorrow) + dateFormatTomorrow.format(date_empty);
                String date = dateFormat.format(date_empty);

                //maxTemp, minTemp
                JSONObject jsonObjectTemp = object_i.getJSONObject("temp");
                String max_geted = jsonObjectTemp.getString("max");
                String min_geted = jsonObjectTemp.getString("min");
                double d_max = Double.parseDouble(max_geted);
                double d_min = Double.parseDouble(min_geted);
                String maxTemp = String.valueOf((int) d_max) + "°C";
                String minTemp = String.valueOf((int) d_min) + "°C";

                //Status + Image(icon)
                JSONArray jsonArrayWeather = object_i.getJSONArray("weather");
                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                String status = jsonObjectWeather.getString("description");
                status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase(); //UpperCase s0
                String image = jsonObjectWeather.getString("icon");

//                Log.d("ketqua dateTomorrow", dateTomorrow);
//                Log.d("ketqua date", date);
//                Log.d("ketqua maxTemp", maxTemp);
//                Log.d("ketqua minTemp", minTemp);
//                Log.d("ketqua status", status);
//                Log.d("ketqua image", image);

                if (i != 1) {
                    daysArrayList.add(new Days(date, status, MainActivity.changeIcon(image), maxTemp, minTemp));
                } else {
                    daysArrayList.add(new Days(dateTomorrow, status, MainActivity.changeIcon(image), maxTemp, minTemp));
                }
            }
            daysAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {

        //already set in xml file
        RecyclerView.LayoutManager layoutManager
                = new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false);
        rvNextDays.setLayoutManager(layoutManager);

        rvNextDays.setHasFixedSize(true);
        daysAdapter = new DaysAdapter(NextDaysActivity.this, daysArrayList);
//        //test data in RecyclerView
//        daysArrayList.add(new Days("Wed, 08/07/2020", "Clear sky", R.drawable.ic_01d_clear_sky,"34°C","30°C"));
        rvNextDays.setAdapter(daysAdapter);

//        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_from_right);
//        rvNextDays.setLayoutAnimation(animationController);
//        rvNextDays.getAdapter().notifyDataSetChanged();
//        rvNextDays.scheduleLayoutAnimation();
    }


    private void setCityName() {
        txtCityName.setText(city);
    }


    //back to MainActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                daysArrayList.clear();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mapping() {
        local = Locale.getDefault().getLanguage();
        apiid = "53fbf527d52d4d773e828243b90c1f8e";

        rvNextDays = findViewById(R.id.rv_next_days);
        txtCityName = findViewById(R.id.txt_city_name);
    }
}