//////////////////////////////////////////////////
//Nathaniel Josephs ////////////////////////////
//Leftovers App - RecipeSearchActivity.cs//////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeSearchActivity extends AppCompatActivity {
    //Tag for Debug logs etc.
    private static final String TAG = "Recipe Search Activity";

    String recipeAPIKey = "7e3ea6fc23a655a74c53190eb3f504bf";
    //String recipeAPIKey = "9da3afd669ac2906655dadc595859677"; //Spare key that was used for testing purposes due to limit

    //List of all the recipes returned from API search
    ArrayList<Recipe> recipeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);

        final ListView lstRecipeList = findViewById(R.id.lstRecipeList);

        RecipeListAdapter adapter = new RecipeListAdapter(this, R.layout.adapter_recipe_view_layout, recipeList);
        lstRecipeList.setAdapter(adapter);
        //Setting up the On click listener, so that the user can select which recipe they would like to view
        lstRecipeList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                     ViewRecipe(recipeList.get(position));
                                                 }
                                             });

    }

    //View the specific recipe after clicking on the list item
    private void ViewRecipe(Recipe recipe) {

        //Intent created to start the recipeDetailsActivity
        Intent intent = new Intent(RecipeSearchActivity.this, RecipeDetailsActivity.class);

        //Pass through the recipe id information, (the GET request will be performed in the new activity on creation)
        String recipeId = recipe.getId().toString();
        intent.putExtra("Recipe_ID", recipeId);
        //Start the Recipe Details Activity
        startActivity(intent);
    }

    /* called when the user hits the search  button */
    public void SearchButton(View view)
    {
        //get the ingredients entered from the user
        EditText txtIngredient = findViewById(R.id.txtIngredient);
        EditText txtIngredient2 = findViewById(R.id.txtIngredient2);
        EditText txtIngredient3 = findViewById(R.id.txtIngredient3);
        String leftover1 = txtIngredient.getText().toString();
        String leftover2 = txtIngredient2.getText().toString();
        String leftover3 = txtIngredient3.getText().toString();

        //This method will populate the recipe list, if no connection will populate it with last known
        JSONArray recipeArray = RecipeSearchGetRequest(leftover1, leftover2, leftover3);

        //Method for updating the list with returned information
        PopulateRecipeList(recipeArray);
    }

    //GET request for a recipe search, returns a JSONArray list of recipes
    private JSONArray RecipeSearchGetRequest(String leftover1, String leftover2, String leftover3) {
        //Define the recipe listview
        final ListView lstRecipeList = findViewById(R.id.lstRecipeList);
        final RecipeListAdapter adapter = new RecipeListAdapter(this, R.layout.adapter_recipe_view_layout, recipeList);

        RequestQueue requestQueue;
        JSONArray recipeArray = new JSONArray();

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        //API Search Request
        String requestURL = "https://www.food2fork.com/api/search?key=" + recipeAPIKey + "&q=" + leftover1 + "," + leftover2 + "," + leftover3 + "&page=1";

        // Formulate the request and handle the response.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Do something with the response
                        try{
                            JSONArray recipeArray = response.getJSONArray("recipes");
                            for (int i = 0; i < recipeArray.length(); i++) {
                                JSONObject recipeObject = recipeArray.getJSONObject(i);
                                String[] ingredients = {"ingredients"}; //Define an empty array as Array Intitialiser is not possible in below line
                                Recipe recipe = new Recipe(recipeObject.getString("recipe_id"), recipeObject.getString("title"), recipeObject.getString("publisher"), recipeObject.getString("image_url"), ingredients, "originalURL");
                                recipeList.add(recipe);

                                lstRecipeList.setAdapter(adapter);
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
                                Toast.makeText(RecipeSearchActivity.this,"Recipe Search Error",Toast.LENGTH_SHORT);
                                Log.d(TAG, "Error: " + error.toString());
                            }
                        });
        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);

        //Method for updating the list with returned information
        PopulateRecipeList(recipeArray);

        //return the recipe array
        return recipeArray;
    }

    //Method that populates the list view with recipes using adapter
    private void PopulateRecipeList(JSONArray recipeArray) {
        //Define the recipe listview
        ListView lstRecipeList = findViewById(R.id.lstRecipeList);

        try {
            for (int i = 0; i < recipeArray.length(); i++) {
                JSONObject recipeObject = recipeArray.getJSONObject(i);
                String[] ingredients = {"ingredients"}; //Define an empty array as Array Intitialiser is not possible in below line
                Recipe recipe = new Recipe(recipeObject.getString("recipe_id"), recipeObject.getString("title"), recipeObject.getString("publisher"), recipeObject.getString("image_url"), ingredients, "originalURL");
                recipeList.add(recipe);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Define adapter based on the recipe view layout designed and the recipe list
        RecipeListAdapter adapter = new RecipeListAdapter(this, R.layout.adapter_recipe_view_layout, recipeList);
        lstRecipeList.setAdapter(adapter); //Set adapter
    }
}
