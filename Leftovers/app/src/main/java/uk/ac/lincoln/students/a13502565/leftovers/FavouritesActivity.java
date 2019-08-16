//////////////////////////////////////////////////
//Nathaniel Josephs ////////////////////////////
//Leftovers App - FavouritesActivity.cs/////////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FavouritesActivity extends AppCompatActivity {

    //Tag for Debug logs
    private static final String TAG = "Favourites Activity";

    //Adding Firebase Database functionality by defining the database and its authentication
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference userReference; //Reference to user data in database

    //List view for places returned
    private ListView favouritesListView;
    HashMap<String, String> titlePubID = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        //Init the List view for places returned
        favouritesListView = findViewById(R.id.favouritesListView);

        //Declare Database Reference Objects, allows access to database
        //THE USER MUST BE SIGNED IN
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Authentication Listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(FavouritesActivity.this, user.getEmail() + "Successfully signed in.", Toast.LENGTH_SHORT);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(FavouritesActivity.this, "Successfully signed out.", Toast.LENGTH_SHORT);
                }
            }
        };

        //Getting current authenticated user
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid(); //Get ID of user
        // Read from the database
        userReference = FirebaseDatabase.getInstance().getReference(userID);

        //Read Data for length of the favourites array on creation
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get Child Data Snapshot ("Favourites")
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    //Get Child Data Snapshot ("Favourites")
                    for (DataSnapshot snap2 : snap.getChildren()) {
                        //For each child, get the values of recipe id, name and publisher
                        String recipeID = snap2.child("Recipe_Id").getValue().toString();
                        String title = snap2.child("Title").getValue().toString();
                        String publisher = snap2.child("Publisher").getValue().toString();

                        //Put in the HashMap list
                        titlePubID.put(title, publisher + " ID: " + recipeID);
                    }
                }
                //Display Favourites in List View after data has been changed and updated
                PopulateFavouritesListView();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Provide message to user when there is an error loading the database
                Toast.makeText(FavouritesActivity.this, "Error loading database!", Toast.LENGTH_SHORT);
            }
        });
    }

    //Method for populating the list view with the favourites currently in the users firebase account database
    private void PopulateFavouritesListView() {
        //Put the Hashmap from database into a list, so that it can be displayed in the adapter
        List<HashMap<String, String>> favouritesListItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(FavouritesActivity.this, favouritesListItems, R.layout.favourites_list_item,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.txtFavTitle, R.id.txtPublisherID});

        //Will contain all items in list
        Iterator iterator = titlePubID.entrySet().iterator();
        while (iterator.hasNext()){
            //Pair together both the title and the publisher + id to display in the listview
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) iterator.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());

            //Add each item in the hashmap as a list item to the list view
            favouritesListItems.add(resultsMap);
        }
        favouritesListView.setAdapter(adapter); //Set adapter
    }
}
