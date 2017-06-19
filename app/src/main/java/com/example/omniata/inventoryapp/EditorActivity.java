package com.example.omniata.inventoryapp;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

public class EditorActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
