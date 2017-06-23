package com.example.omniata.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omniata.inventoryapp.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity {

    private EditText mNameTextView;
    private EditText mSupplierTextView;
    private EditText mPriceTextView;
    private EditText mQuantityTextView;
    private Uri mCurrentProductUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_layout);

        mNameTextView = (EditText) findViewById(R.id.product_name);
        mSupplierTextView = (EditText) findViewById(R.id.product_supplier);
        mPriceTextView = (EditText) findViewById(R.id.product_unit_price);
        mQuantityTextView = (EditText) findViewById(R.id.product_quantity);

        mCurrentProductUri = getIntent().getData();
        Log.e("TEST", "Uri: " + mCurrentProductUri);
    }

    // Insert user's input into products table
    private void insertProduct() {

        // Get user's input of product name and remove possible space before of after it
        String nameString = mNameTextView.getText().toString().trim();

        // Get user's input of product supplier and remove possible space before of after it
        String supplierString = mSupplierTextView.getText().toString().trim();

        // Get user's input of product price
        // Remove possible space before or after it
        // And parse it as int
        int priceInt = Integer.parseInt(mPriceTextView.getText().toString().trim());

        // Get user's input of product quantity
        // Remove possible space before or after it
        // And parse it as int
        int quantityInt = Integer.parseInt(mQuantityTextView.getText().toString().trim());

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceInt);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityInt);

        // Insert user's input as a new row into provider using ContentResolver
        Uri uri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

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
                // Insert new product
                insertProduct();
                // Finish current activity and return to MainActivity
                finish();
                return true;
            case R.id.delete_product:
                // Do nothing now
                Toast.makeText(this, "Delete product clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.order_product:
                // Do nothing now
                Toast.makeText(this, "Order product clicked", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
