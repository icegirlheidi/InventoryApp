package com.example.omniata.inventoryapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

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
}
