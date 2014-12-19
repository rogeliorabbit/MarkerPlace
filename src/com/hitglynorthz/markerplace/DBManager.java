package com.hitglynorthz.markerplace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    public static final String TABLE_NAME = "places";

    public static final String CN_ID = "_id";
    public static final String CN_PLACENAME = "place_name";
    public static final String CN_PLACEDESC = "place_desc";
    public static final String CN_IMG = "place_img";
    public static final String CN_LAT = "lat";
    public static final String CN_LNG = "lng";

    public static final String CREATE_TABLE = "create table " +TABLE_NAME+ " ("
            + CN_ID + " integer primary key autoincrement,"
            + CN_PLACENAME + " text not null,"
            + CN_PLACEDESC + " text not null,"
            + CN_IMG + " text,"
            + CN_LAT + " text not null,"
            + CN_LNG + " text not null);";

    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {

        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    private ContentValues generarContentValues(String place_name, String place_desc, String lat, String lng, String place_img) {
        ContentValues valores = new ContentValues();
        valores.put(CN_PLACENAME, place_name);
        valores.put(CN_PLACEDESC, place_desc);
        valores.put(CN_LAT, lat);
        valores.put(CN_LNG, lng);
        valores.put(CN_IMG, place_img);
        return valores;
    }
    
    public Cursor getAllMarkers(){
        return db.query(TABLE_NAME, new String[] { CN_ID,  CN_PLACENAME , CN_PLACEDESC, CN_LAT, CN_LNG, CN_IMG } , null, null, null, null, null);
    }
    
    public Cursor searchMarkerID(String id) {     
        String[] columnas = new String[]{CN_ID, CN_PLACENAME, CN_PLACEDESC, CN_LAT, CN_LNG, CN_IMG};   
    	return db.query(TABLE_NAME,columnas,CN_ID + "=?", new String[]{id},null,null,null);
    }

    public void insertMarker(String place_name, String place_desc, String lat, String lng, String place_img) {
        db.insert(TABLE_NAME, null, generarContentValues(place_name, place_desc, lat, lng, place_img) );
    }
    
    public void deleteMarker(String id) {
    	db.delete(TABLE_NAME,CN_ID  + "=?", new String[]{id});
    }
    
    public void editMarker(String id, String place_name, String place_desc, String lat, String lng, String place_img) {
    	db.update(TABLE_NAME, generarContentValues(place_name, place_desc, lat, lng, place_img), CN_ID + "=?", new String[]{id});
    }
    
    public Cursor loadMarkers_by_name(String name) {     
        String[] columnas = new String[]{CN_ID, CN_PLACENAME};     
        return db.query(TABLE_NAME, columnas, CN_PLACENAME + "=?", new String[]{name}, null, null, null);
    }

}
