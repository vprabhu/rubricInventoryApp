package com.example.rubricinventoryapp;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rubricinventoryapp.data.InventoryContracts;

import java.io.IOException;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mSingleQueryUri;
    private TextView mProductNameTextView;
    private TextView mProductQuantityTextView;
    private TextView mProductPriceTextView;
    private TextView mProductSupplierNameTextView;
    private TextView mProductSupplierPhoneTextView;
    private TextView mProductSoldTextView;
    private ImageView mProductImageView;
    private int mQuantity = 0;
    private int mRowId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // sets the back button to the activity
        getSupportActionBar().setHomeButtonEnabled(true);

        // get the data from CatalogActivity
        mSingleQueryUri = getIntent().getData();

        // loader to load the data into views
        getLoaderManager().initLoader(1, null, DetailsActivity.this);


        // ui typecasting
        mProductNameTextView = (TextView) findViewById(R.id.textView_detaisl_product_name);
        mProductQuantityTextView = (TextView) findViewById(R.id.textView_details_product_total_quantity);
        mProductPriceTextView = (TextView) findViewById(R.id.textView_details_product_single_price);
        mProductSupplierNameTextView = (TextView) findViewById(R.id.textView_product_supplier_details_name);
        mProductSupplierPhoneTextView = (TextView) findViewById(R.id.textView_product_supplier_details_phone);
        mProductImageView = (ImageView) findViewById(R.id.imageView_details_product);
        mProductSoldTextView = (TextView) findViewById(R.id.textView_details_product_sold_item);
        Button mIncreaseQuantityButton = (Button) findViewById(R.id.button_details_quantity_add);
        Button mDecreaseQuantityButton = (Button) findViewById(R.id.button_details_quantity_subtract);

        mProductSupplierPhoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // starts the gallery activity
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mProductSupplierPhoneTextView.getText().toString()));
                if (ActivityCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });

        mDecreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // decreases the quantity and if quantity becomes 1 alerts the user to coontact supplier
                if(mQuantity > 1){
                    mQuantity  = mQuantity -1 ;
                    String totalQuantity = getResources().getString(R.string.info_total_quantity) + mQuantity;
                    mProductQuantityTextView.setText(totalQuantity);
                }else{
                    Toast.makeText(DetailsActivity.this , getResources().getString(R.string.info_contact_supplier) , Toast.LENGTH_SHORT).show();
                }
            }
        });

        mIncreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // increases the quantity
                mQuantity = mQuantity + 1;
                String totalQuantity = getResources().getString(R.string.info_total_quantity) + mQuantity;
                mProductQuantityTextView.setText(totalQuantity);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // updates the altered quantity to db via content provider
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(InventoryContracts.InventoryEntry.COLUMN_ITEM_QUANTITY , mQuantity);
        Uri currentUri = ContentUris.withAppendedId(InventoryContracts.InventoryEntry.CONTENT_URI , mRowId);
        getContentResolver().update(
                currentUri , mContentValues , null , null );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {
                InventoryContracts.InventoryEntry._ID ,
                InventoryContracts.InventoryEntry.COLUMN_ITEM_NAME ,
                InventoryContracts.InventoryEntry.COLUMN_ITEM_QUANTITY ,
                InventoryContracts.InventoryEntry.COLUMN_ITEM_PRICE ,
                InventoryContracts.InventoryEntry.COLUMN_ITEM_PICTURE ,
                InventoryContracts.InventoryEntry.COLUMN_ITEM_SOLD ,
                InventoryContracts.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME ,
                InventoryContracts.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE
        };
        return new CursorLoader(DetailsActivity.this ,
                mSingleQueryUri,
                projection ,
                null ,
                null ,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // moves the cursor to position 1 and fills the data into views
        if(data.moveToFirst()){
            mRowId = data.getInt(data.getColumnIndex(InventoryContracts.InventoryEntry._ID));
            String name = data.getString(data.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_NAME));
            mQuantity = data.getInt(data.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_QUANTITY));
            String price = data.getString(data.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_PRICE));
            int soldItems = data.getInt(data.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_SOLD));
            String supplierName = data.getString(data.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME));
            String supplierPhone = data.getString(data.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE));
            mProductNameTextView.setText(name);
            String totalQuantity = getResources().getString(R.string.info_total_quantity) + mQuantity;
            String indPrice = getResources().getString(R.string.info_indiv_price) + price;
            mProductPriceTextView.setText(indPrice);
            mProductQuantityTextView.setText(totalQuantity);
            mProductSupplierNameTextView.setText(supplierName);
            mProductSupplierPhoneTextView.setText(supplierPhone);
            String soldItemsDisplay = getResources().getString(R.string.info_sold_items) + soldItems;
            mProductSoldTextView.setText(soldItemsDisplay);
            // sets the product name as title
            getSupportActionBar().setTitle(name);
            String mImagePath = data.getString(data.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_PICTURE));
            loadImageFromStorage(mImagePath);
        }
    }

    /**
     * loads the image from sdcard to imageview
     * @param path image path stored in sdcard
     */
    private void loadImageFromStorage(String path){
        Uri mUri = Uri.parse(path);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),mUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mProductImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameTextView.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // close the current activity
                finish();
                break;
            case R.id.menu_delete_product:
                // deletes the current product from Database via contentprovider
                Uri currentUri = ContentUris.withAppendedId(InventoryContracts.InventoryEntry.CONTENT_URI , mRowId);
                int rowsDeleted = getContentResolver().delete(currentUri , null ,null);
                if(rowsDeleted == 1){
                    finish();
                }else {
                    Toast.makeText(
                            DetailsActivity.this ,
                            getResources().getString(R.string.info_delete_failure_message) ,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details , menu);
        return super.onCreateOptionsMenu(menu);
    }
}
