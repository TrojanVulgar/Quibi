package com.easyplex.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import androidx.annotation.RequiresApi;


public class NetworkUtils {


    private NetworkUtils(){


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
  public static boolean isWifiConnected(Context context) {

    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

   if (manager == null) return true;

   Network network = manager.getActiveNetwork();

    NetworkCapabilities capabilities = manager.getNetworkCapabilities(network);

        assert capabilities != null;
        return !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
  }



}
