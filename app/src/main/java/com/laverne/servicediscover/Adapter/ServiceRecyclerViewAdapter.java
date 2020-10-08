package com.laverne.servicediscover.Adapter;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.servicediscover.Model.Service;
import com.laverne.servicediscover.R;

import java.util.ArrayList;
import java.util.List;


public class ServiceRecyclerViewAdapter extends RecyclerView.Adapter<ServiceRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<Service> liraries;
    private List<Service> allLibraries;
    private Context context;
    private OnPhoneCallListener phoneCallListener;

    public ServiceRecyclerViewAdapter(List<Service> libraries) {

        this.liraries = libraries;
        allLibraries = new ArrayList<>(libraries);
    }


    public void setPhoneCallListener(OnPhoneCallListener listener) {
        this.phoneCallListener = listener;
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
    public ServiceRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the view from XML layout file
        View libraryView = inflater.inflate(R.layout.service_rv_layout, parent, false);
        // construct the viewholder with the new view
        ViewHolder viewHolder = new ViewHolder(libraryView);
        return viewHolder;
    }


    // this method binds the view holder created with data that will be displayed
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Service library = liraries.get(position);
        // viewholder binding with its data at the specified position
        TextView tvName = viewHolder.nameTextView;
        tvName.setText(library.getName());
        TextView tvAddress = viewHolder.addressTextView;
        tvAddress.setText(library.getAddress());
        TextView tvDistance = viewHolder.distanceTextView;
        float distance = 0;
        distance = library.getCurrentDistance();

        if (distance < 1000) {
            tvDistance.setText("Distance: " + String.valueOf((int)distance) + "m");
        } else {
            tvDistance.setText("Distance: " + String.format("%.2f", distance * 0.001) + "km");
        }
        final String phoneNo = library.getPhoneNo();
        final String website = library.getWebsite();
        final Button callBtn = viewHolder.callButton;
        Button websiteBtn = viewHolder.websiteButton;

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneCallListener.onPhoneCallClick(phoneNo);


            }
        });

        websiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open website
                Uri uri = Uri.parse(website);
                Intent launchWeb = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(launchWeb);
            }
        });
    }


    public interface OnPhoneCallListener{
        void onPhoneCallClick(String phoneNo);
    }

    public void updateList(List<Service> liraries) {
        this.liraries = liraries;
        allLibraries = new ArrayList<>(liraries);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return libraryFilter;
    }

    private Filter libraryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Service> filteredList = new ArrayList<>();


            if (constraint == null || constraint.length() == 0) {
                // search input is empty, return all
                filteredList.addAll(allLibraries);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Service library: allLibraries) {
                    if (library.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(library);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            liraries.clear();
            liraries.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
