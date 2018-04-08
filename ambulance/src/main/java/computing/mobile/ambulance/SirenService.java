package computing.mobile.ambulance;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SirenService extends Service {
    public SirenService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
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
}
