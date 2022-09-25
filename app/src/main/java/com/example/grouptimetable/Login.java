package com.example.grouptimetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    //variables
    Button signinButton, signupButton, helpButton;
    String user, pass, share;
    DatabaseReference reff;
    List<User> userList;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //load toolbar
        displayToolbar();
        //empty list
        userList = new ArrayList<>();
        //buttons
        helpButton = findViewById(R.id.helpButton);
        signinButton = findViewById(R.id.buttonSignIn);
        signupButton = findViewById(R.id.buttonSignUp);
        //when the user chooses to sign in
        signinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //get the input from the text fields
                getTextFields();
                //if user hasn't entered a username or password
                if (user.length() == 0 || pass.length() == 0) {
                    Toast.makeText(Login.this, "One or More fields are empty", Toast.LENGTH_LONG).show();
                } else {
                    //query database for users
                    //get list of user objects
                    //if object with username and password exists
                    //use intent to move to main activity
                    reff = FirebaseDatabase.getInstance().getReference("Users");
                    reff.addListenerForSingleValueEvent(valueEventListener1);

                }

            }
        });
        //if the user chooses to sign up
        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getTextFields();
                if (user.length() == 0 || pass.length() == 0) {
                    Toast.makeText(Login.this, "One or More fields are empty", Toast.LENGTH_SHORT).show();
                } else {
                    //user initially does not exist
                    reff = FirebaseDatabase.getInstance().getReference("Users");
                    reff.addListenerForSingleValueEvent(valueEventListener2);

                }


            }
        });
        helpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), help.class);
                startActivity(intent);


            }
        });
    }


    public void displayToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLogin);
        //sets the toolbar title to the day selected
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
    }

    public void getTextFields() {
        // get text from input fields
        EditText mEdit1 = (EditText) findViewById(R.id.userField);
        EditText mEdit2 = (EditText) findViewById(R.id.passField);
        user = mEdit1.getText().toString();
        pass = mEdit2.getText().toString();
    }

    ValueEventListener valueEventListener1 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //clear the list of events
            userList.clear();
            if (dataSnapshot.exists()) {
                //for every item in the data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //create an event object and set values from the snapshot
                    User user = snapshot.getValue(User.class);
                    //add it to the list of events
                    userList.add(user);
                }
                    boolean signedIn = false;
                    for (User mUser : userList) {
                        String username = mUser.getUsername();
                        //if the username is in the database
                        if (username.equals(user)) {
                            //if the password matches
                            String password = mUser.getPassword();
                            if (password.equals(pass)) {
                                //get the sharecode of the user
                                String sharecode = mUser.getSharecode();
                                //pass data to intent
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                intent.putExtra("EXTRA_SHARE_CODE", sharecode);
                                //start activity
                                startActivity(intent);
                                //just in case
                                signedIn = true;
                                break;
                            }
                            else{
                                //otherwise break out
                                break;
                            }
                        }
                    }
                    if(signedIn == false) {
                        //will appear if the username or password is invlaid
                        Toast.makeText(Login.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }


        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }

    };

    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //clear the list of events
            userList.clear();
            if (dataSnapshot.exists()) {
                //for every item in the data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //create an event object and set values from the snapshot
                    User user = snapshot.getValue(User.class);
                    //add it to the list of events
                    userList.add(user);
                }
                boolean userExists = false;
                for (User mUser : userList) {
                    String username = mUser.getUsername();
                    //if the username is in the database
                    if (username.equals(user)) {
                        //the user exists
                        Toast.makeText(Login.this, "This user already exists", Toast.LENGTH_SHORT).show();
                        userExists = true;
                        break;
                    }

                }
                //if no user with that username exists
                if(userExists == false){
                    reff = FirebaseDatabase.getInstance().getReference("Users");
                    //generate a sharecode
                    share = randomAlphaNumeric(6);
                    Toast.makeText(Login.this, share, Toast.LENGTH_LONG).show();
                    //create new user object
                    User newUser = new User(user, pass, share);
                    //use .push to give object an id, add user to database
                    reff.push().setValue(newUser);
                    //toast
                    Toast.makeText(Login.this, "User Added", Toast.LENGTH_LONG).show();
                    //pass data to intent
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("EXTRA_SHARE_CODE", share);
                    //start activity
                    startActivity(intent);
                }


            }
        }


        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }

    };
    //generates a random string for share codes
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

}

