package computing.mobile.helpinghands;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import computing.mobile.helpinghands.gcm.RegistrationIntentService;
import computing.mobile.helpinghands.util.Constant;
import computing.mobile.helpinghands.util.ServerRequest;

public class MainActivity extends AppCompatActivity {

    public static Context thisAct;
    private Button approach1,approach2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisAct = MainActivity.this;

        if (
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        ||
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED
                        ||
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                                != PackageManager.PERMISSION_GRANTED

                ) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.RECORD_AUDIO
                    },
                    1);
        }


        final SharedPreferences info = getSharedPreferences(getString(R.string.user_data_shared_pref),MODE_PRIVATE);
        if(info.getString(getString(R.string.phone_user_shared_pref),null)==null){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Introduce Yourself!");
            builder.setMessage("Please provide your 10 digit mobile number.");

            final EditText phoneNumber = new EditText(builder.getContext());
            phoneNumber.setHint("Phone Number");
            phoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            //spinner.setLayoutParams(params);
            phoneNumber.setLayoutParams(params);
            // builder.setView(spinner);
            builder.setView(phoneNumber);

            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(phoneNumber.getText().toString().length()!=10){
                        Toast.makeText(MainActivity.this,"Enter Valid Mobile Number",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        SharedPreferences.Editor editor = info.edit();
                        editor.putString(getString(R.string.phone_user_shared_pref), phoneNumber.getText().toString());
                        editor.apply();

                        Intent gcmToken = new Intent(MainActivity.thisAct, RegistrationIntentService.class);
                        startService(gcmToken);
                    }
                }
            });
            builder.setCancelable(false);
            builder.show();
        }

        checkIfTokenSent();

        approach1 = findViewById(R.id.approach1);
        approach2 = findViewById(R.id.approach2);

        approach1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent serviceIntent = new Intent(MainActivity.this, GPSService.class);
                    startService(serviceIntent);
                    Log.d(this.getClass().getName(), "Approach 1 Started");
            }
        });

        approach2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(MainActivity.this, AccelerometerService.class);
                startService(serviceIntent);
                Log.d(this.getClass().getName(), "Approach 2 Started");
            }
        });


    }

    private void checkIfTokenSent() {
        SharedPreferences info = getSharedPreferences(getString(R.string.user_data_shared_pref),MODE_PRIVATE);
        if(info.getString(getString(R.string.iid_user_shared_pred),null)==null){
            Intent gcmToken = new Intent(MainActivity.thisAct, RegistrationIntentService.class);
            startService(gcmToken);
        }

        if(!info.getBoolean(getString(R.string.token_user_shared_pref),false)){
            String url = Constant.url + "/driver/updateIid";

            JSONObject param = new JSONObject();
            try {
                param.put("userID", info.getString(getString(R.string.phone_user_shared_pref),null));
                param.put("IID",info.getString(getString(R.string.iid_user_shared_pred),null));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("IID Request",param.toString());
            JsonObjectRequest postIID = new JsonObjectRequest(Request.Method.POST, url, param,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            SharedPreferences info = getSharedPreferences(getString(R.string.user_data_shared_pref),MODE_PRIVATE);
                            SharedPreferences.Editor editor = info.edit();
                            editor.putBoolean(getString(R.string.token_user_shared_pref),true);
                            editor.apply();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );

            ServerRequest.getInstance(this).getRequestQueue().add(postIID);
        }
    }
}
