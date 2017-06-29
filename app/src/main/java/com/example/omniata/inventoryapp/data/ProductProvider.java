package com.example.omniata.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.omniata.inventoryapp.data.ProductContract.ProductEntry;

import org.w3c.dom.Text;

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

        // Set notification uri on cursor
        // If the data at URI changes, we will update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);


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

        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        // Check if user's input of name is empty
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Product must have a name");
        }

        String supplier = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        // Check if user's input of supplier is empty
        if (TextUtils.isEmpty(supplier)) {
            throw new IllegalArgumentException("Product must have a supplier");
        }

        Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
        // Check if user's input of price is smaller than or equal to 0
        if(price != null && price <= 0) {
            throw new IllegalArgumentException("Price must be bigger than 0");
        } else if (price == null || TextUtils.isEmpty(price.toString())) {
            throw new IllegalArgumentException("Product must have unit price");
        }

        Double quantity = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        // Check if user's input of quantity is smaller than 0
        if(quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Quantity shouldn't be less than 0");
        }

        String imageUri = values.getAsString(ProductEntry.COLUMN_PRODUCT_IMAGE);
        if (imageUri == null) {
            throw new IllegalArgumentException("Product must have an image");
        }
        Toast.makeText(getContext(), "The inserted product image uri is: " + imageUri, Toast.LENGTH_SHORT).show();

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

        //Toast.makeText(getContext(), "The newly inserted product id is: " + id, Toast.LENGTH_SHORT).show();

        // Notify all listener that the data has been changed for products uri
        // content://com.example.omniata.inventoryapp/products/products
        getContext().getContentResolver().notifyChange(uri,null);

        // Return content uri with the id of the newly added row at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            // If uri matches products uri, then delete the whole table
            case PRODUCTS:
                return deletePet(uri, selection, selectionArgs);
            // If uri matches a single product, then delete a single row of product
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return deletePet(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Delete is not supported for uri: " + uri);
        }
    }

    private int deletePet(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);

        // If more than zero rows are deleted, then notify listener all listeners
        // that the data has been changed for product uri

        Log.e("TEST", "Id of rows deleted: " + rowDeleted);
        if(rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        // Return the number of rows deleted
        return rowDeleted;
    };

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateTable(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateTable(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot update table for uri: " + uri);
        }
    }

    private int updateTable(Uri uri, ContentValues values, String selection, String[] selectionArgs) {


        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Update products database
        int rowsAffected = db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // Notify changes so the ui in MainActivity will be updated automatically
        getContext().getContentResolver().notifyChange(uri, null);

        // Return number of rows affected
        return rowsAffected;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // The content uri is: content:// com.example.omniata.inventoryapp.products/products
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);

        // The content uri is: content:// com.example.omniata.inventoryapp.products/products/#
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

}
