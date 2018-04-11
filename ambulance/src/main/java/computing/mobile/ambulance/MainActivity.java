package computing.mobile.ambulance;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import computing.mobile.ambulance.util.Constant;
import computing.mobile.ambulance.util.ServerRequest;

//import static computing.mobile.ambulance.R.id.btnsendgps;
//import static computing.mobile.ambulance.R.id.submenuarrow;

public class MainActivity extends AppCompatActivity {
    public static Context ambulance_context;
    Button sendLocation;
    public static TextView textview;
    public static final String TAG = MainActivity.class.getCanonicalName();
    private GoogleMap mGoogleMap;
    private Handler updateMarkerThread;

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        mGoogleMap = googleMap;
//
//        try {
//            // Customise the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            boolean success = mGoogleMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                            this, R.raw.style_json));
//
//            if (!success) {
//                Log.e(TAG, "Style parsing failed.");
//            }
//        } catch (Resources.NotFoundException e) {
//            Log.e(TAG, "Can't find style. Error: ", e);
//        }
////
////        Log.d(TAG, "HERE");
////
////        // Add a marker in Sydney, Australia,
////        // and move the map's camera to the same location.
////        LatLng sydney = new LatLng(-33.852, 151.211);
////        mGoogleMap.addMarker(new MarkerOptions().position(sydney)
////                .title("Marker in Sydney"));
//////        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
////        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));
//
//        final Handler firstLocationUpdateHandler = new Handler();
//        firstLocationUpdateHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                String[] coordinates = GPS.lastLocationValue.split(",");
//                LatLng currentLocation = new LatLng(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]));
//                mGoogleMap.addMarker(new MarkerOptions().position(currentLocation));
//                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
//            }
//        }, 3000);
////        updateMarkerThread = new Handler();
////        updateMarkerThread.postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                LatLng mumbai = new LatLng(19.1405176,72.9165265);
////                mGoogleMap.addMarker(new MarkerOptions().position(mumbai)
////                        .title("Marker in Mumbai"));
////                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mumbai));
////                updateMarkerThread.postDelayed(this, 5000);
////
////
////            }
////        }, 5000);
//
//    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ambulance_context = MainActivity.this;
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
        ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)

        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO}
                    , 1);

        }

        else{
            Toast.makeText(MainActivity.this,"Permission OK",Toast.LENGTH_SHORT).show();
        }

        SirenService service = new SirenService();
        Intent intent = new Intent(MainActivity.this, service.getClass());
        if (!isMyServiceRunning(service.getClass())) {
            startService(intent);
        }

        // Retrieve the content view that renders the map.
//        setContentView(R.layout.activity_main);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }




//    public void onDestroy() {
//        stopService(new Intent(MainActivity.this, GPS.class));
//        super.onDestroy();
//    }
//
//    public void onPause(){
//        super.onPause();
//        stopService(new Intent(MainActivity.this, GPS.class));
//    }
//
//    public void onResume(){
//        super.onResume();
//        startService(new Intent(MainActivity.this, GPS.class));
//    }


}
