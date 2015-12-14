package me.skywave.helloapplication.utils.server_api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class HostDensityData {
    public String host_id;
    public int connected_count;
    public int density;
    public String date;

    public HostDensityData(String host_id, int connected_count, int density) {
        this.host_id = host_id;
        this.connected_count = connected_count;
        this.density = density;

        date = null;
    }

    // http://stackoverflow.com/questions/4216745/java-string-to-date-conversion
    public String getRefinedDate() {
        if (date == null) {
            return null;
        }


        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        fromFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        DateFormat toFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");


        try {
            return toFormat.format(fromFormat.parse(date.substring(0, date.length() - 3)));
        } catch (ParseException e) {
            return null;
        }
    }
}
