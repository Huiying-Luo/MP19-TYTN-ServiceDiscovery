package com.laverne.servicediscover.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.servicediscover.Model.Library;
import com.laverne.servicediscover.R;

import org.w3c.dom.Text;

import java.util.List;

public class LibraryRecyclerViewAdapter extends RecyclerView.Adapter<LibraryRecyclerViewAdapter.ViewHolder> {

    private List<Library> liraries;
    private Context context;

    public LibraryRecyclerViewAdapter(List<Library> libraries) {
        this.liraries = libraries;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView addressTextView;
        public TextView distanceTextView;
        public Button callButton;
        public Button websiteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.library_name);
            addressTextView = itemView.findViewById(R.id.library_address);
            distanceTextView = itemView.findViewById(R.id.library_distance);
            callButton = itemView.findViewById(R.id.library_call_btn);
            websiteButton = itemView.findViewById(R.id.library_website_btn);

            context = itemView.getContext();
        }
    }

    @Override
    public int getItemCount() {
        return liraries.size();
    }

    @NonNull
    @Override
    public LibraryRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the view from XML layout file
        View libraryView = inflater.inflate(R.layout.library_rv_layout, parent, false);
        // construct the viewholder with the new view
        ViewHolder viewHolder = new ViewHolder(libraryView);
        return viewHolder;
    }


    // this method binds the view holder created with data that will be displayed
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Library library = liraries.get(position);
        // viewholder binding with its data at the specified position
        TextView tvName = viewHolder.nameTextView;
        tvName.setText(library.getName());
        TextView tvAddress = viewHolder.addressTextView;
        tvAddress.setText(library.getAddress());
        TextView tvDistance = viewHolder.distanceTextView;
        tvDistance.setText(library.getDistance());
        String phoneNo = library.getPhoneNo();
        String website = library.getWebsite();
        Button callBtn = viewHolder.callButton;
        Button websiteBtn = viewHolder.websiteButton;

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make phone call
            }
        });

        websiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open website
            }
        });
    }


    public void updateList(List<Library> liraries) {
        this.liraries = liraries;
        notifyDataSetChanged();
    }
}
