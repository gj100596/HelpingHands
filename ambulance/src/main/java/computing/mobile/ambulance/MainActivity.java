package computing.mobile.ambulance;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import org.json.JSONException;
import org.json.JSONObject;
import computing.mobile.ambulance.util.Constant;
import computing.mobile.ambulance.util.ServerRequest;

import static computing.mobile.ambulance.R.id.btnsendgps;
import static computing.mobile.ambulance.R.id.submenuarrow;

public class MainActivity extends AppCompatActivity {
    public static Context ambulance_context;
    Button sendLocation;
    public static TextView textview;



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


        ambulance_context = MainActivity.this;
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }

        else{
            Toast.makeText(MainActivity.this
            ,"Permission OK",Toast.LENGTH_SHORT).show();
        }

        textview = (TextView)findViewById(R.id.textViewgps);
        Intent gpsintent = new Intent(MainActivity.this,GPS.class);
        startService(gpsintent);
        textview.setText("Location of Ambulance is :"+"Loading ....");


        sendLocation =(Button)findViewById(R.id.btnsendgps);
        sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendGPStoServer(GPS.lastLocationValue);

            }
        });
    }


    public void onDestroy() {
        stopService(new Intent(MainActivity.this, GPS.class));
        super.onDestroy();
    }

    public void onPause(){
        super.onPause();
        stopService(new Intent(MainActivity.this, GPS.class));
    }

    public void onResume(){
        super.onResume();
        startService(new Intent(MainActivity.this, GPS.class));
    }
}
