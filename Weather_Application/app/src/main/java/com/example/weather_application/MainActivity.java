package com.example.weather_application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {
    Button next;
    AutoCompleteTextView datainput;
    ListView list;
    ArrayAdapter<String> adapter;
    List<String> weatherList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        next=findViewById(R.id.next);
        datainput=findViewById(R.id.datainput);
        list=findViewById(R.id.lv_weatherReports);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, weatherList);
        list.setAdapter(adapter);

        String[] cities = {"London", "Paris", "New York", "Tokyo", "Delhi"};
        AutoCompleteTextView datainput = findViewById(R.id.datainput);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                cities
        );

        datainput.setAdapter(adapter);

        //click listeners for each btn
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = datainput.getText().toString();
                fetchWeather(city);
            }
        });

    }
    private void fetchWeather(String city){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService weatherService=retrofit.create(WeatherService.class);
        Call<WeatherResponse> call=weatherService.getWeatherByCity(city,"add017c3b46a9ddd4c4cfaee6b6e172c","metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    String condition = weather.weather.get(0).description;
                    Toast.makeText(MainActivity.this, weather.weather.get(0).description, Toast.LENGTH_SHORT).show();
                    String result = "City: " + weather.name + "\nTemp: " + weather.main.temp + "°C"+"\nHumidity:"+weather.main.hum+"%"+"\nCondition: " + condition;

                    weatherList.clear();
                    weatherList.add(result);
                    adapter.notifyDataSetChanged();}
                else {
                    Toast.makeText(MainActivity.this, "City not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}