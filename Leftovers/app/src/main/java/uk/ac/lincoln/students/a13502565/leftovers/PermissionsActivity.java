//////////////////////////////////////////////////
//Nathaniel Josephs ////////////////////////////
//Leftovers App - PermissionsActivity.cs///////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.security.Permission;

public class PermissionsActivity extends AppCompatActivity {

    Switch locationSwitch;
    Switch internetSwitch;
    Switch readwriteSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        //Defining the Switch UI elements
        locationSwitch = (Switch) findViewById(R.id.switchLocation);
        internetSwitch = (Switch) findViewById(R.id.switchInternet);
        readwriteSwitch = (Switch) findViewById(R.id.switchReadWrite);

        //Getting the current permission status
        int permissionLocation = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionReadExternal = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternal = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionInternet = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.INTERNET);

        // Read/Write Permission
        if (permissionWriteExternal == PackageManager.PERMISSION_GRANTED|| permissionReadExternal == PackageManager.PERMISSION_GRANTED) {
            readwriteSwitch.setChecked(true);
            readwriteSwitch.setEnabled(false);
        }
        // Internet Permission
        if (permissionInternet == PackageManager.PERMISSION_GRANTED) {
            internetSwitch.setChecked(true);
            internetSwitch.setEnabled(false);
        }
        // Location Permission
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            locationSwitch.setChecked(true);
            locationSwitch.setEnabled(false);
        }
    }

    public void onSwitchClicked(View view) {
        switch (view.getId()) {
            case R.id.switchLocation:
                if (locationSwitch.isChecked()) {
                    // do something when check is selected
                    Toast.makeText(PermissionsActivity.this, "Location Permission Checked", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(PermissionsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                    //The user should not be able to uncheck, this can only be done from the app settings in androidOS
                    locationSwitch.setEnabled(false);
                } else {
                    //do something when unchecked
                    Toast.makeText(PermissionsActivity.this, "Location Permission Unchecked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.switchInternet:
                if (internetSwitch.isChecked()) {
                    // do something when check is selected
                    Toast.makeText(PermissionsActivity.this, "Internet Permission Checked", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(PermissionsActivity.this,
                            new String[]{Manifest.permission.INTERNET},
                            1);
                    //The user should not be able to uncheck, this can only be done from the app settings in androidOS
                    internetSwitch.setEnabled(false);
                } else {
                    //do something when unchecked
                    Toast.makeText(PermissionsActivity.this, "Internet Permission Unchecked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.switchReadWrite:
                if (readwriteSwitch.isChecked()) {
                    // do something when check is selected
                    Toast.makeText(PermissionsActivity.this, "Read/Write Permission Checked", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(PermissionsActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                    ActivityCompat.requestPermissions(PermissionsActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                    //The user should not be able to uncheck, this can only be done from the app settings in androidOS
                    readwriteSwitch.setEnabled(false);
                } else {
                    //do something when unchecked
                    Toast.makeText(PermissionsActivity.this, "Read/Write Permission Unchecked", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
