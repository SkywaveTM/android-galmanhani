package me.skywave.helloapplication.utils.local_storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HostStorage {
    private HostDbHelper helper;

    public HostStorage(Context context) {
        helper = new HostDbHelper(context);
    }

    public void addHost(HostInformation info) {
        boolean exists = getHost(info.getHostId()) != null;
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HostDbHelper.COLUMN_NAME_HOST_ID, info.getHostId());
        values.put(HostDbHelper.COLUMN_NAME_NAME, info.getName());
        values.put(HostDbHelper.COLUMN_NAME_ALLOWED, info.isAllowed() ? 1 : 0);

        Log.d("insert", values.toString());

        if (exists) {
            String where = String.format("%s='%s'", HostDbHelper.COLUMN_NAME_HOST_ID, info.getHostId());
            db.delete(HostDbHelper.TABLE_NAME, where, null);
        }

        db.insert(HostDbHelper.TABLE_NAME, null, values);
        db.close();
    }

    public HostInformation getHost(String hostId) {
        String selection = String.format("%s='%s'", HostDbHelper.COLUMN_NAME_HOST_ID , hostId);
        HostInformation[] result = query(selection);

        if (result.length == 0) {
            return null;
        }

        return result[0];
    }

    public HostInformation[] getAllHosts() {
        return query(null);
    }

    private HostInformation[] query(String selection) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.query(HostDbHelper.TABLE_NAME, null,
                selection, null,
                null, null, null);

        int hostCount = c.getCount();
        HostInformation[] hosts = new HostInformation[hostCount];
        c.moveToFirst();

        for (int i = 0; i < hostCount; i++) {
            String hostId = c.getString(c.getColumnIndex(HostDbHelper.COLUMN_NAME_HOST_ID));
            String name = c.getString(c.getColumnIndex(HostDbHelper.COLUMN_NAME_NAME));
            boolean allowed = c.getInt(c.getColumnIndex(HostDbHelper.COLUMN_NAME_ALLOWED)) != 0;

            hosts[i] = new HostInformation(hostId, name, allowed);
            c.moveToNext();
        }

        c.close();
        db.close();

        return hosts;
    }
}
