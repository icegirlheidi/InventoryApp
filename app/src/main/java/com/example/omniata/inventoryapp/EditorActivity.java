package com.example.omniata.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.omniata.inventoryapp.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;
    private EditText mSupplierEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private Uri mCurrentProductUri;

    private static final int LOADER_ID = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_layout);

        mNameEditText = (EditText) findViewById(R.id.product_name);
        mSupplierEditText = (EditText) findViewById(R.id.product_supplier);
        mPriceEditText = (EditText) findViewById(R.id.product_unit_price);
        mQuantityEditText = (EditText) findViewById(R.id.product_quantity);

        // Get the current product uri passed through intent from MainActivity
        mCurrentProductUri = getIntent().getData();
        Log.e("TEST", "Uri: " + mCurrentProductUri);

        // If there is current product uri passed through intent
        if (mCurrentProductUri != null) {
            // Start loader
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    // Save user's input into products table
    private void saveProduct() {
        
        // Get user's input of product name and remove possible space before of after it
        String nameString = mNameEditText.getText().toString().trim();

        // Get user's input of product supplier and remove possible space before of after it
        String supplierString = mSupplierEditText.getText().toString().trim();

        // Get user's input of product price
        // Remove possible space before or after it
        // And parse it as int
        int priceInt = Integer.parseInt(mPriceEditText.getText().toString().trim());

        // Get user's input of product quantity
        // Remove possible space before or after it
        // And parse it as int
        int quantityInt = Integer.parseInt(mQuantityEditText.getText().toString().trim());

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceInt);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityInt);

        // If current product uri is null,
        // then it's in inserting new product mode
        if (mCurrentProductUri == null) {
            // Insert user's input as a new row into provider using ContentResolver
            Uri uri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            Toast.makeText(this, "Number of rows updated: " + rowsAffected, Toast.LENGTH_SHORT).show();
        }
    }

    // Delete current product
    private void deleteProduct() {

        if (mCurrentProductUri != null) {
            getContentResolver().delete(mCurrentProductUri, null, null);
            Log.e("TEST", "pet deleted: with uri: " + mCurrentProductUri);
            Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.save_product:
                // Save product
                saveProduct();
                // Finish current activity and return to MainActivity
                finish();
                return true;
            case R.id.delete_product:
                // Delete current product
                deleteProduct();
                // Close current activity and return to MainActivity
                finish();
                return true;
            case R.id.order_product:
                // Do nothing now
                Toast.makeText(this, "Order product clicked", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("TEST", "onCreateLoader() initiated");
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY
        };
        return new CursorLoader(
                this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // If the cursor has first row, then move to the first row
        if (cursor.moveToFirst()) {

            // Get the value of name, supplier, price and quantity from cursor
            String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
            String supplier = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER));
            int price = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
            int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));

            // Set the value of name, supplier, price and quantity to edittext view
            mNameEditText.setText(name);
            mSupplierEditText.setText(supplier);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Clear and reset all EditText view in EditorActivity
        mNameEditText.setText("");
        mSupplierEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
    }
}
