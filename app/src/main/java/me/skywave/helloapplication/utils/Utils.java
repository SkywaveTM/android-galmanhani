package me.skywave.helloapplication.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static String NO_HOST_MAC = "00:00:00:00:00:00";

    // http://stackoverflow.com/questions/3103652/hash-string-via-sha-256-in-java
    public static String encrypt(String string) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ignore) {
            return null;
        }

        md.update(string.getBytes());

        return String.format("%064x", new java.math.BigInteger(1, md.digest()));
    }


    public static String getIp(WifiManager manager) {
        return Formatter.formatIpAddress(manager.getConnectionInfo().getIpAddress());
    }

    // http://stackoverflow.com/questions/6063889/can-i-find-the-mac-address-of-my-access-point-in-android
    public static String getHostId(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager == null) {
            return null;
        }

        String hostMac = manager.getConnectionInfo().getBSSID();
        if (hostMac == null) {
            hostMac = NO_HOST_MAC;
        }

//        Log.d("scan", hostMac);
        return encrypt(hostMac);
    }

    public static boolean isNoHost(String hostid) {
        //noinspection ConstantConditions
        return encrypt(NO_HOST_MAC).equals(hostid);
    }
}
