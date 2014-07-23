package com.ragic.android.reader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.ragic.android.reader.pojo.Account;
import com.ragic.android.reader.pojo.User;

import java.util.ArrayList;

/**
 * User: Azuritul
 * Date: 2010/6/18
 * Time: 下午 06:01:57
 */
public class DataHelper {

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "RagicReaderDB";
    private static final String TABLE_USER = "user";
    private static final String TABLE_ACCOUNT = "account";
    private static final String TAG = "RagicReaderDB";
    private final Context context;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ACCOUNT = "account";

    private static final String CREATE_TABLE_USER =
        "CREATE TABLE " + TABLE_USER + " (" +
            COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
            COLUMN_PASSWORD + " TEXT " +
            ");";

    private static final String CREATE_TABLE_ACCOUNT =
        "CREATE TABLE " + TABLE_ACCOUNT + " (" +
            COLUMN_USERNAME + " TEXT ," +
            COLUMN_ACCOUNT + " TEXT " +
            ");";

    private static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS user";
    private static final String DROP_TABLE_ACCOUNT = "DROP TABLE IF EXISTS account";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_USER);
            db.execSQL(CREATE_TABLE_ACCOUNT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL(DROP_TABLE_USER);
            db.execSQL(DROP_TABLE_ACCOUNT);
            onCreate(db);
        }
    }

    public DataHelper(Context mCtx) {
        this.context = mCtx;
    }

    public DataHelper open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public User getUser() {
        Cursor cursor = db.query(true, TABLE_USER, new String[]{COLUMN_USERNAME, COLUMN_PASSWORD}, null, null, null, null, null, null);
        User user = null;
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                user = new User();
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
                cursor.moveToNext();
            }
            cursor.close();
            if(user != null){
                Cursor accountCur = db.query(true, TABLE_ACCOUNT, new String[]{COLUMN_ACCOUNT}, COLUMN_USERNAME + "='" + user.getEmail() + "'", null, null, null, null, null);
                if (accountCur != null) {
                    accountCur.moveToFirst();
                    while (!accountCur.isAfterLast()) {
                        user.addAccount(accountCur.getString(accountCur.getColumnIndexOrThrow(COLUMN_ACCOUNT)));
                        accountCur.moveToNext();
                    }
                }
                accountCur.close();
            }
        }
        return user;
    }

    public void deleteUser(){
        String sql = "Delete from " + TABLE_USER;
        db.execSQL(sql);
    }

    public void deleteAccount(){
        String sql = "Delete from " + TABLE_ACCOUNT;
        db.execSQL(sql);
    }

    public long createUser(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        return db.insert(TABLE_USER, null, values);
    }

    public void addAccount(User user, String newAccount) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getEmail());
        values.put(COLUMN_ACCOUNT, newAccount);
        db.insert(TABLE_ACCOUNT, null, values);
    }

    public void createAccount(User user) {
        user.getEmail();
        ArrayList<Account> accounts = user.getAccounts();

        ContentValues values = null;
        for (Account a : accounts) {
            values = new ContentValues();
            values.put(COLUMN_USERNAME, user.getEmail());
            values.put(COLUMN_ACCOUNT, a.getAccountName());
            db.insert(TABLE_ACCOUNT, null, values);
        }
    }

}