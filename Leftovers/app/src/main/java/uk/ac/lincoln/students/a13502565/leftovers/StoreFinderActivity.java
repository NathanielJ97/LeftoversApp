package uk.ac.lincoln.students.a13502565.leftovers;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class StoreFinderActivity extends AppCompatActivity {
    //Tag for debug logs etc.
    private static final String TAG = "Store Finder Activity";
    //API Key Constant
    private static final String GOOGLE_API_KEY = "google_maps_key";

    //Places Database
    DatabaseHelper databaseHelper;

    private MapsStatePagerAdapter mapsStatePagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_finder);

        //Database Initialisation
        databaseHelper = new DatabaseHelper(this);

        //Fragments Initialisation
        mapsStatePagerAdapter = new MapsStatePagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.fragmentContainer);

        //setup the page adapter
        setupViewPager(viewPager);

        Toast.makeText(StoreFinderActivity.this, "Location Permission Allowed", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(StoreFinderActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        try{
            // get location code
            // acquire a reference to the system Location Manager
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // Use GPS provider to get last known location
            String locationProvider = LocationManager.GPS_PROVIDER;
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

            // create a few new variable to get and store the lat/long coordinates of last known location
            double lat = lastKnownLocation.getLatitude();
            double longi = lastKnownLocation.getLongitude();

            //Save to Shared Preferences
            SaveLastLoc(lat, longi);

            //This method will populate the places database, if no connection the last database that was populated will be used
            PlacesNearbySearchGetRequest(lat, longi);
        }catch (Exception e){
            //Check Permissions
            Toast.makeText(StoreFinderActivity.this,"Please check your permissions", Toast.LENGTH_SHORT);
    }
    }

    private void PlacesNearbySearchGetRequest(final double lat, double longi) {
        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        //Google Nearby Places API REQUEST URL
        //String requestURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + longi + "&radius=1500&type=supermarket&opennow=true&key=" + GOOGLE_API_KEY;
        String requestURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + longi + "&radius=2000&type=supermarket&key=" + GOOGLE_API_KEY; //Had to switch to this because of lack of results

        // Formulate the request and handle the response.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Do something with the response
                        try{
                            JSONArray placesArray = response.getJSONArray("results");
                            for (int i = 0; i < placesArray.length(); i++) {
                                JSONObject placesObject = placesArray.getJSONObject(i);
                                //Get the Lat and Lon Locations
                                JSONObject placesGeomObject = placesObject.getJSONObject("geometry");
                                JSONObject placesLocationObject = placesGeomObject.getJSONObject("location");

                                String placesLat = placesLocationObject.getString("lat");
                                String placesLong = placesLocationObject.getString("lng");
                                //
                                String placesId = placesObject.getString("id");
                                String placesName = placesObject.getString("name");
                                AddData(placesId, placesName, placesLat, placesLong);
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle error
                                Log.d(TAG, "Error: " + error.toString());
                                Toast.makeText(StoreFinderActivity.this, "Error Getting Nearby Place Information", Toast.LENGTH_SHORT).show();
                            }
                        });
        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }

    //Initial of the setup view pager
    private void setupViewPager (ViewPager viewPager){
        MapsStatePagerAdapter adapter = new MapsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PlacesListFragment(), ("PlacesListFragment")); //Because this one is first, will show Places List Fragment first
        adapter.addFragment(new PlacesMapsFragment(), ("PlacesMapsFragment"));
        viewPager.setAdapter(adapter);
    }

    //Set the current fragment
    public void setViewPager(int fragmentNumber){
        viewPager.setCurrentItem(fragmentNumber);
    }

    //Save the current location into the last known location into Shared Preferences
    private  void SaveLastLoc(double latitude, double longitude){
        //doubles latitude, and longitude are our current locations passed in

        // create a new shared preferences file by name
        // if it already exists it will use existing file
        try {
            SharedPreferences locationInfo = getSharedPreferences("locationData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = locationInfo.edit();
            // put the key values and lat/long into the shared preferences file
            editor.putString("latitude", String.valueOf(latitude));
            editor.putString("longitude", String.valueOf(longitude));
            editor.commit();

            // show toast message for successful save
            Context context = getApplicationContext();
            CharSequence text = "Location data saved!";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        catch(Exception e) {
            // Handle error
            Log.d(TAG, "Error Saving into Shared Preferences" + e.toString());
        }
    }

    //Method for adding a place to the database
    public void AddData(String newIDEntry, String newNameEntry, String newLatEntry, String newLongEntry)
    {
        boolean insertData = databaseHelper.addData(newIDEntry, newNameEntry, newLatEntry, newLongEntry);

        Log.d(TAG, "Data Inserting to Database");

        if (insertData) {
            Log.d(TAG, "Data Insert Success");
        } else {
            Log.d(TAG, "Data Insert Failed");
        }
    }

    //Load the last known location from Shared Preferences, returns the string value which can later be converted appropriately
    private String[] LoadLastLoc(){

        //Latitude and Longitude objects that can be returned
        String latitude, longitude;

        // get the saved values of locations from the Shared Preferenced file 'locationData' file
        SharedPreferences locationInfo = getSharedPreferences("locationData", Context.MODE_PRIVATE);

        // set the lat and long to the values from Shared Preferenced file 'locationData'
        latitude = locationInfo.getString("latitude", "");
        longitude = locationInfo.getString("longitude", "");

        return new String[]{latitude, longitude};
    }
}
