package computing.mobile.ambulance.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Gaurav on 19-09-2016.
 */

public class Constant {
//    public static String url = "http://ec2-35-154-68-218.ap-south-1.compute.amazonaws.com:8000";
    public static String url = "http://10.196.23.228:5000";


    static public boolean CheckConnectivity(Context context){

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
