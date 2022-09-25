package com.example.grouptimetable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEvent extends AppCompatActivity {

    DatabaseReference mDatabase;
    String Day;
    Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event2);
        //initialise the toolbar
        displayToolbar();
        //create a spinner with the days as options
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String[] days = {"Monday", "Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        //test
        final String sharecode = getIntent().getStringExtra("EXTRA_SHARE_CODE");
        //spinner arrayadapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
        spinner.setAdapter(adapter);
        //when an item is selected in the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Day = spinner.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button button = findViewById(R.id.buttonSubmit);
        button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                // get text from input fields
               EditText mEdit1 = (EditText) findViewById(R.id.NameField);
               EditText mEdit2 = (EditText) findViewById(R.id.startinputfield);
               EditText mEdit3 = (EditText) findViewById(R.id.endinputfield);
               String Name = mEdit1.getText().toString();
               String Start = mEdit2.getText().toString();
               String End = mEdit3.getText().toString();
               //make sure fields aren't empty
               if(mEdit1.length() == 0 || mEdit2.length() == 0 || mEdit3.length() == 0){
                   Toast.makeText(AddEvent.this, "One or More fields are empty", Toast.LENGTH_SHORT).show();
               }

               else{
                       Integer StartInt = Integer.valueOf(Start);
                       Integer EndInt = Integer.valueOf(End);
                       //correct 24 to 0
                       if(StartInt == 24){
                           StartInt = 0;
                       }
                       if(EndInt == 24){
                           EndInt = 0;
                       }
                   //start time can't be after/same as end time or outside 0-23
                   if(StartInt >= EndInt || StartInt < 0 || EndInt <0 || StartInt > 24 || EndInt > 24) {
                       Toast.makeText(AddEvent.this, "Invalid Times", Toast.LENGTH_SHORT).show();
                   }
                   //submit fields
                   else {
                       //create/point at tree with value of the day selected
                       //each tree contains the events of a certain day
                       //this is because firebase database can only query a single field and it needs to be ordered by start time
                       mDatabase = FirebaseDatabase.getInstance().getReference("Timetable").child(sharecode).child(Day);
                       //create new event object
                       event = new Event(Name, StartInt,EndInt);
                       //use .push to give object an id, add event to database
                       mDatabase.push().setValue(event);
                       Toast.makeText(AddEvent.this, "Event Added", Toast.LENGTH_SHORT).show();


                   }
               }

            }
        });
    }

    public void displayToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarevent);
        //sets the toolbar title to the day selected
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


}
