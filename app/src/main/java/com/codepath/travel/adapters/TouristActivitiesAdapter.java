package com.codepath.travel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.travel.R;
import com.codepath.travel.models.TouristSpot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TouristActivitiesAdapter extends RecyclerView.Adapter<TouristActivitiesAdapter.ViewHolder> {

    private final Context context;
    private final List<TouristSpot> touristSpots;

    public TouristActivitiesAdapter(Context context, List<TouristSpot> touristSpots) {
        this.context = context;
        this.touristSpots = touristSpots;
    }

    @NonNull
    @NotNull
    @Override
    public TouristActivitiesAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tourist_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TouristActivitiesAdapter.ViewHolder holder, int position) {
        TouristSpot touristSpot = touristSpots.get(position);
        holder.bind(touristSpot);
    }

    @Override
    public int getItemCount() {
        return touristSpots.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivBusinessPicture;
        private TextView tvBusinessName;
        private ImageView ivYelpRating;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivBusinessPicture = itemView.findViewById(R.id.ivBusinessPicture);
            tvBusinessName = itemView.findViewById(R.id.tvBusinessName);
            ivYelpRating = itemView.findViewById(R.id.ivYelpRating);
        }

        public void bind(TouristSpot touristSpot) {
            Glide.with(context).load(touristSpot.getImageURL()).override(500,500).into(ivBusinessPicture);
            tvBusinessName.setText(touristSpot.getBusinessName());
        }
    }
}
