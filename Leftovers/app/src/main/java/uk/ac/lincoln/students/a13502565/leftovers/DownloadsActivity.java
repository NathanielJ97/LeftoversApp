//////////////////////////////////////////////////
//CMP3034M - Mobile Computing Assignment Item 1//
//Nathaniel Josephs - JOS13502565///////////////
//Leftovers App - DownloadsActivity.cs/////////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class DownloadsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

    }

    //Method for showing the explorer icon
    protected void LoadDownloadedFile(View view){
        FileInputStream inputStream = null;

        //Defining the UI objects we wish to edit
        TextView txtRecipeID = (TextView)findViewById(R.id.txtID);
        TextView txtTitle = (TextView)findViewById(R.id.txtTitle);
        TextView txtPublisher = (TextView)findViewById(R.id.txtPublisher);
        TextView txtIngredients = (TextView)findViewById(R.id.txtIngredients);
        TextView txtImageURL = (TextView)findViewById(R.id.txtImageURL);
        TextView txtOriginalURL = (TextView)findViewById(R.id.txtOriginalURL);
        //Getting the input from the user
        EditText txtFileName = (EditText)findViewById(R.id.txtFileName);
        String fileName = txtFileName.getText().toString();

        //Try statement for loading the file, exceptions are caught
        try{
            //Set up the Stream and Reader
            inputStream = openFileInput(fileName);
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputReader);
            StringBuilder stringBuilder = new StringBuilder();

            String inputText;
            while ((inputText = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputText);
            }
            //Remove and format the input text string
            inputText = stringBuilder.toString();
            //Strings are formatted via splitting of the input text, the array below keeps hold of the current split string
            String[] currentSplitsInText = inputText.split("@1@", 2);
            //First get ID
            String id = currentSplitsInText[0];
            txtRecipeID.setText("ID: " + id);
            currentSplitsInText = currentSplitsInText[1].split("@2@", 2);
            //get Title
            String title = currentSplitsInText[0];
            txtTitle.setText("Title: " + title);
            currentSplitsInText = currentSplitsInText[1].split("@3@", 2);
            //get Publisher
            String publisher = currentSplitsInText[0];
            txtPublisher.setText("Publisher: " + publisher);
            currentSplitsInText = currentSplitsInText[1].split("@4@", 2);
            //get Ingredients
            String ingredients = currentSplitsInText[0];
            txtIngredients.setText("Ingredients: \n" + ingredients
                    .replaceAll("\\[|\\]|,,","")
                    .replaceAll(",", "\n- ")); //Remove Double Commas and square brackets and then treat each commas as a new line
            currentSplitsInText = currentSplitsInText[1].split("@5@", 2);
            //get Image URL
            String imageURL = currentSplitsInText[0];
            txtImageURL.setText("Image URL: " + imageURL);
            //NO NEED FOR ANOTHER SPLIT, JUST TAKE LATTER OF PREVIOUS SPLIT
            //get Original URL
            String originalURL = currentSplitsInText[1];
            txtOriginalURL.setText("Original URL: " + originalURL);

        } catch (FileNotFoundException e) {
            //Provide error message if an exception is caught in the try statement, to direct the user towards a solution
            e.printStackTrace();
            Toast.makeText(DownloadsActivity.this, "Error Opening File (Not Found)", Toast.LENGTH_SHORT);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(DownloadsActivity.this, "Error Opening File (Input/Output)", Toast.LENGTH_SHORT);
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(DownloadsActivity.this, "Error Opening File", Toast.LENGTH_SHORT);
                }
            }
        }
    }
}
