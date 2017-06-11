package com.example.rubricinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.rubricinventoryapp.R;

public class InventoryProvider extends ContentProvider {

    /** Tag for log messages */
    private static final String LOG_TAG = "InventoryProvider";

    /** {@link InventoryDbHelper} create object */
    private InventoryDbHelper mInventoryDbHelper;

    /** URI matcher code for the content URI for the products table */
    private static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single product in the products table */
    private static final int PRODUCT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(InventoryContracts.CONTENT_AUTHORITY , InventoryContracts.PATH_INVENTORY , PRODUCTS);
        sUriMatcher.addURI(InventoryContracts.CONTENT_AUTHORITY , InventoryContracts.PATH_INVENTORY+"/#" , PRODUCT_ID);
    }
    public InventoryProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mInventoryDbHelper.getWritableDatabase();
        int rowsDeleted = 0;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted =database.delete(InventoryContracts.InventoryEntry.TABLE_NAME , selection , selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryContracts.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted=  database.delete(InventoryContracts.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
        }
        // notifies the cursor with the related entries
        if (rowsDeleted>0){
            getContext().getContentResolver().notifyChange(InventoryContracts.InventoryEntry.CONTENT_URI , null);
        }

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return InventoryContracts.InventoryEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return InventoryContracts.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return insertProduct(uri , values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Initialize the provider and database class objects
     */
    @Override
    public boolean onCreate() {
        mInventoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mInventoryDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // For the PRODUCTS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the inventory table.
                cursor = database.query(InventoryContracts.InventoryEntry.TABLE_NAME ,
                        projection ,
                        selection ,
                        selectionArgs ,
                        null ,
                        null,
                        sortOrder);
                break;
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.rubricinventoryapp/inventory/5",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 5 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = InventoryContracts.InventoryEntry._ID+ "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the inventory table where the _id equals 5 to return a
                // Cursor containing that row of the table.
                cursor = database.query(InventoryContracts.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // notifies the uploaded cursor to listeners
        cursor.setNotificationUri(getContext().getContentResolver() , InventoryContracts.InventoryEntry.CONTENT_URI);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryContracts.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    /**
     * Update product in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more product).
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase mSqLiteDatabase = mInventoryDbHelper.getWritableDatabase();

        int rows = mSqLiteDatabase.update(InventoryContracts.InventoryEntry.TABLE_NAME , values , selection , selectionArgs);
        // notifies the cursor with the related entries
        if(rows!=0){
            getContext().getContentResolver().notifyChange(InventoryContracts.InventoryEntry.CONTENT_URI , null);
        }
        return rows;
    }

    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(InventoryContracts.InventoryEntry.COLUMN_ITEM_NAME);
        if (name == null|| name.isEmpty()) {
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.info_error_name));
        }

        String quantity = values.getAsString(InventoryContracts.InventoryEntry.COLUMN_ITEM_QUANTITY);
        if(quantity ==null || quantity.isEmpty()){
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.info_error_quantity));
        }

        String price  = values.getAsString(InventoryContracts.InventoryEntry.COLUMN_ITEM_PRICE);
        if (price==null || price.isEmpty()){
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.info_error_price));
        }

        String picture = values.getAsString(InventoryContracts.InventoryEntry.COLUMN_ITEM_PICTURE);
        if (picture==null || picture.isEmpty()){
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.info_error_image));
        }

        String supplierName  = values.getAsString(InventoryContracts.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME);
        if (supplierName==null || supplierName.isEmpty()){
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.info_error_supplier_name));
        }

        String supplierPhone  = values.getAsString(InventoryContracts.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE);
        if (supplierPhone==null || supplierPhone.isEmpty()|| supplierPhone.length()<10){
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.info_error_supplier_phone));
        }

        SQLiteDatabase mSqLiteDatabase = mInventoryDbHelper.getWritableDatabase();

        long id = mSqLiteDatabase.insert(InventoryContracts.InventoryEntry.TABLE_NAME , null , values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // notifies the cursor with the related entries
        getContext().getContentResolver().notifyChange(InventoryContracts.InventoryEntry.CONTENT_URI , null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }
}
