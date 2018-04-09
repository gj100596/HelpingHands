package computing.mobile.ambulance;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import computing.mobile.ambulance.util.Constant;
import computing.mobile.ambulance.util.ServerRequest;

public class SirenService extends Service {
    private MediaRecorder mRecorder;
    private boolean recording = false;
    Handler displayThread;
    String filePath;

    public SirenService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e("ServiceRecording","Running");
        filePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/test.3gp";
        displayThread = new Handler();
        displayThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recording) {
                    recording = false;
//                    Log.e("R","Stop");
                    stopRecording();
                    ifAmbulance();
                    displayThread.postDelayed(this, 1);
                }
                else {
                    recording = true;
//                    Log.e("R","Start");
                    startRecording();
                    displayThread.postDelayed(this, 10000);
                }

            }
        }, 10000);
        return START_STICKY;
    }

    private void ifAmbulance() {
        Log.e("ServiceRecording","IsAmbulance?");

        String url = /*Constant.url + */"http://10.130.4.192:8000/isambulance";


        JSONObject param = new JSONObject();
        try {
            File file = new File(filePath);
            FileInputStream in=new FileInputStream(filePath);
            byte fileContent[] = new byte[(int)file.length()];

            in.read(fileContent,0,fileContent.length);

            String encoded = Base64.encodeToString(fileContent, 0);
            param.put("audio",encoded);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postGPS = new JsonObjectRequest(Request.Method.POST, url, param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("result").equalsIgnoreCase("yes"))
                                sendGPStoServer(GPS.lastLocationValue);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        ServerRequest.getInstance(SirenService.this).getRequestQueue().add(postGPS);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent("computing.mobile.ambulance.siren");
        sendBroadcast(broadcastIntent);
    }


    private void startRecording() {
        Log.e("ServiceRecording","Start");

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        mRecorder.setOutputFile(filePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("EEErrrrr", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        Log.e("ServiceRecording","Stopped");

        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void sendGPStoServer(String lastLocationValue) {
        Log.e("Sending","GPS");
        String url = Constant.url + "/ambulance/gps";

        JSONObject param = new JSONObject();
        try {
            param.put("userID", "");
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

        ServerRequest.getInstance(SirenService.this).getRequestQueue().add(dpUpload);
    }
}
