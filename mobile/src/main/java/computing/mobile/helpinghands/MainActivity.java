package computing.mobile.helpinghands;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static Context thisAct;
    private Button mStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisAct = MainActivity.this;

        mStartButton = findViewById(R.id.start);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStartButton.getText().toString().equalsIgnoreCase(getString(R.string.ride_start))){
                    mStartButton.setText(getString(R.string.ride_stop));
                    Intent serviceIntent = new Intent(MainActivity.this, GPSService.class);
                    startService(serviceIntent);
                }
                else{
                    mStartButton.setText(getString(R.string.ride_start));
                    stopService(new Intent(MainActivity.this, GPSService.class));
                }
            }
        });
    }
}
