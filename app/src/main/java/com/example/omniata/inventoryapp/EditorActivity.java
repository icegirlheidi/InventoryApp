package com.example.omniata.inventoryapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {

    private EditText mNameTextView;
    private EditText mSupplierTextView;
    private EditText mUnitPriceTextView;
    private EditText mQuantityTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_layout);

        mNameTextView = (EditText) findViewById(R.id.product_name);
        mSupplierTextView = (EditText) findViewById(R.id.product_supplier);
        mUnitPriceTextView = (EditText) findViewById(R.id.product_unit_price);
        mQuantityTextView = (EditText) findViewById(R.id.product_quantity);

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
                // Do nothing now
                Toast.makeText(this, "Save product clicked", Toast.LENGTH_SHORT).show();
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
