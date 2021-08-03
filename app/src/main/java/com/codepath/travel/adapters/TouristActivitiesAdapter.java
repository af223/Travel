package com.codepath.travel.adapters;

import android.content.Context;
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
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.TouristDestination;
import com.codepath.travel.models.YelpData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TouristActivitiesAdapter extends RecyclerView.Adapter<TouristActivitiesAdapter.ViewHolder> {

    private final Context context;
    private final List<YelpData> touristSpots;
    private final Destination destination;
    private final Boolean isRestaurant;
    private final ArrayList<TouristDestination> storedTouristSpots;

    public TouristActivitiesAdapter(Context context, List<YelpData> touristSpots, Destination destination, Boolean isRestaurant, ArrayList<TouristDestination> storedTouristSpots) {
        this.context = context;
        this.touristSpots = touristSpots;
        this.destination = destination;
        this.isRestaurant = isRestaurant;
        this.storedTouristSpots = storedTouristSpots;
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
        YelpData touristSpot = touristSpots.get(position);
        holder.bind(touristSpot);
    }

    @Override
    public int getItemCount() {
        return touristSpots.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivBusinessPicture;
        private final TextView tvBusinessName;
        private final ImageView ivYelpRating;
        private final ImageButton ibAddTouristDest;
        private final ImageButton ibYelpPage;
        private final TextView tvCommentCount;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivBusinessPicture = itemView.findViewById(R.id.ivBusinessPicture);
            tvBusinessName = itemView.findViewById(R.id.tvBusinessName);
            ivYelpRating = itemView.findViewById(R.id.ivYelpRating);
            ibAddTouristDest = itemView.findViewById(R.id.ibAddTouristDest);
            ibYelpPage = itemView.findViewById(R.id.ibYelpPage);
            tvCommentCount = itemView.findViewById(R.id.tvReviewCount);
        }

        public void bind(YelpData touristSpot) {
            tvBusinessName.setText(touristSpot.getBusinessName());
            if (touristSpot.getReviewCount() < 0) {
                tvCommentCount.setVisibility(View.GONE);
            } else {
                String numReviews = touristSpot.getReviewCount() + " reviews";
                tvCommentCount.setText(numReviews);
                tvCommentCount.setVisibility(View.VISIBLE);
            }
            if (touristSpot.isChosen()) {
                if (storedTouristSpots != null) {
                    ibAddTouristDest.setImageResource(R.drawable.ic_baseline_delete_24);
                    ibAddTouristDest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = getAdapterPosition();
                            storedTouristSpots.get(position).deleteInBackground();
                            storedTouristSpots.remove(position);
                            touristSpots.remove(position);
                            notifyItemRemoved(position);
                        }
                    });
                } else {
                    ibAddTouristDest.setClickable(false);
                    ibAddTouristDest.setImageResource(R.drawable.ic_baseline_check_24);
                }
            } else {
                ibAddTouristDest.setClickable(true);
                ibAddTouristDest.setImageResource(R.drawable.ic_baseline_add_24);
                ibAddTouristDest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ibAddTouristDest.setClickable(false);
                        ibAddTouristDest.setImageResource(R.drawable.ic_baseline_check_24);
                        touristSpot.flipChosen();
                        YelpData.saveTouristDestination(touristSpot, destination, isRestaurant);
                    }
                });
            }
            if (touristSpot.getImageURL() != null && !touristSpot.getImageURL().isEmpty()) {
                // prevents item reordering when scrolling up in staggeredgridlayout
                Glide.with(context).load(R.drawable.no_photo_placeholder).into(ivBusinessPicture);
                ivBusinessPicture.layout(0, 0, 0, 0);
                Glide.with(context).load(touristSpot.getImageURL()).into(ivBusinessPicture);
            }
            YelpData.linkToYelp(touristSpot.getYelpURL(), ibYelpPage, context);
            if (touristSpot.getRating() == null) {
                ivYelpRating.setVisibility(View.GONE);
            } else {
                ivYelpRating.setVisibility(View.VISIBLE);
                YelpData.displayRatingBar(ivYelpRating, touristSpot.getRating(), context);
            }
        }
    }
}
