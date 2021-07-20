package com.codepath.travel.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.travel.R;
import com.codepath.travel.TouristSpotsActivity;
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
        private ImageButton ibAddTouristDest;
        private ImageButton ibYelpPage;
        private TextView tvCommentCount;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivBusinessPicture = itemView.findViewById(R.id.ivBusinessPicture);
            tvBusinessName = itemView.findViewById(R.id.tvBusinessName);
            ivYelpRating = itemView.findViewById(R.id.ivYelpRating);
            ibAddTouristDest = itemView.findViewById(R.id.ibAddTouristDest);
            ibYelpPage = itemView.findViewById(R.id.ibYelpPage);
            tvCommentCount = itemView.findViewById(R.id.tvReviewCount);
        }

        public void bind(TouristSpot touristSpot) {
            tvBusinessName.setText(touristSpot.getBusinessName());
            String numReivews = String.valueOf(touristSpot.getReviewCount()) + " reviews";
            tvCommentCount.setText(numReivews);
            if (touristSpot.isChosen()) {
                ibAddTouristDest.setClickable(false);
                ibAddTouristDest.setImageResource(R.drawable.ic_baseline_check_24);
            } else {
                ibAddTouristDest.setClickable(true);
                ibAddTouristDest.setImageResource(R.drawable.ic_baseline_add_24);
                ibAddTouristDest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ibAddTouristDest.setClickable(false);
                        ibAddTouristDest.setImageResource(R.drawable.ic_baseline_check_24);
                        touristSpot.flipChosen();
                        TouristSpotsActivity.saveTouristDestination(touristSpot);
                    }
                });
            }
            ibYelpPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(touristSpot.getYelpURL()));
                    context.startActivity(i);
                }
            });
            if (!touristSpot.getImageURL().isEmpty()) {
                Glide.with(context).load(touristSpot.getImageURL()).override(500,500).into(ivBusinessPicture);
            }
            switch (touristSpot.getRating()) {
                case "1.0":
                    Glide.with(context).load(R.drawable.stars_extra_large_1).into(ivYelpRating);
                    break;
                case "1.5":
                    Glide.with(context).load(R.drawable.stars_extra_large_1_half).into(ivYelpRating);
                    break;
                case "2.0":
                    Glide.with(context).load(R.drawable.stars_extra_large_2).into(ivYelpRating);
                    break;
                case "2.5":
                    Glide.with(context).load(R.drawable.stars_extra_large_2_half).into(ivYelpRating);
                    break;
                case "3.0":
                    Glide.with(context).load(R.drawable.stars_extra_large_3).into(ivYelpRating);
                    break;
                case "3.5":
                    Glide.with(context).load(R.drawable.stars_extra_large_3_half).into(ivYelpRating);
                    break;
                case "4.0":
                    Glide.with(context).load(R.drawable.stars_extra_large_4).into(ivYelpRating);
                    break;
                case "4.5":
                    Glide.with(context).load(R.drawable.stars_extra_large_4_half).into(ivYelpRating);
                    break;
                case "5.0":
                    Glide.with(context).load(R.drawable.stars_extra_large_5).into(ivYelpRating);
                    break;
            }
        }
    }
}
