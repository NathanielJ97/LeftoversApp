//////////////////////////////////////////////////
//Nathaniel Josephs ////////////////////////////
//Leftovers App - StartActivity.cs/////////////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "Start Activity";

    //Setup Firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();

        //Authentication Listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(StartActivity.this, user.getEmail() + "Successfully signed in.", Toast.LENGTH_SHORT);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(StartActivity.this, "Successfully signed out.", Toast.LENGTH_SHORT);
                }
                // ...
            }
        };
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

    //Method for Creating Firebase Account
    public void CreateAccount (View view) {
        //Get input information
        EditText emailText = findViewById(R.id.txtEmail);
        EditText passwordText = findViewById(R.id.txtPassword);
        email = emailText.getText().toString();
        password = passwordText.getText().toString();

        //Check that the email and password are not empty
        if (!email.equals("")&&!password.equals("")) {
            //Create User with email address and password method (Firebase)
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign up success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(StartActivity.this, "Account Creation Success.",
                                        Toast.LENGTH_SHORT).show();

                                FirebaseUser user = mAuth.getCurrentUser();
                            } else {
                                // If sign up fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(StartActivity.this, "Account Creation Failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(StartActivity.this, "You must fill the email and password fields correctly", Toast.LENGTH_SHORT);
        }
    }

    //Method for Logging into a Firebase Account
    public void SignInAccount(View view) {
        //Get input information
        EditText emailText = findViewById(R.id.txtEmail);
        EditText passwordText = findViewById(R.id.txtPassword);
        email = emailText.getText().toString();
        password = passwordText.getText().toString();

        //Sign in User with email address and password method (Firebase)
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(StartActivity.this, "Login Authentication Successful.",
                                    Toast.LENGTH_SHORT).show();

                            //Intent created to start the mainActivity
                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                            //Start the Main Activity
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(StartActivity.this, "Login Authentication Failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

