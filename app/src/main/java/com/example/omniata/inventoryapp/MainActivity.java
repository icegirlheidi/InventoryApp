package com.example.omniata.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.omniata.inventoryapp.data.ProductContract.ProductEntry;
import com.example.omniata.inventoryapp.data.ProductDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ProductCursorAdapter mProductCursorAdapter;

    // A unique identifier for this loader.
    // Identifiers are scoped to a particular LoaderManager instance.
    private static final int LOADER_ID = 0;

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

        // Create new cursor adapter
        mProductCursorAdapter = new ProductCursorAdapter(this, null);
        // Get the listview for showing a list of products
        ListView list = (ListView) findViewById(R.id.list_view);
        // Set up cursor adapter with listview
        list.setAdapter(mProductCursorAdapter);

        // Start the loader
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.setData(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });
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

    // Delete all products
    private void deleteProducts() {
        // Call delete method in ProductProvider through getContentResolver
        getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        // Toast message telling user all products have been deleted
        Toast.makeText(this, R.string.toast_message_delete_all, Toast.LENGTH_SHORT).show();
    }

    // Dialog for user to confirm deleting or cancel it
    private void showDeleteConfirmationDialog() {
        // Create builder to set up the necessary info for creating alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Message asking user: "Do you really want to delete all products?"
        builder.setMessage(R.string.delete_all_message);
        // Delete button which confirm deleting all products
        builder.setPositiveButton(R.string.delete_delete_confirmation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProducts();
            }
        });
        // Cancel button which cancel deleting and hide dialog
        builder.setNegativeButton(R.string.cancel_delete_confirmation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show up alert dialog
        builder.create().show();
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
                // Show dialog for user to confirm deleting all products
                showDeleteConfirmationDialog();
                return true;
            case R.id.insert_dummy_data:
                insertProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY
        };
        return new CursorLoader(
                this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProductCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductCursorAdapter.swapCursor(null);
    }
}
