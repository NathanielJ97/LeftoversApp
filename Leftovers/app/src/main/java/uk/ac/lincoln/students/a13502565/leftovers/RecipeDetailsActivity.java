//////////////////////////////////////////////////
//CMP3034M - Mobile Computing Assignment Item 1//
//Nathaniel Josephs - JOS13502565///////////////
//Leftovers App - RecipeDetailsActivity.cs/////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class RecipeDetailsActivity extends AppCompatActivity {
    //Tag for Debug logs etc.
    private static final String TAG = "Recipe Details Activity";

    String recipeAPIKey = "7e3ea6fc23a655a74c53190eb3f504bf";
    //String recipeAPIKey = "9da3afd669ac2906655dadc595859677"; //Spare key that was used for testing purposes due to limit

    //Adding Firebase Database functionality
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference firebaseRef;

    //Recipe object
    Recipe recipe;

    //Get current length of the favourites array
    final int[] favLength = new int[1];
    int favArrayPos = 0; //Current position in array of the favourited item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        //Access and obtain the recipe id from the previous Intent (Data being passed through activities)
        Intent previousIntent = getIntent();
        final String recipeId = previousIntent.getStringExtra("Recipe_ID");

        recipe = RecipeDetailsGetRequest(recipeId);

        //Declare Database Reference Objects, allows access to database
        //THE USER MUST BE SIGNED IN
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRef = firebaseDatabase.getReference();

        //Authentication Listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(RecipeDetailsActivity.this, user.getEmail() + "Successfully signed in.", Toast.LENGTH_SHORT);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(RecipeDetailsActivity.this, "Successfully signed out.", Toast.LENGTH_SHORT);
                }
            }
        };

        //Getting current authenticated user
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid(); //Get ID of user
        // Read from the database
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference(userID);
        //Read Data for length of the favourites array on creation
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get Child Data Snapshot ("Favourites")
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    //Get Child Data Snapshot
                    favLength[0] = (int) snap.getChildrenCount(); //Get the count of children in the favourites array
                    //Now check if this is already favourited in the array
                    for (DataSnapshot snap2 : snap.getChildren()){
                        //If it is already favourited
                        if (snap2.child("Recipe_Id").getValue().toString().contains(recipeId)){
                            //Set checkbox to the checked, store the favArrayPosition
                            ((CheckBox) findViewById(R.id.chkFave)).setChecked(true); //setChecked
                            favArrayPos = Integer.valueOf(snap2.getKey());
                        }
                        else
                            {
                            //setCheck to unchecked
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Provide message to user when there is an error loading the database
                Toast.makeText(RecipeDetailsActivity.this, "Error loading database!", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //If the checkbox is clicked
    public void CheckboxClicked(View view) {
        //Check if the checkbox is checked (i.e. already a favourite)
        if (((CheckBox) view).isChecked()) {
            try {
                //If it is not checked, then add the current recipe item to the favourites database
                AddToFavourites();
                Toast.makeText(RecipeDetailsActivity.this,
                        "Checked", Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                //Do something with exception
                Toast.makeText(RecipeDetailsActivity.this,
                        "Error adding to favourites", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            try {
                //If it is checked, remove the current recipe item from the favourites database
                RemoveFromFavourites();
                Toast.makeText(RecipeDetailsActivity.this,
                        "Unchecked", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                //Do something with exception
                Toast.makeText(RecipeDetailsActivity.this,
                        "Error removing from favourites", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Add the current recipe to the firebase favourites database
    private void AddToFavourites() {
        Log.d(TAG, "Adding to the database.");
        try {
            //Check that the recipe id is not empty
            if (!recipe.getId().equals("")) {
                //Getting current authenticated user
                FirebaseUser user = mAuth.getCurrentUser();
                String userID = user.getUid(); //Get ID of user

                firebaseRef.child(userID).child("Favourites").child(String.valueOf((favLength[0] + 1)).toString()).child("Recipe_Id").setValue(recipe.getId());
                firebaseRef.child(userID).child("Favourites").child(String.valueOf((favLength[0] + 1)).toString()).child("Title").setValue(recipe.getTitle());
                firebaseRef.child(userID).child("Favourites").child(String.valueOf((favLength[0] + 1)).toString()).child("Publisher").setValue(recipe.getPublisher());
                firebaseRef.child(userID).child("Favourites").child(String.valueOf((favLength[0] + 1)).toString()).child("Ingredients").setValue(Arrays.toString(recipe.getIngredients()));
                firebaseRef.child(userID).child("Favourites").child(String.valueOf((favLength[0] + 1)).toString()).child("OriginalURL").setValue(recipe.getOriginalURL());
                firebaseRef.child(userID).child("Favourites").child(String.valueOf((favLength[0] + 1)).toString()).child("ImageURL").setValue(recipe.getImageURL());
                Toast.makeText(RecipeDetailsActivity.this, "Added Recipe: " + recipe.getId() + " to Favourites", Toast.LENGTH_SHORT);
            }
        } catch (Exception e){
            //
        }
    }

    //Remove the current recipe from the favourites
    private void RemoveFromFavourites() {
        Log.d(TAG, "Removing from favourites database.");
        //If the recipe is already favourited, uncheck and remove the item from the database

        //Getting current authenticated user
        FirebaseUser user = mAuth.getCurrentUser();
        final String userID = user.getUid(); //Get ID of user

        //Get the specific child and remove at that point
        DatabaseReference userReference = firebaseRef.child(userID);

        //Data snapshot required of the users database
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get Child Data Snapshot ("Favourites")
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    favLength[0] = (int) snap.getChildrenCount(); //Get the count of children in the favourites array
                    //Get Child Data Snapshot
                    for (DataSnapshot snap2 : snap.getChildren())
                    {
                        //Shift the values of those down following accordingly (Going through those that lie above the one to be removed)
                        if (Integer.valueOf(snap2.getKey()) > favArrayPos) {
                            //Firebase has no concept of renaming the name/path of each favourite.
                            //It has to be copied, added and removed
                            //Copy the current value down a position using a HashMap
                            HashMap<String, Object> copyValue = new HashMap<>();
                            //Change the key value to one below its prior (Shift down)
                            copyValue.put(String.valueOf(Integer.valueOf(snap2.getKey())-1), snap2.getValue());
                            //Remove the value the user selected to 'unfavourite'
                            firebaseRef.child(userID).child("Favourites").child(String.valueOf(Integer.valueOf(snap2.getKey())-1)).removeValue();
                            //Update the new path for this favourite (shift down one)
                             firebaseRef.child(userID).child("Favourites").updateChildren(copyValue);
                            //Remove the value above
                            firebaseRef.child(userID).child("Favourites").child(snap2.getKey()).removeValue();
                        }
                        else
                        {
                            //Do nothing as it is below the array position we just moved
                            Log.d(TAG, "Position below removed item");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Provide message to user when there is an error loading the database
                Toast.makeText(RecipeDetailsActivity.this, "Error loading database!", Toast.LENGTH_SHORT);
            }
        });
    }

    //Method responsible for updating the recipe details display
    private void UpdateRecipeDetailsDisplay(Recipe displayRecipe) {
        //Create the ImageLoader object
        ImageLoader imageLoader = ImageLoader.getInstance();
        //Check if the image loader has been initialised in a previous thread
        if (!imageLoader.isInited()) {
            //Method for setting up the Universal Image Loader
            SetupImageLoader();

            //Create the ImageLoader object
            imageLoader = ImageLoader.getInstance();
        }


        try {
            ((TextView) findViewById(R.id.txtTitle)).setText("Title: " + displayRecipe.getTitle());//Update Title Text View
            ((TextView) findViewById(R.id.txtID)).setText("ID: " + displayRecipe.getId());//Update ID Text View
            ((TextView) findViewById(R.id.txtPublisher)).setText("Publisher: " + displayRecipe.getPublisher());//Update Publisher Text View
            ((TextView) findViewById(R.id.txtIngredients)).setText("Ingredients: \n" + Arrays.toString(displayRecipe.getIngredients()).replaceAll("\\[|\\]|,,","").replaceAll(",", "\n- "));//Update Ingredients Text View, Remove Double Commas and square brackets and then treat each commas as a new line
            String imageURL = displayRecipe.getImageURL(); //Set the Image URL
            ((TextView) findViewById(R.id.txtFullRecipe)).setText(("To see full recipe text:" + displayRecipe.getOriginalURL()));

            //Define the image that will be loaded should the ImageLoader fail to download from the imageURL
            int fallBackImage = this.getResources().getIdentifier("@drawable/image_failed", null, this.getPackageName());

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .resetViewBeforeLoading(true)
                    .showImageForEmptyUri(fallBackImage)
                    .showImageOnFail(fallBackImage)
                    .showImageOnLoading(fallBackImage).build();

            imageLoader.displayImage(imageURL,((ImageView) findViewById(R.id.imageView)), options);
        } catch (Exception e){
            //Provide message to the log when there is an error loading the image
            Log.d(TAG, "Error Loading Image");
        }

    }

    private void SetupImageLoader() {
        // UNIVERSAL IMAGE LOADER SETUP

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        // END - UNIVERSAL IMAGE LOADER SETUP
    }

    //GET request for a recipe details, returns a Recipe of recipe details
    private Recipe RecipeDetailsGetRequest(String id) {

        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        String requestURL = "https://www.food2fork.com/api/get?key=" + recipeAPIKey + "&rId=" + id;

        // Formulate the request and handle the response.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Do something with the response
                        try {
                            JSONObject recipeObject = response.getJSONObject("recipe");
                            String ingredients = recipeObject.getString("ingredients");
                            recipe = new Recipe(recipeObject.getString("recipe_id"), recipeObject.getString("title"), recipeObject.getString("publisher"), recipeObject.getString("image_url"), ingredients.split("\""), recipeObject.getString("source_url"));
                            //Update Recipe Details Display
                            UpdateRecipeDetailsDisplay(recipe);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    },
                        new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.d(TAG, "API Error: " + error.toString());
                               }
                });
    // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);

    //return the recipe object
        return recipe;
}

    //Method for downloading recipe to a text file for read/write internal
    public void DownloadRecipe(View view) {
        //Download as a text file
        FileOutputStream outputStream = null;
        //Defining the output file name
        String outputName = recipe.getId();
        //Numbers are used for identifying, splitting and formatting the string when loaded
        String outputFile = (recipe.getId() + "@1@" + recipe.getTitle() + "@2@" + recipe.getPublisher() + "@3@" + (Arrays.toString(recipe.getIngredients())) + "@4@" + recipe.getImageURL() + "@5@" + recipe.getOriginalURL());


        try
        {
            outputStream = openFileOutput(outputName, MODE_PRIVATE);
            outputStream.write(outputFile.getBytes());
            Toast.makeText(RecipeDetailsActivity.this, "Saved to: " + getFilesDir() + "/" + outputName, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /* called when the user hits the use find missing ingredients button
     * This displays a map with the nearest grocery stores (of which are open) to
     * the user based on their location, using the google
     * places API*/
    //Go to the Google Places API to find a shop that may sell the missing ingredients
    public void storeFinderButton(View view)
    {
        try {
            //Intent created to start the storeFinderActivity
            Intent intent = new Intent(this, StoreFinderActivity.class);
            //Start the Store Finder Activity
            startActivity(intent);
        }catch (Exception e){
            //Check Permissions
            Toast.makeText(RecipeDetailsActivity.this,"Please check your permissions", Toast.LENGTH_SHORT);
        }
    }
}
