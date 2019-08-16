//////////////////////////////////////////////////
//Nathaniel Josephs ////////////////////////////
//Leftovers App - DatabaseHelper.cs////////////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "places_table"; //Table Name
    private static final String placeId = "placesID"; //Column 1
    private static final String placeName = "Name"; //Column 2
    private static final String placeLatitude = "Latitude"; //Column 3
    private static final String placeLongitude = "Longitude"; //Column 4

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + placeId + " TEXT, " + placeName  + " TEXT, " + placeLatitude + " TEXT, " + placeLongitude + " TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Method for adding an item to the database
    public boolean addData(String idItem, String nameItem, String latItem, String longItem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(placeName, idItem);
        contentValues.put(placeName, nameItem);
        contentValues.put(placeLatitude, latItem);
        contentValues.put(placeLongitude, longItem);

        //Log the entry into the database
        Log.d(TAG, "addData: Adding " + idItem + nameItem + latItem + longItem + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if the data is not inserted correctly it will return -1
        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    //RETURNS ALL THE DATA FROM THE DATABASE
    public Cursor getData(){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = database.rawQuery(query, null);
        return data;
    }
}
