package com.example.group4_finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyDatabase {
    private Context context;
    private final Helper helper;

    public MyDatabase(Context c){
        context = c;
        helper = new Helper(context);
    }

    public long insertData (String km, String kcal, String time, String steps)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.KM, km);
        contentValues.put(Constants.KCAL, kcal);
        contentValues.put(Constants.TIME, time);
        contentValues.put(Constants.STEPS, steps);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.KM, Constants.KCAL, Constants.TIME, Constants.STEPS};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public String getSelectedData(String type)
    {
        //select plants from database of specified type from user
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.KM, Constants.KCAL, Constants.TIME, Constants.STEPS};

        String selection = Constants.STEPS + "='" +type+ "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(Constants.KM);
            int index2 = cursor.getColumnIndex(Constants.KCAL);
            int index3 = cursor.getColumnIndex(Constants.TIME);
            int index4 = cursor.getColumnIndex(Constants.STEPS);
            String routeKm = cursor.getString(index1);
            String routeKcal = cursor.getString(index2);
            String routeTime = cursor.getString(index3);
            String routeSteps = cursor.getString(index4);
            buffer.append(routeKm + " " + routeKcal + " " + routeTime + " " + routeSteps + "\n");
        }
        return buffer.toString();
    }



}
