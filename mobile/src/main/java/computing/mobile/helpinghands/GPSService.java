package computing.mobile.helpinghands;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import computing.mobile.helpinghands.util.Constant;
import computing.mobile.helpinghands.util.ServerRequest;

public class GPSService extends Service{
    LocationManager locationManagerNetwork, locationManagerGPS;
    LocationListener locationListener;
    String lastLocationValue = "null";
    private Handler displayThread;
    SensorManager sensorManager;
    Sensor sensorAccelerometer;
    SensorEventListener sensorEventListener;
    Context context;
    boolean start = true;
    float xLast, yLast;
    public static final String LOG_TAG = GPSService.class.getCanonicalName();


    public GPSService() {
        locationManagerNetwork = (LocationManager) MainActivity.thisAct.getSystemService(Context.LOCATION_SERVICE);
        locationManagerGPS = (LocationManager) MainActivity.thisAct.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Sensor sensor = sensorEvent.sensor;

                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                    Log.e(LOG_TAG, "GX="+String.valueOf(sensorEvent.values[0])+"\nGY="+String.valueOf(sensorEvent.values[1])+"\nGZ="+String.valueOf(sensorEvent.values[2]));
                    float xCurrent = sensorEvent.values[0]; // Get current x
                    float yCurrent = sensorEvent.values[1]; // Get current y
                    if(start){
                        // Initialize last x and y
                        xLast = xCurrent;
                        yLast = yCurrent;
                        start = false;
                    }
                    else{
                        // Calculate variation between last x and current x, last y and current y
                        float xDelta=xLast-xCurrent;
                        float yDelta=yLast-yCurrent;
                        if(Math.sqrt(xDelta*xDelta/2)>5) {
                            Log.d(LOG_TAG,"The device is moved horizontally.");
                        }
                        if(Math.sqrt(yDelta*yDelta/2)>5) {
                            Log.d(LOG_TAG,"The device is moved vertically.");
                        }

                        // Update last x and y
                        xLast = xCurrent;
                        yLast = yCurrent;
                    }

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sensorManager.registerListener(sensorEventListener, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                lastLocationValue = "" + location.getLatitude() + "," + location.getLongitude();
                String time = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
                Log.e("Location", "Time: " + time + " GPS:" + lastLocationValue);

                sendGPStoServer(lastLocationValue);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        try {
            locationManagerNetwork.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListener);
        } catch (SecurityException ex) {
            Log.e("Error", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e("Error", "gps provider does not exist " + ex.getMessage());
        }
        try {
            locationManagerGPS.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0,
                    locationListener);
        } catch (SecurityException ex) {
            Log.e("Error", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e("Error", "gps provider does not exist " + ex.getMessage());
        }

/*
Use if we want to send GPS Location per second.
        displayThread = new Handler();
        displayThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                String time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                String value = time + "," + lastLocationValue;
                Log.e("Readings", time + "," + value);
                sendGPStoServer(lastLocationValue);
                displayThread.postDelayed(this, 1000);


            }
        }, 1000);
*/
    }

    private void sendGPStoServer(String lastLocationValue) {
        String url = Constant.url + "/driver/gps";

        JSONObject param = new JSONObject();
        try {
            SharedPreferences info = getSharedPreferences(getString(R.string.user_data_shared_pref),MODE_PRIVATE);
            param.put("userID", info.getString(getString(R.string.phone_user_shared_pref),null));
            param.put("GPS", lastLocationValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postGPS = new JsonObjectRequest(Request.Method.POST, url, param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        ServerRequest.getInstance(GPSService.this).getRequestQueue().add(postGPS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManagerNetwork.removeUpdates(locationListener);
        locationManagerGPS.removeUpdates(locationListener);
//        displayThread.removeCallbacksAndMessages(null);

        sensorManager.unregisterListener(sensorEventListener);

    }

}
