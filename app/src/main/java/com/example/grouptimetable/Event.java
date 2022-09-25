package com.example.grouptimetable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Event {
    public String eventName;
    public int eventStart;
    public int eventEnd;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }
    //construct object
    public Event(String EventName, int EventStart, int EventEnd ) {
        this.eventName = EventName;
        this.eventStart = EventStart;
        this.eventEnd = EventEnd;
    }
    //getters
    public String getEventName(){
        return eventName;

    }

    public int getEventStart(){
        return eventStart;

    }
    public int getEventEnd(){
        return eventEnd;

    }
}

