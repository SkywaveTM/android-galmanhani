package me.skywave.helloapplication.utils.local_storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HostDbHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "galmanhani.db";
    public static final String TABLE_NAME = "host_list";
    public static final String COLUMN_NAME_HOST_ID = "host_id";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_ALLOWED = "allowed";

    public HostDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = String.format("CREATE TABLE %s (" +
                "%s TEXT PRIMARY KEY," +
                "%s TEXT," +
                "%s INTEGER" +
                ")", TABLE_NAME, COLUMN_NAME_HOST_ID,
                COLUMN_NAME_NAME, COLUMN_NAME_ALLOWED);

        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // none
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
    }
}
