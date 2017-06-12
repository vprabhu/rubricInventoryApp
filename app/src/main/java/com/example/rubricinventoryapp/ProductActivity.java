package com.example.rubricinventoryapp;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rubricinventoryapp.adapter.ProductAdapter;
import com.example.rubricinventoryapp.data.InventoryContracts;

public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProductAdapter mProductAdapter;
    private ListView mProductListView;
    private TextView mAbsenceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getResources().getString(R.string.app_name));

        // listView to hold the products
        mProductListView = (ListView) findViewById(R.id.product_listView);
        mAbsenceTextView = (TextView) findViewById(R.id.textView_absence);

        mProductListView.setEmptyView(mAbsenceTextView);
        mAbsenceTextView.setText(getResources().getString(R.string.info_absence_text));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                mProductListView.setVisibility(View.GONE);
                mAbsenceTextView.setVisibility(View.VISIBLE);
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
            }
        }
        mProductAdapter = new ProductAdapter(
                ProductActivity.this ,
                null);
        //set the adapter to listview
        mProductListView.setAdapter(mProductAdapter);

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
        if(data != null){
            mProductAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductAdapter.swapCursor(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // create a cursor adapter with cursor
                mProductListView.setVisibility(View.GONE);
                mAbsenceTextView.setVisibility(View.VISIBLE);
            }else {
                Toast.makeText(ProductActivity.this, getResources().getString(R.string.info_need_access) , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_product:
                // starts the new activity to insert the new product
                startActivity(new Intent(ProductActivity.this , InsertProductActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
