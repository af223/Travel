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
import com.codepath.travel.models.YelpData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * This adapter is for the RecyclerView in TransportationActivity that displays the popular modes
 * of transportation in the chosen location.
 */

public class TransportationsAdapter extends RecyclerView.Adapter<TransportationsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<YelpData> transportations;

    public TransportationsAdapter(Context context, ArrayList<YelpData> transportations) {
        this.context = context;
        this.transportations = transportations;
    }

    @NonNull
    @NotNull
    @Override
    public TransportationsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transportation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TransportationsAdapter.ViewHolder holder, int position) {
        YelpData transport = transportations.get(position);
        holder.bind(transport);
    }

    @Override
    public int getItemCount() {
        return transportations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivTransport;
        private final TextView tvTransportName;
        private final ImageView ivYelpRating;
        private final TextView tvTransportPhone;
        private final ImageButton ibYelpPage;
        private final TextView tvTransportAddress;
        private final ImageView ivPhone;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivTransport = itemView.findViewById(R.id.ivTransport);
            tvTransportName = itemView.findViewById(R.id.tvTransportName);
            ivYelpRating = itemView.findViewById(R.id.ivYelpRating);
            tvTransportPhone = itemView.findViewById(R.id.tvTransportPhone);
            ibYelpPage = itemView.findViewById(R.id.ibToYelp);
            tvTransportAddress = itemView.findViewById(R.id.tvTransportAddress);
            ivPhone = itemView.findViewById(R.id.ivPhone);
        }

        public void bind(YelpData transport) {
            tvTransportName.setText(transport.getBusinessName());
            if (transport.getPhone().isEmpty()) {
                tvTransportPhone.setVisibility(View.GONE);
                ivPhone.setVisibility(View.GONE);
            } else {
                tvTransportPhone.setVisibility(View.VISIBLE);
                tvTransportPhone.setText(transport.getPhone());
                ivPhone.setVisibility(View.VISIBLE);
            }
            tvTransportAddress.setText(transport.getAddress());
            YelpData.displayRatingBar(ivYelpRating, transport.getRating(), context);
            Glide.with(context).load(transport.getImageURL()).placeholder(R.drawable.no_photo_placeholder)
                    .error(R.drawable.no_photo_placeholder).into(ivTransport);
            YelpData.linkToYelp(transport.getYelpURL(), ibYelpPage, context);
        }
    }
}
