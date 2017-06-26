package com.example.omniata.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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

    private static boolean mProductInputChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_layout);

        mNameEditText = (EditText) findViewById(R.id.product_name);
        mSupplierEditText = (EditText) findViewById(R.id.product_supplier);
        mPriceEditText = (EditText) findViewById(R.id.product_unit_price);
        mQuantityEditText = (EditText) findViewById(R.id.product_quantity);

        // Set on touch listener on all edittext view
        // so that we can know whether they're modified
        mNameEditText.setOnTouchListener(mOnTouchListener);
        mSupplierEditText.setOnTouchListener(mOnTouchListener);
        mPriceEditText.setOnTouchListener(mOnTouchListener);
        mQuantityEditText.setOnTouchListener(mOnTouchListener);

        // Get the current product uri passed through intent from MainActivity
        mCurrentProductUri = getIntent().getData();
        Log.e("TEST", "Uri: " + mCurrentProductUri);

        // If there is current product uri passed through intent
        if (mCurrentProductUri != null) {
            setTitle(getString(R.string.edit_product));
            // Start loader
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            setTitle(getString(R.string.add_new_product));


        }
    }

    @Override
    public void onBackPressed() {
        // If user's input hasn't been changed
        if (!mProductInputChanged) {
            // then leave current activity and go back to MainActivity
            finish();
            return;
        }
        final DialogInterface.OnClickListener discardChangeClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardChangeClickListener);
    }

    // On touch listener to listen whether any view has been changed
    // if yes, then we change boolean value of mProductInputChanged to true
    // which indicates the view has been modified
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            mProductInputChanged = true;
            return false;
        }
    };

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
            // If current product uri is not null
            // then it's in editing mode
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
            // Leave current activity after deleting current product
            finish();
        }
    }


    // Show dialog if user pressed delete button
    private void showDeleteConfirmationDialog() {
        // Create alertdialog builder
        // which makes the needed info for creating alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirmation_message));
        // Positivie button which is "Delete"
        builder.setPositiveButton(R.string.delete_delete_confirmation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        // Negative button which is "Cancel"
        builder.setNegativeButton(R.string.cancel_delete_confirmation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create alert dialog and then show it up
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Show dialog if user has unsaved changes
    // Pass in a parameter discardChangeClickListener which will be used in setPositiveButton
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardChangeClickListener) {
        // Create builder to set up the needed info for creating dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Message asking user:
        // "You have unsaved changes. Do you really want to leave?"
        builder.setMessage(R.string.unsaved_changes_message);
        // Positive answer: Leave
        // Then finish current activity and go back to MainActivity
        builder.setPositiveButton(R.string.leave_unsaved_changes, discardChangeClickListener);
        // Negative answer: Keep editing
        // Then dismiss dialog
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show up dialog
        builder.create().show();
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
        // If current product uri is null
        // then it's in adding new product mode
        if (mCurrentProductUri == null) {
            MenuItem menuItemDelete = menu.findItem(R.id.delete_product);

            // Then we don't need the delete product item in menu
            menuItemDelete.setVisible(false);

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        }
        return true;
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
                // Show dialog for user to confirm deleting
                showDeleteConfirmationDialog();
                // Close current activity and return to MainActivity
                //finish();
                return true;
            case R.id.order_product:
                // Do nothing now
                Toast.makeText(this, "Order product clicked", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                // If no input has been changed
                if (!mProductInputChanged) {
                    // Then leave current EditorActivity
                    // and go back to MainActivity
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardChangeClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show unsaved change dialog with
                // discardChangeListener passed in as parameter
                showUnsavedChangesDialog(discardChangeClickListener);
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
