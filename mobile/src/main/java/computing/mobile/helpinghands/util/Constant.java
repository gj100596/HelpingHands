package computing.mobile.helpinghands.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Constant {
    //public static String url = "http://ec2-18-188-6-200.us-east-2.compute.amazonaws.com:5000";
    public static String url = "http://10.16.23.129:5000";

    static public boolean CheckConnectivity(Context context){

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
