package edu.sjsu.android.project4yingyingzhao;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap mMap;
    // TODO: set up following class attributes
    // Uri: get from LocationsProvider
    private final Uri CONTENT_URI = LocationsProvider.uri;

    // 2 LatLng for SJSU and CS department (given in exercise 6, or you can find those yourself)
    private final LatLng LOCATION_UNIV = new LatLng(37.335371, -121.881050);
    private final LatLng LOCATION_CS = new LatLng(37.333714, -121.881860);
    // (Extra credit) SharedPreferences and KEYs needed
    private SharedPreferences sharedPreference;
    private final String PREFERENCE_NAME = "edu.sjsu.android.sharedpreference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // TODO: attach listeners to the buttons
        findViewById(R.id.city).setOnClickListener(this::switchView);
        findViewById(R.id.univ).setOnClickListener(this::switchView);
        findViewById(R.id.cs).setOnClickListener(this::switchView);
        findViewById(R.id.location).setOnClickListener(this::getLocation);
        findViewById(R.id.uninstall).setOnClickListener(this::uninstall);

        // TODO: retrieve and draw already saved locations in map
        // Hint: a method in LoaderManager
        LoaderManager.getInstance(this).restartLoader(0, null, this);

        // TODO: extra credit - restore the map setting
        //  (camara position & map type) using SharedPreferences

    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreference = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = sharedPreference.edit();
        preferencesEditor.putInt("map_type", mMap.getMapType());
        double latitude = mMap.getCameraPosition().target.latitude;
        double longitude = mMap.getCameraPosition().target.longitude;
        float zoom_level = mMap.getCameraPosition().zoom;
        preferencesEditor.putFloat("latitude", (float) latitude);
        preferencesEditor.putFloat("longitude", (float)longitude);
        preferencesEditor.putFloat("zoom", zoom_level);
        preferencesEditor.apply();
        LoaderManager.getInstance(this).destroyLoader(0);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // TODO: extra credit - restore the map setting
//        sharedPreference = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        if(sharedPreference.contains("map_type")) {
//            int mapType = sharedPreference.getInt("map_type", 0);
//            double lat = sharedPreference.getFloat("latitude", 1f);
//            double lon = sharedPreference.getFloat("longitude", 1f);
//            float zoom = sharedPreference.getFloat("zoom", 1f);
//
//            mMap.setMapType(mapType);
//            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), zoom);
//            mMap.animateCamera(update);
//        }

        mMap.setOnMapClickListener(point -> {
            // TODO: insert the LatLng point to the database on click
            // (You may consider put the code in an private helper method)
            // 1) Add a maker on the point to the map
            // 2) Store the latitude, longitude and zoom level of the point to SQLite database using ContentValues
            // 3) Call execute(ContentValues) on a MyTask (defined below) object to add a point
            ContentValues values = new ContentValues();
            values.put("latitude", point.latitude);
            values.put("longitude", point.longitude);
            values.put("zoom_level", mMap.getCameraPosition().zoom);
            new MyTask().execute(values);
            mMap.addMarker(new MarkerOptions().position(point));
        });

        mMap.setOnMapLongClickListener(point -> {
            // TODO: delete all locations from the database on long click
            // (You may consider put the code in an private helper method)
            // 1) Clear all markers from the Google Map
            // 2) Call execute() on a MyTask (defined below) object to clear the database
            // 3) Toast a message "All makers are removed"
            mMap.clear();
            new MyTask().execute((ContentValues) null);
            Toast.makeText(this, "All makers are removed",
                    Toast.LENGTH_LONG).show();
        });
    }


    // Below is the class that extend AsyncTask, to insert/delete data in background
    // Note that AsyncTask is deprecated from API 30, but you can still use it.
    // You can use java.util.concurrent instead, if you are familiar with threads and concurrency.
    private class MyTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {
            // TODO: insert one row or delete all data base on input
            // Hint: if the first contentValues is not null, insert it
            // Otherwise, delete all.
            // Both operations should be done through content provider

            if(contentValues[0] != null) {
                getContentResolver().insert(CONTENT_URI, contentValues[0]);
            } else {
                getContentResolver().delete(CONTENT_URI, null, null);
            }

            return null;
        }
    }
    // ----- End of AsyncTask classes -----


    // Below are for the CursorLoader, that is, the methods of
    // LoaderManager.LoaderCallbacks<Cursor> interface
    /**
     * Instantiate and return a new Loader for the database.
     *
     * @param id   the ID whose loader is to be created
     * @param args any arguments supplied by the caller
     * @return a new Loader instance that is ready to start loading
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // TODO: load the cursor that's pointing to the database
        // Hint: return a CursorLoader object with this context
        // and the URI (set other parameters to null)
        Log.i("pitao3", "test loader");
        return new CursorLoader(this, CONTENT_URI, null, null, null, null);
    }

    /**
     * Draw the markers after data is loaded, that is, the loader returned
     * in the onCreateLoader has finished its load.
     *
     * @param loader the Loader that has finished
     * @param cursor a cursor to read the data generated by the Loader
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // TODO: display markers based on the database using cursor
        // First, get data row by row using the cursor
        // For each row, get the latitude, longitude and zoom level
        // Draw a marker based on the LatLng object on the map
        // After getting all data, move the "camera" to focus on the last clicked location
        // Also the zoom level should be the same as the last clicked location
        if (cursor != null && cursor.moveToFirst()) {
            // Retrieve data row by row
            LatLng location;
            float zoom;
            do {
                double latitude = cursor.getDouble(1);
                double longitude = cursor.getDouble(2);
                location = new LatLng(latitude, longitude);

                mMap.addMarker(new MarkerOptions().position(location));
                zoom = cursor.getFloat(cursor.getColumnCount() - 1);
            } while (cursor.moveToNext()) ;

//            CameraUpdate update =  CameraUpdateFactory.newLatLngZoom(location, zoom);
//            mMap.animateCamera(update);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // No need to implement
    }
    // ----- End of LoaderManager.LoaderCallbacks<Cursor> methods -----

    // Below are the methods respond to clicks on buttons
    // Remember to attach them to the buttons in onCreate
    // getLocation and switchView are the same as the ones in exercise 6.
    public void uninstall(View view) {
        // TODO: uninstall the app
        Intent delete = new Intent(Intent.ACTION_DELETE,
                Uri.parse("package:" + getPackageName()));
        startActivity(delete);
    }

    public void getLocation(View view) {
        // TODO: get the current location
        // Remember to check if the location is enabled
        // and ask for permissions
        // Can implement GPSTracker class to do all these.
        GPSTracker tracker = new GPSTracker(this);
        tracker.getLocation();
    }

    public void switchView(View view) {
        // TODO: switch between different views based on the button being clicked
        CameraUpdate update = null;
        if (view.getId() == R.id.city) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 10f);
        } else if (view.getId() == R.id.univ) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 14f);
        } else if (view.getId() == R.id.cs) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            update = CameraUpdateFactory.newLatLngZoom(LOCATION_CS, 18f);
        }
        mMap.animateCamera(update);
    }
}