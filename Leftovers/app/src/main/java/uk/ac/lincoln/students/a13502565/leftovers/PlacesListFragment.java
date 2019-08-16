//////////////////////////////////////////////////
//Nathaniel Josephs ////////////////////////////
//Leftovers App - PlacesListFragment.cs////////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PlacesListFragment extends Fragment {
    //Tag for debug logs etc.
    private static final String TAG = "Places List Fragment";

    private Button btnPlacesList;
    private Button btnPlacesMaps;

    //List view for places returned
    private ListView placesListView;
    //Database
    DatabaseHelper databaseHelper;

    //Use onCreateView instead of onCreate for fragments, is managing the layouts and just creating a view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Specify our layout for the fragment view
        View view = inflater.inflate(R.layout.fragment_places_list, container, false);

        //Init the two buttons which allow fragment switching (view has to be referenced)
        btnPlacesList = view.findViewById(R.id.btnPlacesList);
        btnPlacesMaps = view.findViewById(R.id.btnPlacesMap);
        //Init the List view for places returned
        placesListView = view.findViewById(R.id.placesListView);
        //Init Database
        databaseHelper = new DatabaseHelper(getActivity());

        btnPlacesList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Display Message to user
                Toast.makeText(getActivity(), "Going to Places List Fragment", Toast.LENGTH_SHORT).show();
                //places list fragment method
                ((StoreFinderActivity)getActivity()).setViewPager(0);
            }
        });

        btnPlacesMaps.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Display Message to user
                Toast.makeText(getActivity(), "Going to Places Maps Fragment", Toast.LENGTH_SHORT).show();
                //places maps fragment method
                ((StoreFinderActivity)getActivity()).setViewPager(1);
            }
        });

        //Load from the Shared Preferences (this can be used if the location services are not available)
        String[] lastLoc = LoadLastLoc();

        //Display Places in List View
        PopulatePlacesListView();

        return view;
    }

    //Method for populating the list view with the places currently in the places database
    private void PopulatePlacesListView(){
        HashMap<String, String> nameLatLon = new HashMap<>();
        //PUT THE DATABASE NAMES AND LOCATIONS IN
        //get the data
        Cursor data = databaseHelper.getData();
        while (data.moveToNext()){
            //Iterates through each row
            //Put in the HashMap list
            nameLatLon.put(data.getString(2), data.getString(3) + data.getString(4));
        }

        List<HashMap<String, String>> placesListItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), placesListItems, R.layout.places_list_item,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.txtName, R.id.txtLocation});

        //Will contain all items in list
        Iterator iterator = nameLatLon.entrySet().iterator();
        while (iterator.hasNext()){
            //Pair together both the names and the locations to display in the listview
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) iterator.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());

            //Add each item in the hashmap as a list item to the list view
            placesListItems.add(resultsMap);
        }
        placesListView.setAdapter(adapter); //Set adapter
    }

    //Load the last known location from Shared Preferences, returns the string value which can later be converted appropriately
    private String[] LoadLastLoc(){

        //Latitude and Longitude objects that can be returned
        String latitude, longitude;

        // get the saved values of locations from the Shared Preferenced file 'locationData' file
        SharedPreferences locationInfo = getActivity().getSharedPreferences("locationData", Context.MODE_PRIVATE);

        // set the lat and long to the values from Shared Preferenced file 'locationData'
        latitude = locationInfo.getString("latitude", "");
        longitude = locationInfo.getString("longitude", "");

        return new String[]{latitude, longitude};
    }
}
