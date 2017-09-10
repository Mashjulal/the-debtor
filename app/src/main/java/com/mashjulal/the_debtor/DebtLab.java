package com.mashjulal.the_debtor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mashjulal.the_debtor.database.DebtsDB;

import java.util.ArrayList;
import java.util.List;

import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.BORROW_DATE;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.DESCRIPTION;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.ID;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.MONEY_AMOUNT;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.PHOTO_PATH;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.RECIPIENT_NAME;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.RETURN_DATE;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.THING_NAME;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.TYPE;

public class DebtLab {
    private static DebtLab sDebtLab;

    private DebtsDB db;
    private Context mContext;

    static DebtLab getInstance(Context context) {
        if (sDebtLab == null)
            sDebtLab = new DebtLab(context);
        return sDebtLab;
    }

    private DebtLab(Context context) {
        mContext = context.getApplicationContext();
        db = new DebtsDB(mContext);
    }
    
    List<Debt> getDebts(){
        db.open();
        Cursor cursor = db.getAllData();

        List<Debt> debts = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Debt debt = parseCursor(cursor);
                debts.add(debt);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return debts;
    }
    
    Debt getDebt(long id){
        db.open();
        String selectionClause = ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};

        Cursor cursor = db.getData(selectionClause, selectionArgs);
        cursor.moveToFirst();
        Debt d = parseCursor(cursor);
        cursor.close();
        db.close();
        return d;
    }

    void updateDebt(Debt d){
        db.open();
        db.updateData(getContentValues(d));
        db.close();
    }

    private Debt parseCursor(Cursor cursor){
        long id = cursor.getLong(cursor.getColumnIndex(ID));
        String type = cursor.getString(cursor.getColumnIndex(TYPE));
        Long borrowDate = cursor.getLong(cursor.getColumnIndex(BORROW_DATE));
        Long returnDate = cursor.getLong(cursor.getColumnIndex(RETURN_DATE));
        String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
        String recipientName = cursor.getString(cursor.getColumnIndex(RECIPIENT_NAME));
        String thingName = cursor.getString(cursor.getColumnIndex(THING_NAME));
        Double moneyAmount = cursor.getDouble(cursor.getColumnIndex(MONEY_AMOUNT));

        String strPhotoPath = cursor.getString(cursor.getColumnIndex(PHOTO_PATH));
        Uri photoPath = (strPhotoPath != null) ? Uri.parse(strPhotoPath) : null;

        return new Debt(id, type, thingName, photoPath, moneyAmount,
                borrowDate, returnDate, description, recipientName);
    }

    void removeDebt(long id){
        db.open();
        db.deleteData(id);
        db.close();
    }

    void addDebt(Debt d){
        db.open();
        db.addData(getContentValues(d));
        db.close();
    }

    private ContentValues getContentValues(Debt d){
        ContentValues cv = new ContentValues();
        if (d.getId() != null)
            cv.put(ID, d.getId());
        cv.put(TYPE, d.getType());
        cv.put(THING_NAME, d.getThingName());
        cv.put(PHOTO_PATH, (d.getPhotoPath() != null) ? d.getPhotoPath().toString() : null);
        cv.put(MONEY_AMOUNT, d.getMoneyAmount());
        cv.put(BORROW_DATE, d.getBorrowDate());
        cv.put(RETURN_DATE, d.getReturnDate());
        cv.put(DESCRIPTION, d.getDescription());
        cv.put(RECIPIENT_NAME, d.getRecipientName());

        return cv;
    }
}
