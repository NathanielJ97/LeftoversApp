//////////////////////////////////////////////////
//CMP3034M - Mobile Computing Assignment Item 1//
//Nathaniel Josephs - JOS13502565///////////////
//Leftovers App - PlacesMapsFragment.cs////////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

public class PlacesMapsFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "Places Maps Fragment";

    //Defining Google Maps stuff
    MapView mapView;

    private Button btnPlacesList;
    private Button btnPlacesMaps;

    //Database
    DatabaseHelper databaseHelper;

    //String for storing last known location
    String[] lastLoc;

    //Use onCreateView instead of onCreate for fragments, is managing the layouts and just creating a view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Specify our layout for the fragment view
        View view = inflater.inflate(R.layout.fragment_places_map, container, false);

        //Declare the two buttons which allow fragment switching (view has to be referenced)
        btnPlacesList = view.findViewById(R.id.btnPlacesList);
        btnPlacesMaps = view.findViewById(R.id.btnPlacesMap);

        //Initialise the Database
        databaseHelper = new DatabaseHelper(getActivity());

        //Listener for button that changes to the Places List Fragment
        btnPlacesList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Display message to user that they are changing Fragment
                Toast.makeText(getActivity(), "Going to Places List Fragment", Toast.LENGTH_SHORT).show();
                //places list fragment method
                ((StoreFinderActivity)getActivity()).setViewPager(0);
            }
        });

        //Listener for button that changes to the Places Maps Fragment
        btnPlacesMaps.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Display message to user that they are changing Fragment
                Toast.makeText(getActivity(), "Going to Places Maps Fragment", Toast.LENGTH_SHORT).show();
                //places maps fragment method
                ((StoreFinderActivity)getActivity()).setViewPager(1);
            }
        });



        //Map and Mapview Setup
        //Load from the shared preferences (this can be used if the location services are not available)
        lastLoc = LoadLastLoc();
        //Initialisation for Google Map
        mapView = view.findViewById(R.id.mapView);
        setupGoogleMap(savedInstanceState);

        return view;
    }

    //Method for Google Map initialisation
    private void setupGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle("mvBundleKey");
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    //Load the last known location from Shared Preferences, returns the string value which can later be converted appropriately
    private String[] LoadLastLoc(){

        //Latitude and Longitude objects that can be returned
        String latitude, longitude;

        // get the saved values of locations from the Shared Preference 'locationData' file
        SharedPreferences locationInfo = getActivity().getSharedPreferences("locationData", Context.MODE_PRIVATE);

        // set the lat and long to the values from Shared Preference 'locationData' file
        latitude = locationInfo.getString("latitude", "");
        longitude = locationInfo.getString("longitude", "");

        return new String[]{latitude, longitude};
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //Set the camera position and zoom to that of the centre marker
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Double.valueOf(lastLoc[0]), Double.valueOf(lastLoc[1]))) //target focused on current position
                .zoom(13.5F) //zoom in the current focus
                .bearing(0F) // orientation
                .build();

        //use map to move the camera into position
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(lastLoc[0]), Double.valueOf(lastLoc[1]))).title("Your Current Position"));
        //PUT THE DATABASE NAMES AND LOCATIONS IN
        //get the data
        Cursor data = databaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()){
            //Iterates through each row
            //Add a map marker, with the name of the shop, lat and long details
            map.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(data.getString(3)), Double.valueOf(data.getString(4)))).title(data.getString(2))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))); //Set the colour of the store markers to Orange
        }
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
