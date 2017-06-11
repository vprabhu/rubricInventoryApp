package com.example.rubricinventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.rubricinventoryapp.data.InventoryContracts;

public class InsertProductActivity extends AppCompatActivity {

    private static final String TAG = InsertProductActivity.class.getSimpleName();
    private static int RESULT_LOAD_IMG = 1;

    private EditText mProductNameEditText;
    private EditText mProductQuantityEditText;
    private EditText mProductPriceEditText;
    private EditText mProductSupplierNameEditText;
    private EditText mProductSupplierPhoneEditText;
    private ImageView mProductImageView;
    private String mSelectedSdcardPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);

        // sets the page title
        getSupportActionBar().setTitle(getResources().getString(R.string.info_insert_product_title));

        /* UI type casting */
        mProductNameEditText = (EditText) findViewById(R.id.editText_product_name);
        mProductQuantityEditText = (EditText) findViewById(R.id.editText_product_quantity);
        mProductPriceEditText = (EditText) findViewById(R.id.editText_product_price);
        mProductSupplierNameEditText = (EditText) findViewById(R.id.editText_product_supplier_name);
        mProductSupplierPhoneEditText = (EditText) findViewById(R.id.editText_product_supplier_phone);
        mProductImageView = (ImageView) findViewById(R.id.imageView_product);
        Button mSelectImageButton = (Button) findViewById(R.id.button_insert_product_image);
        mSelectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromGallery();
            }
        });

        findViewById(R.id.fab_add_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gets the input from edittext and stores it in string
                String name = mProductNameEditText.getText().toString();
                String quantity = mProductQuantityEditText.getText().toString();
                String price = mProductPriceEditText.getText().toString();
                String supplierName = mProductSupplierNameEditText.getText().toString();
                String supplierPhone = mProductSupplierPhoneEditText.getText().toString();

                // content values to insert the data
                ContentValues mContentValues = new ContentValues();
                mContentValues.put(InventoryContracts.InventoryEntry.COLUMN_ITEM_NAME , name);
                mContentValues.put(InventoryContracts.InventoryEntry.COLUMN_ITEM_QUANTITY , quantity);
                mContentValues.put(InventoryContracts.InventoryEntry.COLUMN_ITEM_PRICE , price);
                mContentValues.put(InventoryContracts.InventoryEntry.COLUMN_ITEM_PICTURE , mSelectedSdcardPath);
                mContentValues.put(InventoryContracts.InventoryEntry.COLUMN_ITEM_SOLD , 0);
                mContentValues.put(InventoryContracts.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME , supplierName);
                mContentValues.put(InventoryContracts.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE , supplierPhone);

                try{
                    // insert method to add the content values to db via content provider
                    Uri InsertedUri = getContentResolver().insert(InventoryContracts.InventoryEntry.CONTENT_URI , mContentValues);
                    Log.d(TAG, "onClick: " + InsertedUri);
                }catch (IllegalArgumentException e){
                    Toast.makeText(InsertProductActivity.this , e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                cursor.close();
                // sets the bitmap into imageview
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                mSelectedSdcardPath = String.valueOf(selectedImage);
                mProductImageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
