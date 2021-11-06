package edu.sjsu.android.project4yingyingzhao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * A class for a SQLite database of locations.
 */
class LocationsDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "locationsDatabase";
    private static final String TABLE_NAME = "locations";
    private static final int VERSION = 1;

    // TODO: 4 Strings (protected static final) for 4 column names
    protected static final String ID = "_id";
    protected static final String LATITUDE = "latitude";
    protected static final String LONGITUDE = "longitude";
    protected static final String ZOOM_LEVEL = "zoom_level";

    public LocationsDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // TODO: execute the SQL statement to create the table
        String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME +
                        " ("+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + LATITUDE + " TEXT NOT NULL, "
                        + LONGITUDE + " TEXT NOT NULL, "
                        + ZOOM_LEVEL + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /**
     * A method that inserts a new location to the table.
     *
     * @param contentValues location detail
     * @return the id
     */
    public long insert(ContentValues contentValues) {
        // TODO: insert the values to the table
        SQLiteDatabase database = getWritableDatabase();
        return database.insert(TABLE_NAME, null, contentValues);
    }

    /**
     * A method that deletes all locations from the table.
     *
     * @return number of locations deleted
     */
    public int deleteAll() {
        // TODO: delete all data from the table
        SQLiteDatabase database = this.getWritableDatabase();
        int count = (int) DatabaseUtils.queryNumEntries(database, TABLE_NAME);
        database.execSQL("DELETE FROM " + TABLE_NAME);
        return count;
    }

    /**
     * A method that returns all the locations from the table.
     *
     * @return Cursor
     */
    public Cursor getAllLocations() {
        // TODO: query all data from the table
        SQLiteDatabase database = getWritableDatabase();
        return database.query(TABLE_NAME, new String[]{ID, LATITUDE, LONGITUDE, ZOOM_LEVEL},
                null, null, null, null, null);
    }
}