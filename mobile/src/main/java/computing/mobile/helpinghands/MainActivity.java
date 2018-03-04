package computing.mobile.helpinghands;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static Context thisAct;
    private Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisAct = MainActivity.this;

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start.getText().toString().equalsIgnoreCase(getString(R.string.ride_start))){
                    start.setText(getString(R.string.ride_stop));
                    Intent serviceIntent = new Intent(MainActivity.this, GPSService.class);
                    startService(serviceIntent);
                }
                else{
                    start.setText(getString(R.string.ride_start));
                    stopService(new Intent(MainActivity.this, GPSService.class));
                }
            }
        });
    }
}
