package com.example.rubricinventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.rubricinventoryapp.adapter.ProductAdapter;
import com.example.rubricinventoryapp.data.InventoryContracts;

public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProductAdapter mProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // listView to hold the products
        ListView mProductListView = (ListView) findViewById(R.id.product_listView);

        // create a cursor adapter with cursor
        mProductAdapter = new ProductAdapter(
                ProductActivity.this ,
                null);
        //set the adapter to listview
        mProductListView.setAdapter(mProductAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // starts the new activity to insert the new product
                startActivity(new Intent(ProductActivity.this , InsertProductActivity.class));
            }
        });

        // intialize the loader to handle the listview data
        getLoaderManager().initLoader(1 , null , ProductActivity.this);

        mProductListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent(ProductActivity.this , DetailsActivity.class);
                // gets the current cursor
                Cursor mProduct = (Cursor) mProductAdapter.getItem(position);
                // gets the current selected ID
                int rowId = mProduct.getInt(mProduct.getColumnIndex(InventoryContracts.InventoryEntry._ID));
                // creates the query URI for the selected item
                Uri currentUri = ContentUris.withAppendedId(InventoryContracts.InventoryEntry.CONTENT_URI , rowId);
                mIntent.setData(currentUri);
                // starts the detail activity
                startActivity(mIntent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {
                InventoryContracts.InventoryEntry._ID ,
                InventoryContracts.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContracts.InventoryEntry.COLUMN_ITEM_QUANTITY ,
                InventoryContracts.InventoryEntry.COLUMN_ITEM_SOLD ,
                InventoryContracts.InventoryEntry.COLUMN_ITEM_PRICE ,
                InventoryContracts.InventoryEntry.COLUMN_ITEM_PICTURE
        };
        return new CursorLoader(
                ProductActivity.this ,
                InventoryContracts.InventoryEntry.CONTENT_URI ,
                projection ,
                null ,
                null ,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProductAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductAdapter.swapCursor(null);
    }
}
