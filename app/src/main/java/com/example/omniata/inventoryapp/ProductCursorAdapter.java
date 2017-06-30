package com.example.omniata.inventoryapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omniata.inventoryapp.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    public static class Holder {
        TextView mNameTextView;
        TextView mPriceTextView;
        TextView mQuantityTextView;
        ImageButton mSaleButton;

        public Holder(View view) {
            mNameTextView = (TextView) view.findViewById(R.id.list_item_name);
            mPriceTextView = (TextView) view.findViewById(R.id.list_item_unit_price);
            mQuantityTextView = (TextView) view.findViewById(R.id.list_item_quantity);
            mSaleButton = (ImageButton) view.findViewById(R.id.sale_button);
        }
    }

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        Holder holder = new Holder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        final Holder holder = (Holder) view.getTag();
        final long id;

        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        id = cursor.getLong(cursor.getColumnIndex(ProductEntry._ID));

        holder.mNameTextView.setText(name);
        holder.mPriceTextView.setText(String.valueOf(price));
        holder.mQuantityTextView.setText(String.valueOf(quantity));

        // Click sale button to decrease quantity
        holder.mSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get current value of quantity in quantity TextView
                int currentQuantity = Integer.parseInt(holder.mQuantityTextView.getText().toString().trim());
                // Do this if quantity is larger than zero
                if (currentQuantity > 0) {
                    // Decrease quantity by one
                    currentQuantity--;
                    // Set the decreased value of quantity to quantity TextView
                    holder.mQuantityTextView.setText(String.valueOf(currentQuantity));
                    // Create new ContentValue
                    ContentValues values = new ContentValues();
                    // Connect current quantity with quantity column
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, currentQuantity);
                    // Select column with id = ?
                    String selection = ProductEntry._ID + "=?";
                    // id of current product
                    String[] selectionArgs = new String[]{String.valueOf(id)};
                    // Update database
                    view.getContext().getContentResolver().update(
                            ProductEntry.CONTENT_URI,
                            values,
                            selection,
                            selectionArgs);
                } else {
                    String currentName = holder.mNameTextView.getText().toString().trim();
                    // Otherwise show toast message
                    Toast.makeText(view.getContext(), currentName + " out of stock", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
