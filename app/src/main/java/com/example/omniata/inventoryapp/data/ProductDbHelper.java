package com.example.omniata.inventoryapp.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.omniata.inventoryapp.data.ProductContract.ProductEntry;

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // CREATE TABLE products (_id INTEGER, name TEXT, supplier TEXT, price INTEGER, quantity INTEGER);
        String SQL_CREATE_PRODUCTS_TABLE =
                "CREATE TABLE " + ProductEntry.TABLE_NAME + "(" +
                        ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                        ProductEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT NOT NULL, " +
                        ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL DEFAULT 0, " +
                        ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                        ProductEntry.COLUMN_PRODUCT_IMAGE + " TEXT);";
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_PRODUCTS_TABLE =
                "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_PRODUCTS_TABLE);
        onCreate(db);
    }
}
