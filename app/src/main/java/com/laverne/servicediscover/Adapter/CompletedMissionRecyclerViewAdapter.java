package com.laverne.servicediscover.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.servicediscover.Entity.Mission;
import com.laverne.servicediscover.R;

import java.util.ArrayList;
import java.util.List;

public class CompletedMissionRecyclerViewAdapter extends RecyclerView.Adapter<CompletedMissionRecyclerViewAdapter.ViewHolder>{


    private List<Mission> missionList;
    private Context context;

    //default constructor
    public CompletedMissionRecyclerViewAdapter() {
        missionList = new ArrayList<Mission>();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView addressTextView;
        public TextView completeDateTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.completed_mission_name);
            addressTextView = itemView.findViewById(R.id.completed_mission_address);
            completeDateTextView = itemView.findViewById(R.id.completed_mission_date);

            context = itemView.getContext();
        }
    }


    @NonNull
    @Override
    public CompletedMissionRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the view from XML layout file
        View missionView = inflater.inflate(R.layout.completed_mission_rv_layout, parent, false);
        // construct the viewholder with the new view
        CompletedMissionRecyclerViewAdapter.ViewHolder viewHolder = new CompletedMissionRecyclerViewAdapter.ViewHolder(missionView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedMissionRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        final Mission mission = missionList.get(position);
        // viewholder binding with its data at the specified position
        TextView tvName = viewHolder.nameTextView;
        tvName.setText(mission.getName());
        TextView tvAddress = viewHolder.addressTextView;
        tvAddress.setText(mission.getAddress());
        TextView tvDate = viewHolder.completeDateTextView;
        tvDate.setText("Completed Date: " + mission.getCompleteDate());
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
