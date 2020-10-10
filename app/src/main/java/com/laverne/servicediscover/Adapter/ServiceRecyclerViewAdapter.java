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

    private List<Service> services;
    private List<Service> allServices;
    private Context context;
    private OnPhoneCallListener phoneCallListener;

    public ServiceRecyclerViewAdapter(List<Service> services) {

        this.services = services;
        allServices = new ArrayList<>(services);
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
        ViewHolder viewHolder = new ViewHolder(libraryView);
        return viewHolder;
    }


    // this method binds the view holder created with data that will be displayed
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Service service = services.get(position);
        // viewholder binding with its data at the specified position
        TextView tvName = viewHolder.nameTextView;
        tvName.setText(service.getName());
        TextView tvAddress = viewHolder.addressTextView;
        tvAddress.setText(service.getAddress());
        TextView tvDistance = viewHolder.distanceTextView;
        float distance = 0;
        distance = service.getCurrentDistance();

        if (distance < 1000) {
            tvDistance.setText("Distance: " + String.valueOf((int)distance) + "m");
        } else {
            tvDistance.setText("Distance: " + String.format("%.2f", distance * 0.001) + "km");
        }
        Button callBtn = viewHolder.callButton;
        Button websiteBtn = viewHolder.websiteButton;
        // park does not have website and phone no
        if (service.getCategory() != 2) {
            final String phoneNo = service.getPhoneNo();
            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phoneCallListener.onPhoneCallClick(phoneNo);
                }
            });
            // Disable the website button for Adult English Programs
            if (service.getCategory() == 1 && service.getSchoolType() == 4) {
                websiteBtn.setEnabled(false);
            } else {
                websiteBtn.setEnabled(true);
                final String website = service.getWebsite();

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

        } else {
            // make the buttons invisible when display parks
            callBtn.setVisibility(View.GONE);
            websiteBtn.setVisibility(View.GONE);
        }
    }


    public interface OnPhoneCallListener{
        void onPhoneCallClick(String phoneNo);
    }

    public void updateList(List<Service> services) {
        this.services = services;
        allServices = new ArrayList<>(services);
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
                filteredList.addAll(allServices);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Service library: allServices) {
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
            services.clear();
            services.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
