package me.skywave.helloapplication.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;

import me.skywave.helloapplication.front.dialog.HostFeedbackDialog;
import me.skywave.helloapplication.front.dialog.HostEditDialog;
import me.skywave.helloapplication.utils.local_storage.HostInformation;
import me.skywave.helloapplication.utils.local_storage.HostStorage;

public class NotificationUtils {
    public static int SCAN_NOTI_ID = 91452;
    public static int NEW_NOTI_ID = 19725;
    public static int DEBUG_NOTI_ID = 83125;

    public static void sendFeedbackNotification(final Context context) {
        HostStorage hostStorage = new HostStorage(context);

        final String hostId = Utils.getHostId(context);
        final HostInformation hostInformation = hostStorage.getHost(hostId);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Noti
        Intent hostDensityIntent = new Intent(context, HostFeedbackDialog.class);
        hostDensityIntent.putExtra("host_id", hostId);
        hostDensityIntent.putExtra("noti_id", SCAN_NOTI_ID);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                hostDensityIntent,
                PendingIntent.FLAG_ONE_SHOT
        );

        Notification noti = new NotificationCompat.Builder(context)
                .setContentTitle(hostInformation.getName() + " connected.")
                .setContentText("Touch to send feedback")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(SCAN_NOTI_ID, noti);

    }

    public static void sendNewHostNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        Intent addHostIntent = new Intent(context, HostEditDialog.class);
        addHostIntent.putExtra("do_scan", true);
        addHostIntent.putExtra("noti_id", NEW_NOTI_ID);


        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                addHostIntent,
                PendingIntent.FLAG_ONE_SHOT
        );

        Notification noti = new NotificationCompat.Builder(context)
                .setContentText("Touch to allow or block the host")
                .setContentTitle(String.format("New host found - %s", networkInfo.getExtraInfo()))
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(pendingIntent)
                .build();


        notificationManager.notify(NEW_NOTI_ID, noti);
    }


    public static void debugNotification(Context context, String message) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification noti = new NotificationCompat.Builder(context)
                .setContentText(message)
                .setContentTitle("DEBUG")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .build();


        notificationManager.notify(DEBUG_NOTI_ID, noti);
    }

    public static void cancelAllNotifications(Context context) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(NEW_NOTI_ID);
        notificationManager.cancel(SCAN_NOTI_ID);
    }

}
