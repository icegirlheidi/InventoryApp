package com.example.omniata.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.omniata.inventoryapp.data.ProductContract.ProductEntry;
import com.example.omniata.inventoryapp.data.ProductDbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // When floating action button is clicked, the EditorActivity page shows up
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Read a database
        ProductDbHelper mDbHelper = new ProductDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
    }

    // Insert method of dummy data
    private void insertProduct() {
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Pencils");
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, "Hongkong co.");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 2);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 20);

        // Insert a new row of pencils into provider using ContentResolver
        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.delete_all:
                // Do nothing now
                Toast.makeText(this, "Delete all clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.insert_dummy_data:
                insertProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
