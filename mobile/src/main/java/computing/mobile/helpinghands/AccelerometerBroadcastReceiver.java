package computing.mobile.helpinghands;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by GJ on 07-04-2018.
 */

public class AccelerometerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, AccelerometerService.class));
    }
}
