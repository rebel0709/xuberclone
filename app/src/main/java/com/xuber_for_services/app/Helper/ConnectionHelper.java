package com.xuber_for_services.app.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ConnectionHelper {

    private Context context;

    public ConnectionHelper(Context context) {
        this.context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) ||
                    (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
        } else {
            return false;
        }

    }
}
