package com.example.tonyso.TrafficApp.Singleton;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tonyso.TrafficApp.model.TimedBookMark;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TonySoMan on 25/6/2015.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "bookmark.db";
    public static final int OLD_VERSION = 1;
    public static final int NEW_VERSION = 2;
    private static SQLiteDatabase database;
    private static final String BOOKMARK_TABLE_NAME = "Bookmark";
    private static final String KEY_ID = "_id";
    private static final String KEY_ROUTE_NAME = "Route";
    private static final String KEY_ROUTE_NAME_ZH = "Route_ZH";
    private static final String KEY_STARTTIME = "StartTime";
    private static final String KEY_TARGETTIME = "TargetTime";
    private static final String KEY_ROUTEIMAGEKEY = "RouteImageKey";
    private static final String KEY_DISTRICT = "District";
    private static final String KEY_DISTRICT_ZH = "District_ZH";
    private static final String KEY_TIMEOVER = "isTimeOver";

    //Create Table
    public static final String CREATE_TABLE =
            "CREATE TABLE " + BOOKMARK_TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    KEY_ROUTE_NAME + " TEXT," +
                    KEY_ROUTE_NAME_ZH + " TEXT," +
                    KEY_STARTTIME + " TEXT," +
                    KEY_TARGETTIME + " TEXT," +
                    KEY_ROUTEIMAGEKEY + " TEXT," +
                    KEY_DISTRICT + " TEXT," +
                    KEY_DISTRICT_ZH + " TEXT," +
                    KEY_TIMEOVER + " boolean NOT NULL default 0);";

    //Drop Table
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + BOOKMARK_TABLE_NAME;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, OLD_VERSION);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new SQLiteHelper(context).getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

        public long add_Bookmark(TimedBookMark bookMark) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ROUTE_NAME, bookMark.getBkRouteName()[0]);
            values.put(KEY_ROUTE_NAME_ZH, bookMark.getBkRouteName()[1]);
            values.put(KEY_STARTTIME, bookMark.getStartTime().toString());
            values.put(KEY_TARGETTIME, bookMark.getTargetTime().toString());
            values.put(KEY_ROUTEIMAGEKEY, bookMark.getRouteImageKey());
            values.put(KEY_DISTRICT, bookMark.getRegions()[0]);
            values.put(KEY_DISTRICT_ZH, bookMark.getRegions()[1]);
            values.put(KEY_TIMEOVER, bookMark.isTimeOver());
            // Inserting Row
            long success = db.insert(BOOKMARK_TABLE_NAME, null, values);
            db.close(); // Closing database connection
            return success;
        }

        // Getting All Contacts
        public List<TimedBookMark> getBookmarksList() {
            List<TimedBookMark> bookMark_List = new ArrayList<>();
            try {
        // Select All Query
                String selectQuery = "SELECT * FROM " + BOOKMARK_TABLE_NAME;

                SQLiteDatabase db = getWritableDatabase();
                Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        TimedBookMark.Builder builder = new TimedBookMark.Builder();
                        builder.set_id(cursor.getInt(0))
                                .setBkRouteName(new String[]{cursor.getString(1), cursor.getString(2)})
                                .setTimestamp(cursor.getString(3))
                                .setTargetTime(cursor.getString(4))
                                .setRouteImageKey(cursor.getString(5))
                                .setDistrict(new String[]{cursor.getString(6), cursor.getString(7)})
                                .setIsTimeOver((cursor.getInt(8) == 1) ? true : false);
                        bookMark_List.add(builder.build());
                    } while (cursor.moveToNext());
                }
// return contact list
                cursor.close();
                db.close();
                return bookMark_List;
            } catch (Exception e) {
// TODO: handle exception
                Log.e("all_contact", "" + e);
            }
            return bookMark_List;
        }
    }

