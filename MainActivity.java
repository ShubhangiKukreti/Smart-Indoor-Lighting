package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    private SensorManager sensorManager;
    Sensor mLight;
    String sunrise_time;
    String sunset_time;
    float sensorValue;

    private TextView mTextViewResult;
    private RequestQueue mQueue;

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = mDatabase.getReference();

    TextView  light;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        light = (TextView) findViewById(R.id.light);
        mTextViewResult = findViewById(R.id.text_view_result);

        Button buttonParse = findViewById(R.id.button_parse);

        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mQueue = Volley.newRequestQueue(this);


        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse();
            }
        });

        if (mLight != null) {
            sensorManager.registerListener(MainActivity.this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered light listener");
        } else {
            light.setText("Light not supported");
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        //DatabaseReference newProductRef = mDatabase.push()
        sensorValue = sensorEvent.values[0];
        Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0]);
        light.setText("Light: " + sensorEvent.values[0]);
        mDatabaseReference = mDatabase.getReference().child("Light Intensity from Sensor");
        mDatabaseReference.push().setValue(sensorEvent.values[0]);
    }

    private void jsonParse() {

        String url = "https://api.sunrise-sunset.org/json?lat=53.354883&lng=-6.255197&date=today";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = response.getJSONObject("results");

                            sunrise_time = result.getString("sunrise");
                            sunset_time = result.getString("sunset");

                            mTextViewResult.append(sunrise_time + ", " + sunset_time + "\n\n");
                            Date currentTime = Calendar.getInstance().getTime();
                            int hours = currentTime.getHours();
                            int minutes = currentTime.getMinutes();
                            int seconds = currentTime.getSeconds();
                            int timee = seconds + minutes * 60 + hours * 3600;
                            boolean isPM = false;
                            if(sunset_time.contains("PM"))
                                isPM = true;
                            sunset_time = sunset_time.replace("AM", "");
                            sunset_time = sunset_time.replace("PM", "");
                            sunset_time = sunset_time.replace(" ", "");
                            String[] sunsetArray = sunset_time.split(":");
//                            Log.d("TAG: ", sunset_time);
                            int sunsetHours = Integer.parseInt(sunsetArray[0]);
                            int sunsetMinutes = Integer.parseInt(sunsetArray[1]);
                            int sunsetSeconds = Integer.parseInt(sunsetArray[2]);
                            int timee2 = sunsetSeconds + sunsetMinutes * 60 + sunsetHours * 3600;
                            if(isPM)
                                timee2 += 43200;
                            WindowManager.LayoutParams params = MainActivity.this.getWindow().getAttributes();
                            if (timee < timee2) {
                                Log.d("TAG", currentTime + ", "+sunset_time);
                                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                                if (sensorValue < 300)
                                {
                                    Toast.makeText(MainActivity.this, "not enough light in the room!", Toast.LENGTH_SHORT).show();
                                    params.screenBrightness = 1.0f;
                                    getWindow().setAttributes(params);
                                }
                                else if (sensorValue > 1000)
                                {
                                    Toast.makeText(MainActivity.this, "not enough light in the room!", Toast.LENGTH_SHORT).show();
                                    params.screenBrightness = -1.0f;
                                    getWindow().setAttributes(params);
                                }

                            }
                            else
                            {
                                Log.d("TAG", currentTime + ", "+sunset_time);
                                Toast.makeText(MainActivity.this, "The sun has set", Toast.LENGTH_SHORT).show();
                                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                                params.screenBrightness = 1.0f;
                                getWindow().setAttributes(params);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);

    }

}

