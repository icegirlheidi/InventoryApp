package com.example.omniata.inventoryapp;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omniata.inventoryapp.data.ProductContract.ProductEntry;

import org.w3c.dom.Text;

public class ProductCursorAdapter extends CursorAdapter {

    static class Holder {
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

        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));

        holder.mNameTextView.setText(name);
        holder.mPriceTextView.setText(String.valueOf(price));
        holder.mQuantityTextView.setText(String.valueOf(quantity));

        holder.mSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = Integer.parseInt(holder.mQuantityTextView.getText().toString().trim());
                currentQuantity--;
                holder.mQuantityTextView.setText(String.valueOf(currentQuantity));

                ContentValues values = new ContentValues();
                values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, currentQuantity);

                String selection = ProductEntry._ID + "=?";
                long id = cursor.getLong(cursor.getColumnIndex(ProductEntry._ID));
                String[] selectionArgs = new String[]{String.valueOf(id)};
                Toast.makeText(view.getContext(), "Clicked item position: " + cursor.getPosition(), Toast.LENGTH_SHORT);
                view.getContext().getContentResolver().update(
                        ProductEntry.CONTENT_URI,
                        values,
                        selection,
                        selectionArgs);
            }
        });
    }
}
