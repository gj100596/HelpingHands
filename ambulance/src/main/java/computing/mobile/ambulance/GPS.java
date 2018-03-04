package computing.mobile.ambulance;

import android.app.Service;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

public class GPS extends Service {

    LocationManager network;
    LocationListener listener;
    public GPS() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
