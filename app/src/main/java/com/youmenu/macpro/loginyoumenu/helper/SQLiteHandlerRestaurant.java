package com.youmenu.macpro.loginyoumenu.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by MacPro on 05/05/16.
 */
public class SQLiteHandlerRestaurant extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandlerRestaurant.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "menuYouLoginRistorante";

    private static  final String TABLE_USER = "ristorante";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "indirizzo";
    private static final String KEY_PARTITAIVA = "partitaIva";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TEL = "telefono";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";


    public SQLiteHandlerRestaurant(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //creo tabelle
    @Override
    public void onCreate(SQLiteDatabase dbr){
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + "INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_ADDRESS + " TEXT," + KEY_PARTITAIVA + " TEXT UNIQUE,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_TEL + " TEXT UNIQUE,"
                + KEY_UID + " TEXT," + KEY_CREATED_AT + " TEXT" + ")";
        dbr.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database restaurant tables created");
    }

    // aggiornamento database
    @Override
    public void onUpgrade(SQLiteDatabase dbr, int oldVersion, int newVersion){

        dbr.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        onCreate(dbr);
    }

    public void addUser(String name, String address, String partitaIva, String email, String tel, String uid, String created_at){
        SQLiteDatabase dbr = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_PARTITAIVA,partitaIva);
        values.put(KEY_EMAIL, email);
        values.put(KEY_TEL, tel);
        values.put(KEY_UID, uid);
        values.put(KEY_CREATED_AT, created_at);

        long id = dbr.insert(TABLE_USER, null, values);
        dbr.close();

        Log.d(TAG, "New user inserted into sqlite: " + id);

    }


    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase dbr = this.getReadableDatabase();
        Cursor cursor = dbr.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if(cursor.getCount() >0){
            user.put("name", cursor.getString(1));
            user.put("address", cursor.getString(2));
            user.put("partitaIva", cursor.getString(3));
            user.put("email", cursor.getString(4));
            user.put("tel", cursor.getString(5));
            user.put("uid", cursor.getString(6));
            user.put("created_at", cursor.getString(7));
        }
        cursor.close();
        dbr.close();

        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());
        return user;
    }

    public void deleteUsers(){
        SQLiteDatabase dbr = this.getWritableDatabase();

        dbr.delete(TABLE_USER, null, null);
        dbr.close();

        Log.d(TAG, "Deleted all user info from Sqlite");
    }
}
