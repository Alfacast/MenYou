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
public class SQLiteHandlerMenu extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandlerMenu.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "menuYouMenu";

    private static  final String TABLE_MENU = "menu";

    private static final String KEY_ID = "id";
    private static final String KEY_NOME = "nome";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_ID_RISTORANTE = "id_ristorante";

    public SQLiteHandlerMenu(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //creo tabelle
    @Override
    public void onCreate(SQLiteDatabase dbr){
        String CREATE_MENU_TABLE = "CREATE TABLE " + TABLE_MENU + "("
                + KEY_ID + "INTEGER PRIMARY KEY," + KEY_NOME + " TEXT,"
                + KEY_UID + " TEXT," + KEY_CREATED_AT + " TEXT," + KEY_ID_RISTORANTE + " TEXT" + ")";
        dbr.execSQL(CREATE_MENU_TABLE);

        Log.d(TAG, "Database menu tables created");
    }

    // aggiornamento database
    @Override
    public void onUpgrade(SQLiteDatabase dbr, int oldVersion, int newVersion){

        dbr.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);

        onCreate(dbr);
    }

    public void addMenu(String nome, String uid, String created_at, String id_ristorante){
        SQLiteDatabase dbr = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOME, nome);
        values.put(KEY_UID, uid);
        values.put(KEY_CREATED_AT, created_at);
        values.put(KEY_ID_RISTORANTE, id_ristorante);

        long id = dbr.insert(TABLE_MENU, null, values);
        dbr.close();

        Log.d(TAG, "New menu inserted into sqlite: " + id);

    }


    public HashMap<String, String> getMenuDetails(){
        HashMap<String, String> menu = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_MENU;

        SQLiteDatabase dbr = this.getReadableDatabase();
        Cursor cursor = dbr.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if(cursor.getCount() >0){
            menu.put("nome", cursor.getString(1));
            menu.put("uid", cursor.getString(2));
            menu.put("created_at", cursor.getString(3));
            menu.put("id_ristorante", cursor.getString(4));
        }
        cursor.close();
        dbr.close();

        Log.d(TAG, "Fetching menu from Sqlite: " + menu.toString());
        return menu;
    }

    public void deleteMenu(){
        SQLiteDatabase dbr = this.getWritableDatabase();

        dbr.delete(TABLE_MENU, null, null);
        dbr.close();

        Log.d(TAG, "Deleted all menu info from Sqlite");
    }
}
