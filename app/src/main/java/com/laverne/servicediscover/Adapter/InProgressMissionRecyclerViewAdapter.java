package com.laverne.servicediscover.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.servicediscover.Entity.Mission;
import com.laverne.servicediscover.MissionMapActivity;
import com.laverne.servicediscover.R;

import java.util.ArrayList;
import java.util.List;

public class InProgressMissionRecyclerViewAdapter extends RecyclerView.Adapter<InProgressMissionRecyclerViewAdapter.ViewHolder>{

    private List<Mission> missionList;
    private Context context;

    //default constructor
    public InProgressMissionRecyclerViewAdapter() {
        missionList = new ArrayList<Mission>();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView addressTextView;
        public TextView distanceTextView;
        public Button viewBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.inprogress_mission_name);
            addressTextView = itemView.findViewById(R.id.inprogress_mission_address);
            distanceTextView = itemView.findViewById(R.id.inprogress_mission_distance);
            viewBtn = itemView.findViewById(R.id.mission_view_button);

            context = itemView.getContext();
        }
    }


    @NonNull
    @Override
    public InProgressMissionRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the view from XML layout file
        View missionView = inflater.inflate(R.layout.inprogress_mission_rv_layout, parent, false);
        // construct the viewholder with the new view
        ViewHolder viewHolder = new ViewHolder(missionView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InProgressMissionRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        final Mission mission = missionList.get(position);
        // viewholder binding with its data at the specified position
        TextView tvName = viewHolder.nameTextView;
        tvName.setText(mission.getName());
        TextView tvAddress = viewHolder.addressTextView;
        tvAddress.setText(mission.getAddress());
        TextView tvDistance = viewHolder.distanceTextView;
        float distance = mission.getDistance();
        if (distance < 1000) {
            tvDistance.setText("Distance: " + String.valueOf((int)distance) + "m");
        } else {
            tvDistance.setText("Distance: " + String.format("%.2f", distance * 0.001) + "km");
        }
        Button btnView = viewHolder.viewBtn;

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MissionMapActivity.class);
                intent.putExtra("id", mission.uid);
                intent.putExtra("name", mission.getName());
                intent.putExtra("latitude", mission.getLatitude());
                intent.putExtra("longitude", mission.getLongitude());
                context.startActivity(intent);
            }
        });
    }


    public void setMissionList(List<Mission> missionList) {
        this.missionList = missionList;
        notifyDataSetChanged();
    }


    public void updateList(List<Mission> missionList) {
        this.missionList = missionList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return missionList.size();
    }
}
