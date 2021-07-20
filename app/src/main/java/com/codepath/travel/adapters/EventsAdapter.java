package com.codepath.travel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.CalendarUtils;
import com.codepath.travel.R;
import com.codepath.travel.models.Event;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Event> events;

    public EventsAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @NotNull
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EventsAdapter.ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvEventName;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvEventName = itemView.findViewById(R.id.tvEventName);
        }

        public void bind(Event event) {
            String eventTitle = event.getName() + " " + CalendarUtils.formatTime(event.getTime());
            tvEventName.setText(eventTitle);
        }
    }
}
