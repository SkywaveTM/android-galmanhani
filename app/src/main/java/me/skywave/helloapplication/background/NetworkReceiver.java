package me.skywave.helloapplication.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import me.skywave.helloapplication.utils.NotificationUtils;
import me.skywave.helloapplication.utils.Utils;
import me.skywave.helloapplication.utils.local_storage.HostInformation;
import me.skywave.helloapplication.utils.local_storage.HostStorage;

public class NetworkReceiver extends BroadcastReceiver {
    private static String lastHostId = null;

    public NetworkReceiver() {
        super();
    }

    // http://stackoverflow.com/questions/5276032/connectivity-action-intent-received-twice-when-wifi-connected
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager conMngr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo wifi = conMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

//            Log.d("scan", wifi.getDetailedState().toString());

            switch (wifi.getDetailedState()) {
                case DISCONNECTED:
                    NotificationUtils.cancelAllNotifications(context);
                    BackgroundUtils.abortScan();
                    break;

                case CONNECTED:
                    // connection is not completely established. so wait a little.
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    doConnectedAction(context);
                    break;

            }
        }
    }

    private void doConnectedAction(Context context) {
        HostStorage hostStorage = new HostStorage(context);
        String hostId = Utils.getHostId(context);

        HostInformation hostInformation = hostStorage.getHost(hostId);

        if (hostId != null) {
            if (hostInformation != null && hostInformation.isAllowed()) {
                BackgroundUtils.scanAndSend(context, 0);
            }

            if (!hostId.equals(lastHostId)) {
                lastHostId = hostId;

                if (hostInformation == null) {
                    NotificationUtils.sendNewHostNotification(context);
                } else if (hostInformation.isAllowed()) {
                    NotificationUtils.sendFeedbackNotification(context);
                }
            }
        }
    }


}
