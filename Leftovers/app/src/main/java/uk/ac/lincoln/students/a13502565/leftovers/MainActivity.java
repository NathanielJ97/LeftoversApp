//////////////////////////////////////////////////
//Nathaniel Josephs ////////////////////////////
//Leftovers App - MainActivity.cs//////////////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /* called when the user hits the use leftovers button,
     * allows the user to search using a Request based on
      * the leftover ingredients input*/
    public void useLeftoversButton(View view)
    {
        //Intent created to start the recipeSearchActivity
        Intent intent = new Intent(this, RecipeSearchActivity.class);
        //Start the Recipe Search Activity
        startActivity(intent);

    }

    /* called when the user hits the use favourites button
     * This displays a list of id's and names of recipes*/
    public void favouritesButton(View view)
    {
        //Intent created to start the favouritesActivity
        Intent intent = new Intent(this, FavouritesActivity.class);
        //Start the Favourites Activity
        startActivity(intent);

    }

    /* called when the user hits the use downloads button
     * This displays a recipe that was
      * saved to the device, based on a recipe id*/
    public void downloadsButton(View view)
    {
        //Intent created to start the downloadsActivity
        Intent intent = new Intent(this, DownloadsActivity.class);
        //Start the Downloads Activity
        startActivity(intent);

    }

    /* called when the user hits the use store finder button
     * This displays a map with the nearest grocery stores (of which are open) to
     * the user based on their location, using the google
     * places API*/
    public void storeFinderButton(View view)
    {
        try {
            //Intent created to start the storeFinderActivity
            Intent intent = new Intent(this, StoreFinderActivity.class);
            //Start the Store Finder Activity
            startActivity(intent);
        }catch (Exception e){
            //Check Permissions
            Toast.makeText(MainActivity.this,"Please check your permissions", Toast.LENGTH_SHORT);
        }

    }

    /* called when the user hits the User Permissions Button
     * This displays the activity that allows for user permissions to be managed.
     */
    public void managePermissionsButton(View view)
    {
        //Intent created to start the PermissionsActivity
        Intent intent = new Intent(this, PermissionsActivity.class);
        //Start the Permissions Activity
        startActivity(intent);
    }
}
