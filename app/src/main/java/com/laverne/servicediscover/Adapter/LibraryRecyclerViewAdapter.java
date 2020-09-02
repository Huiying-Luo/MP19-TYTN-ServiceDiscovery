package com.laverne.servicediscover.Adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.servicediscover.Model.Library;
import com.laverne.servicediscover.R;
import com.laverne.servicediscover.Utilities;

import org.w3c.dom.Text;

import java.util.List;

import okhttp3.internal.platform.android.UtilKt;

public class LibraryRecyclerViewAdapter extends RecyclerView.Adapter<LibraryRecyclerViewAdapter.ViewHolder> {
    private static final int REQUEST_CODE_CALL_PERMISSION = 2;

    private List<Library> liraries;
    private Context context;
    private boolean isHomeDistance;

    private OnPhoneCallListener phoneCallListener;

    public LibraryRecyclerViewAdapter(List<Library> libraries, boolean isHomeDistance) {
        this.liraries = libraries;
        this.isHomeDistance = isHomeDistance;
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
        public  boolean isHomeDistance;

        public ViewHolder(@NonNull View itemView, boolean isHomeDistance) {
            super(itemView);

            this.isHomeDistance = isHomeDistance;

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
        ViewHolder viewHolder = new ViewHolder(libraryView, isHomeDistance);
        return viewHolder;
    }


    // this method binds the view holder created with data that will be displayed
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Library library = liraries.get(position);
        // viewholder binding with its data at the specified position
        TextView tvName = viewHolder.nameTextView;
        tvName.setText(library.getName());
        TextView tvAddress = viewHolder.addressTextView;
        tvAddress.setText(library.getAddress());
        TextView tvDistance = viewHolder.distanceTextView;
        float distance = 0;
        if (isHomeDistance) {
            distance = library.getHomeDistance();
        } else {
            distance = library.getCurrentDistance();
        }
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

    public void updateList(List<Library> liraries) {
        this.liraries = liraries;
        notifyDataSetChanged();
    }


}
