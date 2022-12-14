package edu.sjsu.android.project4yingyingzhao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

/**
 * The class is a content provider where the underlying database is LocationsDB.
 */
public class LocationsProvider extends ContentProvider {

    // TODO: provide a public static Uri
    // TODO: a private LocationsDB object
    public static Uri uri = Uri.parse("content://" + "edu.sjsu.android.project4yingyingzhao");
    private LocationsDB database;

    @Override
    public boolean onCreate() {
        // TODO: construct the LocationDB object
        database = new LocationsDB(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO: call deleteAll() from the LocationDB object
        return database.deleteAll();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: use insert(values) from the LocationDB object to insert the value
        // And there other steps you need to do to insert a value
        // Refer to exercise 5 or lesson 18 page 11 if needed

        long rowID = database.insert(values);
        //If record is added successfully
        if (rowID > 0){
            Uri _uri = ContentUris.withAppendedId(uri, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: call getAllLocations() from the LocationDB object
        return database.getAllLocations();
    }

    // -----------
    // Following methods won't be used in this project
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}