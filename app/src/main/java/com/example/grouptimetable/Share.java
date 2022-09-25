package com.example.grouptimetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Share extends AppCompatActivity {
    DatabaseReference reff, reff2, sharereff, userreff;
    List<String> friendcodes;
    boolean userExists, foundCode;
    List<User> userList;
    String otherCode, copyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        final String sharecode = getIntent().getStringExtra("EXTRA_SHARE_CODE");
        copyCode = sharecode;
        displayToolbar();
        setTextView(sharecode);
        //empty list of codes
        friendcodes = new ArrayList<>();
        //empty list of users
        userList = new ArrayList<>();

        Button button = findViewById(R.id.buttonsubmitcode);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //no existing user or code has been found at this point
                userExists = false;
                foundCode = false;
                //fill textview
                EditText codeText = (EditText) findViewById(R.id.codeinputfield);
                otherCode = codeText.getText().toString();
                //get database
                reff = FirebaseDatabase.getInstance().getReference();
                //get data of codes shared with the user already
                sharereff = reff.child("Shares").child(sharecode);
                //get shares tree
                reff2 = FirebaseDatabase.getInstance().getReference("Shares");
                //get users tree
                userreff = reff.child("Users");
                //make sure you don't enter your own code
                if(otherCode.equals(sharecode)) {
                    Toast.makeText(Share.this, "That's your own code", Toast.LENGTH_SHORT).show();
                }
                else{
                    userreff.addListenerForSingleValueEvent(valueEventListener);
                }
            }

        });
    }


    public void displayToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarshare);
        //sets the toolbar title to the day selected
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Share your code");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setTextView(String code) {
        TextView myTextView = (TextView) findViewById(R.id.mysharecode);
        myTextView.setText(code);
    }
    //checks if user exists
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //clear the list of events
            //if data exists
            if (dataSnapshot.exists()) {
                //for every item in the data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //get list of all users
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }
                //check if the code/user exists in list of users
                for (User qUser : userList) {
                    String code = qUser.getSharecode();
                    if (code.equals(otherCode)) {
                        //set true if the user exists
                        Toast.makeText(Share.this, "USER EXISTS", Toast.LENGTH_SHORT).show();
                        userExists = true;
                    }
                }
                //now check if the sharecode has been entered
                sharereff.addListenerForSingleValueEvent(valueEventListener2);
            }
        }


        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    //checks if the sharecode has been entered already
    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //if data exists
            if (dataSnapshot.exists()) {
                //for every item in the data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //get a list of all the codes shared with the user
                    String code = snapshot.getKey();
                    friendcodes.add(code);
                }

            }
            //for every code in the list of shared codes
            for (String code : friendcodes) {
                //if the code has already been shared
                if (code.equals(otherCode)) {
                    //set to true
                    Toast.makeText(Share.this, "You've already got this code", Toast.LENGTH_SHORT).show();
                    foundCode = true;
                    break;
                }
            }
            //if the code hasn't been entered already
            if (foundCode == false) {
                //if the user exists
                if (userExists == true) {
                    //add the shared code to the codes shared with the user
                    sharereff.child(otherCode).setValue(true);
                    //repeat for the other user
                    reff2.child(otherCode).child(copyCode).setValue(true);
                    Toast.makeText(Share.this, "Yay! You've added another user's timetable", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Share.this, "This user does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}


