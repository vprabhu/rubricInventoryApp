package com.example.rubricinventoryapp.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rubricinventoryapp.R;
import com.example.rubricinventoryapp.data.InventoryContracts;

import java.io.IOException;

/**
 * Created by root on 6/8/17.
 */

public class ProductAdapter extends CursorAdapter {

    private Context mContext;

    public ProductAdapter(@NonNull Context context,Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemView = LayoutInflater.from(context).inflate(R.layout.layout_product_item,parent , false);
        return listItemView;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        TextView mNameTextView= (TextView) view.findViewById(R.id.textView_row_product_name);
        final TextView  mQuantityTextView = (TextView) view.findViewById(R.id.textView_row_product_quantity);
        final TextView  mSoldTextView = (TextView) view.findViewById(R.id.textView_row_product_items_sold);
        TextView  mPriceTextView = (TextView) view.findViewById(R.id.textView_row_product_price);
        ImageView  mImageTextView = (ImageView) view.findViewById(R.id.imageView_row_product_image);
        // get the data from cursor
        String name = cursor.getString(cursor.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_NAME));
        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_QUANTITY));
        int soldItems = cursor.getInt(cursor.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_SOLD));
        String price = cursor.getString(cursor.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_PRICE));
        String picture = cursor.getString(cursor.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_PICTURE));
        // fill the data to UI
        mNameTextView.setText(name);
        String quantityDisplay = context.getResources().getString(R.string.info_placeholder_qty)+quantity;
        String soldItemsDisplay = context.getResources().getString(R.string.info_placeholder_qty)+soldItems;
        String priceDisplay = context.getResources().getString(R.string.info_placeholder_qty)+price;
        mQuantityTextView.setText(quantityDisplay);
        mSoldTextView.setText(soldItemsDisplay);
        mPriceTextView.setText(priceDisplay);
        loadImageFromStorage(picture , mImageTextView);
        Button mSellButton = (Button) view.findViewById(R.id.button_row_sell_item);
        mSellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=(Integer) view.getTag();
                if(cursor.moveToPosition(position)){
                    int quantityDisplay = cursor.getInt(cursor.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_QUANTITY));
                    int sold = cursor.getInt(cursor.getColumnIndex(InventoryContracts.InventoryEntry.COLUMN_ITEM_SOLD));
                    if(quantityDisplay > 1){
                        quantityDisplay  = quantityDisplay -1 ;
                        sold = sold + 1;
                        ContentValues mContentValues = new ContentValues();
                        mContentValues.put(InventoryContracts.InventoryEntry.COLUMN_ITEM_QUANTITY , quantityDisplay);
                        mContentValues.put(InventoryContracts.InventoryEntry.COLUMN_ITEM_SOLD , sold);
                        int rowId = cursor.getInt(cursor.getColumnIndex(InventoryContracts.InventoryEntry._ID));
                        Uri currentUri = ContentUris.withAppendedId(InventoryContracts.InventoryEntry.CONTENT_URI , rowId);
                        context.getContentResolver().update(
                                currentUri , mContentValues , null , null );
                        mQuantityTextView.setText(context.getResources().getString(R.string.info_placeholder_qty)+String.valueOf(quantityDisplay));
                        mSoldTextView.setText(context.getResources().getString(R.string.info_placeholder_sold)+String.valueOf(sold));
                    }else{
                        Toast.makeText(context , context.getResources().getString(R.string.info_contact_supplier) , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public View getView(int position, View convertview, ViewGroup arg2) {
        if (convertview == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertview = inflater.inflate(R.layout.layout_product_item,
                    null);
        }
        convertview.setTag(position);
        return super.getView(position, convertview, arg2);
    }

    /**
     * loads the image stored in sdcard into imageview
     * @param path sdcard image path
     * @param mProductImageView imageview to load the image
     */
    private void loadImageFromStorage(String path , ImageView mProductImageView){
        Uri mUri = Uri.parse(path);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),mUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mProductImageView.setImageBitmap(bitmap);
    }
}