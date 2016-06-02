package com.alfacast.menyou.restaurant;

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
public class SQLiteHandlerPortata extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandlerPortata.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "menuYouPortata";

    private static  final String TABLE_PORTATA = "portata";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CATEGORY = "categoria";
    private static final String KEY_DESC = "descrizione";
    private static final String KEY_PRICE = "prezzo";
    private static final String KEY_OPTIONS = "opzioni";
    private static final String KEY_DISP = "disponibile";
    private static final String KEY_PHOTO = "foto";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";


    public SQLiteHandlerPortata(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //creo tabelle
    @Override
    public void onCreate(SQLiteDatabase dbr){
        String CREATE_PORTATA_TABLE = "CREATE TABLE " + TABLE_PORTATA + "("
                + KEY_ID + "INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_CATEGORY + " TEXT," + KEY_DESC + " TEXT,"
                + KEY_PRICE + " TEXT," + KEY_OPTIONS + " TEXT,"
                + KEY_DISP + " TEXT," + KEY_PHOTO + " TEXT,"
                + KEY_UID + " TEXT," + KEY_CREATED_AT + " TEXT" + ")";
        dbr.execSQL(CREATE_PORTATA_TABLE);

        Log.d(TAG, "Database portata tables created");
    }

    // aggiornamento database
    @Override
    public void onUpgrade(SQLiteDatabase dbr, int oldVersion, int newVersion){

        dbr.execSQL("DROP TABLE IF EXISTS " + TABLE_PORTATA);

        onCreate(dbr);
    }

    public void addPortata(String nome, String categoria, String descrizione, String prezzo, String opzioni, String disponibile, String foto, String uid, String created_at){
        SQLiteDatabase dbr = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, nome);
        values.put(KEY_CATEGORY, categoria);
        values.put(KEY_DESC, descrizione);
        values.put(KEY_PRICE, prezzo);
        values.put(KEY_OPTIONS, opzioni);
        values.put(KEY_DISP, disponibile);
        values.put(KEY_PHOTO, foto);
        values.put(KEY_UID, uid);
        values.put(KEY_CREATED_AT, created_at);

        long id = dbr.insert(TABLE_PORTATA, null, values);
        dbr.close();

        Log.d(TAG, "New portata inserted into sqlite: " + id);

    }


    public HashMap<String, String> getPortataDetails(){
        HashMap<String, String> portata = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_PORTATA;

        SQLiteDatabase dbr = this.getReadableDatabase();
        Cursor cursor = dbr.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if(cursor.getCount() >0){
            portata.put("nome", cursor.getString(1));
            portata.put("categoria", cursor.getString(2));
            portata.put("descrizione", cursor.getString(3));
            portata.put("prezzo", cursor.getString(4));
            portata.put("opzioni", cursor.getString(5));
            portata.put("disponibile", cursor.getString(6));
            portata.put("foto", cursor.getString(7));
            portata.put("uid", cursor.getString(8));
            portata.put("created_at", cursor.getString(9));
        }
        cursor.close();
        dbr.close();

        Log.d(TAG, "Fetching portata from Sqlite: " + portata.toString());
        return portata;
    }

    public void deletePortata(){
        SQLiteDatabase dbr = this.getWritableDatabase();

        dbr.delete(TABLE_PORTATA, null, null);
        dbr.close();

        Log.d(TAG, "Deleted all portata info from Sqlite");
    }
}
