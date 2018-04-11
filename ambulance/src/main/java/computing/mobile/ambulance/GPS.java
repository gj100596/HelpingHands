package computing.mobile.ambulance;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import computing.mobile.ambulance.util.Constant;
import computing.mobile.ambulance.util.ServerRequest;
import computing.mobile.ambulance.MainActivity;

public class GPS extends Service {

    LocationManager locationManagerNetwork, locationManagerGPS;
    LocationListener locationListener;
    public static String lastLocationValue = "null";
    private Handler displayThread;



    public GPS() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        locationManagerNetwork = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManagerGPS = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                lastLocationValue = "" + location.getLatitude() + "," + location.getLongitude();
                String time = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
//                Log.e("Location", "Time: " + time + " GPS:" + lastLocationValue);
//                MainActivity.textview.setText("Location of Ambulance is :"+GPS.lastLocationValue);
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
}
