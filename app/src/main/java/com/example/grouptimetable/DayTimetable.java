package com.example.grouptimetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;

import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DayTimetable extends AppCompatActivity {

    List<Event> eventList;
    //set up recyclerView
    private RecyclerView recyclerView;
    private EventAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView textView;
    //variables
    //get the day selected from sharedpreferences
    String Day = MainActivity.sharedPreferences.getString(MainActivity.SEL_DAY, null);
    String freeTime;
    List<Integer> notFreeList;
    List<String> codeList;
    DatabaseReference dbEvent, otherEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_timetable);
        //initialise variables
        String sharecode = getIntent().getStringExtra("EXTRA_SHARE_CODE");
        freeTime = "";
        notFreeList = new ArrayList<>();
        codeList = new ArrayList<>();
        //set up views
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        textView = (TextView) findViewById(R.id.view_compare);
        displayToolBar();
        //recyclerView size is fixed, increases performance
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //list of events
        eventList = new ArrayList<>();
        mAdapter = new EventAdapter(eventList);
        recyclerView.setAdapter(mAdapter);
        //get the day that was selected
        //get only events for the selected day
        Query query = FirebaseDatabase.getInstance().getReference("Timetable").child(sharecode).child(Day) // .child will only get the selected day
                //order by start time (chronological order)
                .orderByChild("eventStart");
        //only need to load once, could be changed
        query.addListenerForSingleValueEvent(valueEventListener);
        //gets snapshot of users that are sharing their timetables with the user
        dbEvent = FirebaseDatabase.getInstance().getReference("Shares").child(sharecode);
        //gets a snapshot of the timetable tree
        otherEvents = FirebaseDatabase.getInstance().getReference("Timetable");

    }

    private void displayToolBar() {
        //get toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarday);
        //sets the toolbar title to the day selected
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(MainActivity.sharedPreferences.getString(MainActivity.SEL_DAY, null));
        //enables back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //get list of events
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //clear the list of events
            eventList.clear();
            if(dataSnapshot.exists()) {
                //for every item in the data
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //create an event object and set values from the snapshot
                    Event event = snapshot.getValue(Event.class);
                    //get start and end time of each event
                    int start = event.getEventStart();
                    int end = event.getEventEnd();
                    //add time between start and end-1 to list of times not free
                    for(int i = start; i < end; i++){
                        //if the time is not in the list
                        if(notFreeList.contains(i) == false){
                            notFreeList.add(i);
                        }
                    }
                    //add it to the list of events
                    eventList.add(event);
                }
                //tells the adapter that the data has changed so it refreshes
                mAdapter.notifyDataSetChanged();
                //event listeners are nested so that the functions execute in order
                dbEvent.addListenerForSingleValueEvent(valueEventListener2);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    //get shared codes
    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //clear the list of events
            if(dataSnapshot.exists()) {
                //for every item in the data
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //get id of object
                    String code = snapshot.getKey();
                    //get timetable of other user and compare timetables
                    otherEvents.child(code).child(Day).addListenerForSingleValueEvent(valueEventListener3);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    //compare times
    ValueEventListener valueEventListener3 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //clear the list of events
            if(dataSnapshot.exists()) {
                //for every item in the data
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //create an event object and set values from the snapshot
                    Event event = snapshot.getValue(Event.class);
                    //get start and end time of each event
                    int start = event.getEventStart();
                    int end = event.getEventEnd();
                    //add time between start and end-1 to list of times not free
                    for(int i = start; i < end; i++){
                        //if the time is not in the list
                        if(notFreeList.contains(i) == false){
                            notFreeList.add(i);
                        }
                    }
                    //add it to the list of events
                }
                //gets a string with the times all users are available from
                freeTime = compareTimes(notFreeList);
                textView.setText(freeTime);

            }
            //
            else{
                freeTime = compareTimes(notFreeList);
                textView.setText(freeTime);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    //find times when everyone is free and create a string with all free timeslots
    public String compareTimes(List<Integer> nFList){
        //integer list of unavailable times
        List<Integer> nFL = nFList;
        String freeTime = "You are all free from: ";
        //to temporarily store values
        int timeContainer = 0;
        //so if true it won't add numbers to the string
        boolean startFree = false;
        //assume shared day is from 9am-9pm
        for(int i = 9 ; i < 22; i++){
            //if this not in the list then it is free
            //if startFree is false then it knows that this time is free
            if(nFL.contains(i) == false && startFree == false ){
                timeContainer = i;
                startFree = true;
            }
            //if it is in the list and startFree is true then you are not free from here
            else if(nFL.contains(i) && startFree == true){
                //add the free time to a string
                 freeTime =  freeTime + String.valueOf(timeContainer)+"-"+String.valueOf(i)+", ";
                startFree = false;
            }
            //free time until the end of the day
            else if(i == 21 && startFree == true){
                freeTime =  freeTime + String.valueOf(timeContainer)+"-"+String.valueOf(i);
                startFree = false;
            }
            else{
                //else keep iterating
            }
        }
        //return string
        return freeTime;
    }


    }

