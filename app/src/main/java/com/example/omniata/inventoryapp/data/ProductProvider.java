package com.example.omniata.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.omniata.inventoryapp.data.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {

    /**
     * Log tag for log purpose
     */
    private static final String LOG_TAG = ProductProvider.class.getName();

    private ProductDbHelper mDbHelper;

    /**
     * URI matcher code for the content URI for the products table
     */
    private static final int PRODUCTS = 100;

    /**
     * URI matcher code for the content URI for a single product
     */

    private static final int PRODUCT_ID = 101;
    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        // Get a readable sqlite database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Create a uri matcher
        final int match = sUriMatcher.match(uri);
        switch (match) {
            // query entire products table
            case PRODUCTS:
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            // query a single row of products table
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }

        // Return the whole product table or a row of product
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);

        // Call insertProduct method when the content uri matches
        // content://com.example.omniata.inventoryapp/products
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
        }
        return null;
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        // Get sqlite database for inserting data
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // An id with the type long is returned
        // from inserting data into sqlite database
        long id = db.insert(ProductEntry.TABLE_NAME, null, values);

        // Create log if failed to insert a row of product
        // If failed to insert, then the id will be -1
        if (id == -1) {
            Log.e(LOG_TAG, "Insert data faild for uri: " + uri);
            return null;
        }

        Toast.makeText(getContext(), "The newly inserted product id is: " + id, Toast.LENGTH_SHORT).show();

        // Return content uri with the id of the newly added row at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // The content uri is: content:// com.example.omniata.inventoryapp.products/products
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);

        // The content uri is: content:// com.example.omniata.inventoryapp.products/products/#
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

}
