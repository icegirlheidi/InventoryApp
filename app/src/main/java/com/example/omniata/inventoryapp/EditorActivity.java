package com.example.omniata.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.omniata.inventoryapp.data.ProductContract;
import com.example.omniata.inventoryapp.data.ProductContract.ProductEntry;

import org.w3c.dom.Text;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    Uri mImageUri;
    private ImageView mImageView;
    private EditText mNameEditText;
    private EditText mSupplierEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private ImageButton mPlusButton;
    private ImageButton mMinusButton;
    private Uri mCurrentProductUri;
    private Button mSelectImageButton;

    private static final int LOADER_ID = 0;

    private boolean mProductInputChanged = false;

    private String mNameString;
    private String mSupplierString;
    private Double mPrice = 0.0;
    private int mQuantity = 0;
    private String mEmail;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    // Request code created from image request
    private static final int IMAGE_REQUEST = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_layout);

        mImageView = (ImageView) findViewById(R.id.image_view);
        mNameEditText = (EditText) findViewById(R.id.product_name);
        mSupplierEditText = (EditText) findViewById(R.id.product_supplier);
        mPriceEditText = (EditText) findViewById(R.id.product_unit_price);
        mQuantityEditText = (EditText) findViewById(R.id.product_quantity);
        mPlusButton = (ImageButton) findViewById(R.id.add_button);
        mMinusButton = (ImageButton) findViewById(R.id.minus_button);
        mSelectImageButton = (Button) findViewById(R.id.select_image_button);

        // Set on touch listener on all edittext view
        // so that we can know whether they're modified
        mNameEditText.setOnTouchListener(mOnTouchListener);
        mSupplierEditText.setOnTouchListener(mOnTouchListener);
        mPriceEditText.setOnTouchListener(mOnTouchListener);
        mQuantityEditText.setOnTouchListener(mOnTouchListener);
        mImageView.setOnTouchListener(mOnTouchListener);
        mSelectImageButton.setOnTouchListener(mOnTouchListener);

        // Set on increase quantity click listener on plus button
        mPlusButton.setOnClickListener(mOnClickListenerIncreaseQuantity);
        // Set on decrease quantity click listener on minus button
        mMinusButton.setOnClickListener(mOnClickListenerDecreaseQuantity);

        // Set on select image click listener on select image button
        mSelectImageButton.setOnClickListener(mOnClickListenerSelectImage);

        // Get the current product uri passed through intent from MainActivity
        mCurrentProductUri = getIntent().getData();

        // If there is current product uri passed through intent
        if (mCurrentProductUri != null) {
            setTitle(getString(R.string.edit_product));
            // Start loader
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            setTitle(getString(R.string.add_new_product));
        }

        Log.e("TEST", "Initial mProductInputChanged value is: " + mProductInputChanged);
    }

    // On touch listener to listen whether any view has been changed
    // if yes, then we change boolean value of mProductInputChanged to true
    // which indicates the view has been modified
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Log.e("TEST", view + "is touched");
            mProductInputChanged = true;
            return false;
        }
    };

    // On click listener to increase quantity
    private View.OnClickListener mOnClickListenerIncreaseQuantity = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.e("TEST", "plus onclicklistener called");
            mQuantity++;
            mQuantityEditText.setText(String.valueOf(mQuantity));
            mProductInputChanged = true;
            Log.e("TEST", "input has been changed");
            Log.e("TEST", "mQuantity is: " + mQuantity);
        }
    };

    // On click listener to decrease quantity
    private View.OnClickListener mOnClickListenerDecreaseQuantity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e("TEST", "minus onclicklistener called");
            // Make sure the quantity is bigger than 0
            if (mQuantity > 0) {
                mQuantity--;
                mQuantityEditText.setText(String.valueOf(mQuantity));
                mProductInputChanged = true;
                Log.e("TEST", "input has been changed");
            } else if (mQuantity == 0) {
                Toast.makeText(EditorActivity.this,
                        mNameString + getString(R.string.product_out_of_stock),
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    // Open up gallery through intent
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), IMAGE_REQUEST);
    }

    // On click listener to select image
    private View.OnClickListener mOnClickListenerSelectImage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Permission defined in AndroidManifest to allow our app
            // to read external storage
            int permissionCheck = ContextCompat.checkSelfPermission(EditorActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            // If we don't have permission to read external storage
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // Then we request the permission to read and write external storage
                ActivityCompat.requestPermissions(EditorActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
            selectImage();
        }
    };

    // This is invoked after startActivityForResult method been called
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // If there is an intent passed in
            if (intent != null) {
                // Get the data from intent
                mImageUri = intent.getData();
                // Set image uri to image view
                mImageView.setImageURI(mImageUri);
                mImageView.invalidate();
            }
        }
    }

    // This method is invoked when user responds to our app's permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is granted, the result arrays is no longer empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Then we invoke selectImage method
                    // when we get permission to read external storage
                    selectImage();
                }
                return;
            default:
                Toast.makeText(EditorActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        // If user's input hasn't been changed
        if (!mProductInputChanged) {
            // then leave current activity and go back to MainActivity
            super.onBackPressed();
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

    // Save user's input into products table
    private void saveProduct() {
        // Get user's input of product name and remove possible space before of after it
        mNameString = mNameEditText.getText().toString().trim();

        // Get user's input of product supplier and remove possible space before of after it
        mSupplierString = mSupplierEditText.getText().toString().trim();

        // Get user's input of product price
        // Remove possible space before or after it
        String priceString = mPriceEditText.getText().toString().trim();

        // Get user's input of product quantity
        // Remove possible space before or after it
        String quantityString = mQuantityEditText.getText().toString().trim();

        // If no input has been typed, then return
        if (TextUtils.isEmpty(mNameString) && TextUtils.isEmpty(mSupplierString)
                && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString)
                && mImageUri == null) {
            finish();
            return;
        }

        // Show toast message if user's input of name is empty
        if (TextUtils.isEmpty(mNameString)) {
            Toast.makeText(this, getString(R.string.toast_msg_empty_name), Toast.LENGTH_LONG).show();
            return;
        }

        // Show toast message if user's input of supplier is empty
        if (TextUtils.isEmpty(mSupplierString)) {
            Toast.makeText(this, getString(R.string.toast_msg_empty_supplier), Toast.LENGTH_LONG).show();
            return;
        }

        // Show toast message if user's input of price is empty
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.toast_msg_empty_price), Toast.LENGTH_LONG).show();
            return;
        }

        // Show toast message if user's input of quantity is empty
        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.toast_msg_empty_quantity), Toast.LENGTH_LONG).show();
            return;
        }

        // And parse it as int
        mPrice = Double.parseDouble(priceString);
        if (mPrice < 0) {
            Toast.makeText(this, getString(R.string.toast_msg_price_less_than_zero), Toast.LENGTH_LONG).show();
            return;
        }

        // And parse it as int
        mQuantity = Integer.parseInt(quantityString);
        if (mQuantity < 0) {
            Toast.makeText(this, getString(R.string.toast_msg_quantity_less_than_zero), Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, mNameString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, mSupplierString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, mPrice);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, mQuantity);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, String.valueOf(mImageUri));

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
        // Finish current activity and return to MainActivity
        finish();
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
        // Positive button which is "Delete"
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
                if (dialog != null) {
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
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show up dialog
        builder.create().show();
    }

    // Order current product through email or messaging app
    private void orderProduct() {
        //orderIntent.setType("text/plain");
        String productsToOrder = getString(R.string.order_product_message) + "\n\n"
                + getString(R.string.name_hint) + ": " + mNameString + "\n"
                + getString(R.string.quantity_hint) + ": " + mQuantity + "\n"
                + getString(R.string.unit_price_hint) + ": " + mPrice + " " + getString(R.string.price_units_euros) + "\n\n"
                + getString(R.string.best_regards);
        // If we have supplier's email in our database
        // then we order through email
        if (mEmail != null) {
            Intent orderIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "icegirlheidi@gmail.com"));
            orderIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
            orderIntent.putExtra(Intent.EXTRA_TEXT, productsToOrder);
            startActivity(Intent.createChooser(orderIntent, "Order via"));
        } else {
            // Otherwise we order through messaging app
            Intent orderIntent = new Intent(Intent.ACTION_SEND);
            orderIntent.setType("text/plain");
            orderIntent.putExtra(Intent.EXTRA_TEXT, productsToOrder);
            startActivity(Intent.createChooser(orderIntent, "Order via"));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
        // If current product uri is null
        // then it's in adding new product mode
        if (mCurrentProductUri == null) {
            MenuItem menuItemDelete = menu.findItem(R.id.delete_product);
            MenuItem menuItemOrder = menu.findItem(R.id.order_product);
            // Then we don't need the delete product item in menu
            menuItemDelete.setVisible(false);
            menuItemOrder.setVisible(false);

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
                return true;
            case R.id.delete_product:
                // Show dialog for user to confirm deleting
                showDeleteConfirmationDialog();
                // Close current activity and return to MainActivity
                //finish();
                return true;
            case R.id.order_product:
                // Order product
                orderProduct();
                return true;
            case android.R.id.home:
                // If no input has been changed
                if (!mProductInputChanged) {
                    // Then leave current EditorActivity
                    // and go back to MainActivity
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    Log.e("TEST", "Product input hasn't been changed");
                    return true;
                }
                DialogInterface.OnClickListener discardChangeClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("TEST", "Product input has been changed");
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
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_IMAGE
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
            mNameString = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
            mSupplierString = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER));
            mPrice = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
            mQuantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            mImageUri = Uri.parse(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE)));

            // Set the value of name, supplier, price and quantity to edittext view
            mNameEditText.setText(mNameString);
            mSupplierEditText.setText(mSupplierString);
            mPriceEditText.setText(String.valueOf(mPrice));
            mQuantityEditText.setText(String.valueOf(mQuantity));
            mImageView.setImageURI(mImageUri);
            Log.e("TEST", "image uri is: " + mImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Clear and reset all EditText view in EditorActivity
        mNameEditText.setText("");
        mSupplierEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mImageView.setImageURI(null);
    }
}