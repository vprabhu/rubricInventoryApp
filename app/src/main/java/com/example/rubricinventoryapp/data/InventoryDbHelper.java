package com.example.rubricinventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by root on 6/5/17.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Inventory.db";
    private static final int DATABASE_VERSION = 1;


    // constants -- create table
    private static final String CREATE_TABLE = "CREATE TABLE " + InventoryContracts.InventoryEntry.TABLE_NAME +
            "(" + InventoryContracts.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InventoryContracts.InventoryEntry.COLUMN_ITEM_NAME + " TEXT, "
            + InventoryContracts.InventoryEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL, "
            + InventoryContracts.InventoryEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL, "
            + InventoryContracts.InventoryEntry.COLUMN_ITEM_PICTURE + " TEXT, "
            + InventoryContracts.InventoryEntry.COLUMN_ITEM_SOLD + " INTEGER NOT NULL, "
            + InventoryContracts.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME + " TEXT, "
            + InventoryContracts.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE + " TEXT );";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
