package com.example.shakedetectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView xTextView,yTextView,zTextView;
    private SensorManager sensorManager;
    private Sensor accelometerSensor;
    private Boolean isAvailable,itIsNotFirstTime=false;
    private float currentX,currentY,currentZ,lastX,lastY,lastZ;
    private float xDifference,yDifference,zDifference;
    private float shakeThresHold=5f;
    private CameraManager cameraManager;
    private String cameraId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        xTextView = findViewById(R.id.xTextView);
//        yTextView = findViewById(R.id.yTextView);
//        zTextView = findViewById(R.id.zTextView);
        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
        {
            accelometerSensor= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAvailable = true;

        }else{
            xTextView.setText("Accelometer isnot Avilabe on this device");
            isAvailable=false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        xTextView.setText(event.values[0]+"m/s2");
//        yTextView.setText(event.values[1]+"m/s2");
//        zTextView.setText(event.values[2]+"m/s2");
        currentX=event.values[0];
        currentY=event.values[1];
        currentZ = event.values[2];

        if (itIsNotFirstTime)
        {
            xDifference = Math.abs(lastX-currentX);
            yDifference = Math.abs(lastY-currentY);
            zDifference = Math.abs(lastZ-currentZ);
            if ((xDifference> shakeThresHold&&yDifference>yDifference)
                    ||(xDifference>shakeThresHold &&zDifference>shakeThresHold)||(yDifference>shakeThresHold
                         && zDifference>shakeThresHold))
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        cameraManager.setTorchMode(cameraId,true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }


            }

        }

        lastX=currentX;
        lastY=currentY;
        lastZ=currentZ;
        itIsNotFirstTime=true;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAvailable){
            sensorManager.registerListener(this,accelometerSensor,SensorManager.SENSOR_DELAY_NORMAL);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isAvailable){
            sensorManager.unregisterListener(this);
        }
    }
    public  void offButton (View view){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId,false);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }
}