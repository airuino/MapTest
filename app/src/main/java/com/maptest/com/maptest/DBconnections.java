package com.maptest.com.maptest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by hind on 3/11/2017 AD.
 */

public class DBconnections extends SQLiteOpenHelper {


    /** Database name */
    private static String DbName = "AirUino.db";

    /** Version number of the database */
    private static int Version = 1;

    /** Field 1 of the table locations, which is the primary key */
    public static final String FIELD_ROW_ID = "ID";

    /** Field 2 of the table locations, stores the latitude */
    public static final String FIELD_LAT = "lat";

    /** Field 3 of the table locations, stores the longitude*/
    public static final String FIELD_LNG = "lng";

    /** Field 4 of the table locations, stores the zoom level of map*/
    public static final String FIELD_ZOOM = "zom";

    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE = "locations";

    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;


    public DBconnections(Context context) {
        super(context, DbName, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =     "create table " + DATABASE_TABLE + " ( " +
                FIELD_ROW_ID + " integer primary key autoincrement , " +
                FIELD_LNG + " double , " +
                FIELD_LAT + " double , " +
                FIELD_ZOOM + " text " +
                " ) ";

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // db.execSQL("DROP table if EXISTS "+DATABASE_TABLE);
        // onCreate(db);
    }


    /** Inserts a new location to the table locations */
    public long insert(ContentValues contentValues){
        SQLiteDatabase db = this.getWritableDatabase();
        long rowID = db.insert(DATABASE_TABLE, null, contentValues);
        return rowID;
    }

    /** Deletes all locations from the table */
    public int del(){
        SQLiteDatabase db= this.getWritableDatabase();
        int cnt = mDB.delete(DATABASE_TABLE, null , null);
        return cnt;
    }

    /** Returns all the locations from the table */
    public Cursor getAllLocations(){
        SQLiteDatabase db= this.getWritableDatabase();
        return db.query(DATABASE_TABLE, new String[] { FIELD_ROW_ID,  FIELD_LAT , FIELD_LNG, FIELD_ZOOM } , null, null, null, null, null);
    }

}
