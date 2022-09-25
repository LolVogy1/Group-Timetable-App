package com.example.grouptimetable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    //can be shared across activities
    public static SharedPreferences sharedPreferences;
    public static String SEL_DAY;
    private static final int PERMISSION_REQUEST_STORAGE = 1000;
    private static final int READ_REQUEST_CODE = 42;
    DatabaseReference dbTimetable;
    String copyCode, title;
    List<String> sharedCodes;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get the user's sharecode from the intent
        final String sharecode = getIntent().getStringExtra("EXTRA_SHARE_CODE");
        //copy the string so that it can be used outside of this method
        copyCode = sharecode;
        //empty list of codes
        sharedCodes = new ArrayList<>();
        //if above a certain sdk version permission to access external stroage has to be given
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }

        //button to add events
        final Button button = findViewById(R.id.buttonMain);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //pressing the button moves to new activity
                Intent intent3 = new Intent(MainActivity.this, AddEvent.class);
                //put the sharecode in the intent
                intent3.putExtra("EXTRA_SHARE_CODE", sharecode);
                startActivity(intent3);
            }
        });
        //button to share code
        final Button buttonshare = findViewById(R.id.buttonshare);
        buttonshare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //pressing the button moves to new activity
                Intent intent3 = new Intent(MainActivity.this, Share.class);
                intent3.putExtra("EXTRA_SHARE_CODE", sharecode);
                startActivity(intent3);
            }
        });
        //button to import files
        final Button buttonImport = findViewById(R.id.buttonimport);
        buttonImport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                performFileSearch();
            }

            });
        //logout button
        final Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //pressing the button moves to new activity
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });

        //creates a file that stores the day selected
        sharedPreferences = getSharedPreferences("MY_DAY", MODE_PRIVATE);
        //create an array of days
        String[] days = {"Monday", "Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        //initialise an arrayadapter for the listview
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.activity_listview, days);
        //initialise the listview
        ListView mylv = (ListView) findViewById(R.id.lvMain);
        //initialise the toolbar
        setToolbar(copyCode);
        //bind the adapter to the listview
        mylv.setAdapter(adapter);
        //when a day is clicked move to a new activity with that day
        //each position in the Listview is a different day (monday = 0 , tuesday = 1 etc.)
        mylv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent2 = new Intent(MainActivity.this, DayTimetable.class);
                intent2.putExtra("EXTRA_SHARE_CODE", sharecode);
                switch (position) {
                    case 0: {
                        //start activity
                        startActivity(intent2);
                        //adds a string with the day selected to the file
                        sharedPreferences.edit().putString(SEL_DAY, "Monday").apply(); //adds a string with the day selected to the file
                        break;
                    }
                    case 1: {
                        //start activity
                        startActivity(intent2);
                        sharedPreferences.edit().putString(SEL_DAY, "Tuesday").apply();
                        break;
                    }
                    case 2: {
                        //start activity
                        startActivity(intent2);
                        sharedPreferences.edit().putString(SEL_DAY, "Wednesday").apply();
                        break;
                    }
                    case 3: {
                        //start activity
                        startActivity(intent2);
                        sharedPreferences.edit().putString(SEL_DAY, "Thursday").apply();
                        break;
                    }
                    case 4: {
                        //start activity
                        startActivity(intent2);
                        sharedPreferences.edit().putString(SEL_DAY, "Friday").apply();
                        break;
                    }
                    case 5: {
                        //start activity
                        startActivity(intent2);
                        sharedPreferences.edit().putString(SEL_DAY, "Saturday").apply();
                        break;
                    }
                    case 6: {
                        //start activity
                        startActivity(intent2);
                        sharedPreferences.edit().putString(SEL_DAY, "Sunday").apply();
                        break;
                    }
                    default:
                        break;
                }
            }
        });

    }
    public void setToolbar(String text){
        title = "Your timetable";
        DatabaseReference dbTitle = FirebaseDatabase.getInstance().getReference("Shares").child(text);
        dbTitle.addListenerForSingleValueEvent(valueEventListener);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
    }
    //gets a list of codes shared with the user
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //if data exists
            if(dataSnapshot.exists()) {
                //for each child/record in the table
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //gets the code as it is the key in this table
                    String userCode = snapshot.getKey();
                    //add it to the list of shared codes
                    sharedCodes.add(userCode);
                }
                //create a new instance of the database
                DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference("Users");
                dbUsers.addListenerForSingleValueEvent(valueEventListener2);

            }
        }


        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    //adds the shared users to the title
    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //create a user object with the current record
                    User user = snapshot.getValue(User.class);
                    //get their code and username
                    String userCode = user.getSharecode();
                    String userName = user.getUsername();
                    for(String code: sharedCodes){
                        //if their code is shared with the logged in user
                        if(code.equals(userCode)){
                            //add their username to the title
                            title = title + " +";
                            title = title + userName;
                        }

                    }
                }
                //set the title again
                getSupportActionBar().setTitle(title);
            }
        }


        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void addTimetable(String input){
        //get a file from the external storage using the path given
        File file = new File(Environment.getExternalStorageDirectory(),input);
        //get an instance of the users timetable from the database
        dbTimetable = FirebaseDatabase.getInstance().getReference("Timetable").child(copyCode);
        try{
            //create a scanner to read the file
            Scanner scanner = new Scanner(file);
            //separate by "-" or new line
            scanner.useDelimiter("-|\r\n");
            //for each line in the file (Day-event-start-end)
            while(scanner.hasNext()){
                //get values of event
                String tDay = scanner.next();
                String tName = scanner.next();
                String tStart = scanner.next();
                String tEnd = scanner.next();
                //convert start and end time strings to int
                int tStartInt = Integer.valueOf(tStart);
                int tEndInt = Integer.valueOf(tEnd);
                //if the times are invalid
                if(tStartInt >= tEndInt || tStartInt < 0 || tEndInt <0 || tStartInt > 24 || tEndInt > 24){
                    Toast.makeText(this,"invalid event found",Toast.LENGTH_SHORT).show();
                }
                else {
                    //create event object
                    Event tEvent = new Event(tName, tStartInt, tEndInt);
                    //add to database
                    dbTimetable.child(tDay).push().setValue(tEvent);
                }
            }
            Toast.makeText(this,"Timetable added",Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //select file from storage
    private void performFileSearch(){
        //intent to open text files
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    //get path of file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                //gets the path
                Uri uri = data.getData();
                String path = uri.getPath();
                path = path.substring(path.indexOf(":") + 1);
                if (path.contains("emulated")) {
                    path = path.substring(path.indexOf("0") + 1);
                }
                //calls method to add the timetable
                addTimetable(path);
            }
        }
    }

    @Override
    //makes a toast to show whether permission was granted
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_STORAGE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
               Toast.makeText(this, "Permission granted",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Permission denied",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}