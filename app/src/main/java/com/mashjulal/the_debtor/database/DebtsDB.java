package com.mashjulal.the_debtor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.BORROW_DATE;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.DESCRIPTION;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.ID;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.MONEY_AMOUNT;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.PHOTO_PATH;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.RECIPIENT_NAME;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.RETURN_DATE;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.THING_NAME;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.TYPE;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.TABLE_NAME;

public class DebtsDB {
    private static final String DATABASE_NAME = "debtBase.db";
    private static final int VERSION = 1;

    private static final String DB_CREATE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TYPE + " TEXT, " +
                    THING_NAME + " TEXT, " +
                    PHOTO_PATH + " TEXT, " +
                    MONEY_AMOUNT + " REAL, " +
                    BORROW_DATE + " INTEGER, " +
                    RETURN_DATE + " INTEGER, " +
                    DESCRIPTION + " TEXT, " +
                    RECIPIENT_NAME + " TEXT);";

    private Context context;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DebtsDB(Context c){
        context = c;
    }

    public void open(){
        dbHelper = new DBHelper(context, DATABASE_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        if (dbHelper != null)
            dbHelper.close();
    }

    public Cursor getAllData(){
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public void addData(ContentValues cv){
        db.insert(TABLE_NAME, null, cv);
    }

    public void updateData(ContentValues cv){
        String whereClause = ID + " = ?";
        String[] whereArgs = new String[]{Long.toString(cv.getAsLong(ID))};
        db.update(TABLE_NAME, cv, whereClause, whereArgs);
    }

    public void deleteData(long id){
        db.delete(TABLE_NAME, ID + " = " + Long.toString(id), null);
    }

    public Cursor getData(String whereClause, String[] whereArgs){
        return db.query(TABLE_NAME, null, whereClause, whereArgs, null, null, null);
    }

    private class DBHelper extends SQLiteOpenHelper {
        DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

}
