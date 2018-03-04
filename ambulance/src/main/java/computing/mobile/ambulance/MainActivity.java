package computing.mobile.ambulance;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import computing.mobile.ambulance.util.Constant;
import computing.mobile.ambulance.util.ServerRequest;

import static computing.mobile.ambulance.R.id.btnsendgps;

public class MainActivity extends AppCompatActivity {

    Button sendLocation;
    String latitude;
    String longitute;
    String lastLocationValue = "null";
    LocationManager locationManager;
    LocationListener locationListener;


    private void sendGPStoServer(String lastLocationValue) {
        String url = Constant.url + "/ambulance/gps";

        JSONObject param = new JSONObject();
        try {
            param.put("UserID", "");
            param.put("GPS", lastLocationValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest dpUpload = new JsonObjectRequest(Request.Method.POST, url, param,
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

        ServerRequest.getInstance(MainActivity.this).getRequestQueue().add(dpUpload);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendLocation =(Button)findViewById(R.id.btnsendgps);
        sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager)MainActivity.this.getSystemService(Context.LOCATION_SERVICE);


                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        latitude = Double.toString(location.getLatitude());
                        longitute = Double.toString(location.getLongitude());
                        lastLocationValue = "" + latitude + "," + longitute;
                        TextView textview = (TextView)findViewById(R.id.textViewgps);
                        textview.setText("Location of Ambulance is :"+latitude+" and "+
                        longitute);
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

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                sendGPStoServer(lastLocationValue);

            }
        });
    }


    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
//        locationListener.removeUpdates(locationListener);
//        displayThread.removeCallbacksAndMessages(null);

    }
}
