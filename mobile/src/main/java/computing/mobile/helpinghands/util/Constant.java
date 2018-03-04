package computing.mobile.helpinghands.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Constant {
    public static String url = "http://ec2-35-154-68-218.ap-south-1.compute.amazonaws.com:8000";

    static public boolean CheckConnectivity(Context context){

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
