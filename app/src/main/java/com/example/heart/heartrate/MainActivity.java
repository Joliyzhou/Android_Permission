package com.example.heart.heartrate;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Heart_Rate";
    private boolean granted;
    private TextView HeartRate;
    StringBuilder mBuilder;
    private SensorManager sensorManager;
    private Sensor HeartSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();

        HeartRate = (TextView) findViewById(R.id.Heart_Rate);
        mBuilder = new StringBuilder();
    }

    private void initPermission() {
        int permission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BODY_SENSORS}, 1);
                Log.e(TAG, "Request BODY_SENSORS Permission");
            }
        } else {
            Log.e(TAG, "BODY_SENSORS Permission is rqeuseted");
            RequestSensor();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "BODY_SENSORS Permission result");
        if (requestCode == 1 && grantResults.length > 0) {
            granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;//是否授权，可以根据permission作为标记
            Log.e(TAG, "XXXBODY_SENSORS Permission: " + granted);
            if (granted) {
                RequestSensor();
            } else {
                if (mBuilder.length() > 1) {
                    mBuilder.delete(0, mBuilder.length() - 1);
                }
                mBuilder.append("HRM: No Permission");
                HeartRate.setText(mBuilder.toString());
                return;
            }
        }
    }

    public void RequestSensor() {
        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        HeartSensor=sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (HeartSensor == null) {
            Log.e(TAG,"get heart rate sensor fail");
            if (mBuilder.length() > 1) {
                mBuilder.delete(0, mBuilder.length() - 1);
            }
            mBuilder.append("HRM:Get Sensor Fail");
            HeartRate.setText(mBuilder.toString());
        } else {
            Log.e(TAG,"get heart rate sensor success  ");
        }
        sensorManager.registerListener(this, HeartSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    public void onSensorChanged(SensorEvent event) {
        if (mBuilder.length() > 1) {
            mBuilder.delete(0, mBuilder.length() - 1);
        }
        mBuilder.append("Heart Rate:");
        mBuilder.append(event.values[0]);
        HeartRate.setText(mBuilder.toString());
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
