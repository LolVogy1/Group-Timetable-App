package com.example.grouptimetable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
//create adapter extending from recyclerview adapter
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    //list of events
    private List<Event> eventList;

    //reference each view within a data item
    class EventViewHolder extends RecyclerView.ViewHolder{
        //all views that are set in each row
        TextView textViewName, textViewStart, textViewEnd;
        //construct that accepts the entire item row
        //does view lookups
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.EventView);
            textViewStart = itemView.findViewById(R.id.StartView);
            textViewEnd = itemView.findViewById(R.id.EndView);
        }
    }
    //pass list into constructor
    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    //inflate XML layout and return holder
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context contxt = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(contxt);
            //inflate a layout
            View eventView = inflater.inflate(R.layout.day_listview, parent, false);
        //return new holder instance
        EventViewHolder vh = new EventViewHolder(eventView);
        return vh;
    }
    //populate data into item through holder
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        //get data from current position
        Event event = eventList.get(position);
        //set view text using data
        holder.textViewName.setText(event.getEventName());
        holder.textViewStart.setText(String.valueOf(event.getEventStart()));
        holder.textViewEnd.setText(String.valueOf(event.getEventEnd()));

    }
    //provides size of list
    @Override
    public int getItemCount() {
        return eventList.size();
    }

}
