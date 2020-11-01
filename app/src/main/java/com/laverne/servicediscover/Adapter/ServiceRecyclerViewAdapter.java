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

import com.google.android.material.chip.Chip;
import com.laverne.servicediscover.Model.Service;
import com.laverne.servicediscover.R;

import java.util.ArrayList;
import java.util.List;


public class ServiceRecyclerViewAdapter extends RecyclerView.Adapter<ServiceRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<Service> services;
    private List<Service> allServices;
    private Context context;
    private OnServiceListener mOnServiceListener;

    public ServiceRecyclerViewAdapter(List<Service> services, OnServiceListener onServiceListener) {
        this.services = services;
        allServices = new ArrayList<>(services);
        this.mOnServiceListener = onServiceListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView addressTextView;
        public TextView distanceTextView;
        public Chip typeChip;
        public OnServiceListener onServiceListener;

        public ViewHolder(@NonNull View itemView, OnServiceListener onServiceListener) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.service_name);
            descriptionTextView = itemView.findViewById(R.id.service_description);
            addressTextView = itemView.findViewById(R.id.service_address);
            distanceTextView = itemView.findViewById(R.id.service_distance);
            typeChip = itemView.findViewById(R.id.type_chip);
            this.onServiceListener = onServiceListener;

            itemView.setOnClickListener(this);
            context = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            onServiceListener.onServiceClick(getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    @NonNull
    @Override
    public ServiceRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the view from XML layout file
        View libraryView = inflater.inflate(R.layout.service_rv_layout, parent, false);
        // construct the viewholder with the new view
        ViewHolder viewHolder = new ViewHolder(libraryView, mOnServiceListener);
        return viewHolder;
    }


    // this method binds the view holder created with data that will be displayed
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Service service = services.get(position);
        // viewholder binding with its data at the specified position
        TextView tvName = viewHolder.nameTextView;
        tvName.setText(service.getName());
        TextView tvDescription = viewHolder.descriptionTextView;
        TextView tvAddress = viewHolder.addressTextView;
        tvAddress.setText(service.getAddress());
        TextView tvDistance = viewHolder.distanceTextView;
        Chip chipType = viewHolder.typeChip;
        float distance = 0;
        distance = service.getCurrentDistance();

        if (distance < 1000) {
            tvDistance.setText("Distance: " + String.valueOf((int)distance) + "m");
        } else {
            tvDistance.setText("Distance: " + String.format("%.2f", distance * 0.001) + "km");
        }
            if (service.getCategory() == 3){
                chipType.setVisibility(View.VISIBLE);
                tvDescription.setVisibility(View.VISIBLE);
                chipType.setText(service.getMuseumType());
                // set the description
                tvDescription.setText(service.getMuseumDescription());
            }
    }


    public void updateList(List<Service> services) {
        this.services = services;
        allServices = new ArrayList<>(services);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return serviceFilter;
    }

    private Filter serviceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Service> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // search input is empty, return all
                filteredList.addAll(allServices);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Service service: allServices) {
                    if (service.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(service);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return  results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            services.clear();
            services.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };


    public interface OnServiceListener {
        void onServiceClick(int position);
    }
}
