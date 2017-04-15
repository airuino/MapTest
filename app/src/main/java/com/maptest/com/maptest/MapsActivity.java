package com.maptest.com.maptest;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,LoaderManager.LoaderCallbacks<Cursor> {

    private ViewGroup infoWindow;
    private TextView infoTitle;
    private OnInfoWindowElemTouchListener DetailsButtonListener;
    private OnInfoWindowElemTouchListener AddButtonListener;
    private GoogleMap map;
    private MapWrapperLayout mapWrapperLayout;
    private DBconnections db=new DBconnections(this);
    private EditText location_tf =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
        mapFragment.getMapAsync(this);

        //Set up Tabs
        SetUpTaps();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout.init(map, getPixelsFromDp(this, 0));

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.info_window, null);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        Button add = (Button) infoWindow.findViewById(R.id.Add);
        Button locationDetails = (Button) infoWindow.findViewById(R.id.LocationDetails);


        // Add button
        this.AddButtonListener = new OnInfoWindowElemTouchListener(add)
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker)
            {
                // Here we can perform some action triggered after clicking the button

                ContentValues contentValues = new ContentValues();
                // get  & set with contentvalues
                LatLng position = marker.getPosition();
                contentValues.put(DBconnections.FIELD_LAT, position.latitude);
                contentValues.put(DBconnections.FIELD_LNG, position.longitude);
                contentValues.put(DBconnections.FIELD_ZOOM, map.getCameraPosition().zoom);


                // Creating an instance of LocationInsertTask
               LocationInsertTask insertTask = new LocationInsertTask();

                // Storing the latitude, longitude and zoom level to SQLite database
                insertTask.execute(contentValues);

                Toast.makeText(getBaseContext(), marker.getTitle()+" Has been added ", Toast.LENGTH_SHORT).show();

            }
        };


        //Details  button
        this.DetailsButtonListener = new OnInfoWindowElemTouchListener(locationDetails)
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                Toast.makeText(MapsActivity.this, marker.getTitle() + " details", Toast.LENGTH_SHORT).show();

            }
        };

        add.setOnTouchListener(AddButtonListener);
        locationDetails.setOnTouchListener(DetailsButtonListener);

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());
                AddButtonListener.setMarker(marker);
                DetailsButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        location_tf = (EditText) findViewById(R.id.TFaddress);
        location_tf.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            sendMessage();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        // Invoke LoaderCallbacks to retrieve and draw already saved locations in map
        getLoaderManager().initLoader(1, null, this);
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    //draw marker
    public void drawMarker(LatLng latLng,String knownNama)
    {
        //String knownName =getLocationName(latLng);
        // Let's add a couple of markers
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(knownNama));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));
    }


    //this method call when retrive locations from database
    public void drawVistedLocation(LatLng latLng)
    {
        String knownName = getLocationName(latLng); // Only if available else return NULL
        // Let's add a couple of markers
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(knownName)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.visitedicon3)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));
    }

    //Get Location Name method
    public String getLocationName(LatLng point)
    {
        String knownName = "";
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(this);

        try {
            addressList = geocoder.getFromLocation(point.latitude, point.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();}

        knownName = addressList.get(0).getFeatureName();

        return knownName;
    }


    //method call when search on map
    public void sendMessage()
    {
        String location = location_tf.getText().toString();
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);


            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            String knownNama=addressList.get(0).getFeatureName();
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            drawMarker(latLng,knownNama);

        }
    }



    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {

            /** Setting up values to insert the clicked location into SQLite database */
            getContentResolver().insert(LocationsContentProvider.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    private class LocationDeleteTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {

            /** Deleting all the locations stored in SQLite database */
            getContentResolver().delete(LocationsContentProvider.CONTENT_URI, null, null);
            return null;
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int arg0,
                                         Bundle arg1) {

        // Uri to the content provider LocationsContentProvider
        Uri uri = LocationsContentProvider.CONTENT_URI;

        // Fetches all the rows from locations table
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0,
                               Cursor arg1) {
        int locationCount = 0;
        double lat=0;
        double lng=0;
        float zoom=0;

        // Number of locations available in the SQLite database table
        locationCount = arg1.getCount();

        // Move the current record pointer to the first row of the table
        arg1.moveToFirst();

        for(int i=0;i<locationCount;i++){

            // Get the latitude
            lat = arg1.getDouble(arg1.getColumnIndex(db.FIELD_LAT));

            // Get the longitude
            lng = arg1.getDouble(arg1.getColumnIndex(db.FIELD_LNG));

            // Get the zoom level
            zoom = arg1.getFloat(arg1.getColumnIndex(db.FIELD_ZOOM));

            // Creating an instance of LatLng to plot the location in Google Maps
            LatLng location = new LatLng(lat, lng);
            // Drawing the marker in the Google Maps
            drawVistedLocation(location);

            // Traverse the pointer to the next row
            arg1.moveToNext();
            Toast.makeText(MapsActivity.this, " i="+i, Toast.LENGTH_SHORT).show();

        }

        if(locationCount>0){
            // Moving CameraPosition to last clicked position
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));

            // Setting the zoom level in the map on last position  is clicked
            map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }


    // Tabs action listener
    public void SetUpTaps()
    {

        Button Hactvity=(Button)findViewById(R.id.button3);
        Button Mactivity= (Button)findViewById(R.id.button4);
        Button VLactivity=(Button)findViewById(R.id.button2);
        Button Ractivity=(Button)findViewById(R.id.button);

        //Home Tab
        Hactvity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();

            }
        });

        //Map tab
        Mactivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
                finish();

            }
        });

        //Visited Location List tab
        VLactivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
                finish();

            }
        });


        //Recommandition tab
        Ractivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RecommendationActivity.class);
                startActivity(i);
                finish();

            }
        });

    }
}
