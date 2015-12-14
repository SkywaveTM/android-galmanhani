package me.skywave.helloapplication.background;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import me.skywave.helloapplication.utils.NotificationUtils;
import me.skywave.helloapplication.utils.Utils;
import me.skywave.helloapplication.utils.local_storage.HostInformation;
import me.skywave.helloapplication.utils.local_storage.HostStorage;
import me.skywave.helloapplication.utils.server_api.ServerApi;

public class BackgroundUtils {
    private final static NetworkScan scan = new NetworkScan();

    public static void scanBackground(Context context, NetworkScan.NetworkScanCallback callback) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Utils.getIp(wifiManager);

        NotificationUtils.debugNotification(context, "Scan started.");
        scan.scanNetwork(ip, callback);
    }

    public static void scanAndSend(final Context context, final int density) {
        final String hostId = Utils.getHostId(context);
        HostStorage storage = new HostStorage(context);
        HostInformation information = storage.getHost(hostId);

        if (information == null || !information.isAllowed()) {
            return;
        }

        NetworkScan.NetworkScanCallback callback = new NetworkScan.NetworkScanCallback() {
            @Override
            public void runAfterDone(int result) {
                ServerApi api = new ServerApi();
                if (density <= 0) {
                    api.addHostConnectionData(hostId, result);
                } else {
                    api.addHostDensityData(hostId, result, density);
                }

                NotificationUtils.debugNotification(context, "Data sent. " + result + " / " + density);
            }
        };

        scanBackground(context, callback);
    }

    public static void abortScan() {
        scan.abortCurrentThread();
    }
}
