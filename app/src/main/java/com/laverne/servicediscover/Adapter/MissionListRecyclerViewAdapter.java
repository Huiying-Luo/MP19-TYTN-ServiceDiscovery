package com.laverne.servicediscover.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.laverne.servicediscover.Entity.Mission;
import com.laverne.servicediscover.Model.Service;
import com.laverne.servicediscover.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MissionListRecyclerViewAdapter extends RecyclerView.Adapter<MissionListRecyclerViewAdapter.ViewHolder> {

    private List<Mission> missionList;
    private List<Mission> allMissions;
    private Context context;
    private OnMissionListener mOnMissionListener;

    //default constructor
    public MissionListRecyclerViewAdapter(OnMissionListener onMissionListener) {
        missionList = new ArrayList<Mission>();
        this.mOnMissionListener = onMissionListener;

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nameTextView;
        public TextView addressTextView;
        public TextView descriptionTextView;
        public TextView distanceTextView;
        public Chip museumTypeChip;
        public OnMissionListener onMissionListener;

        public ViewHolder(@NonNull View itemView, OnMissionListener onMissionListener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.add_mission_name);
            descriptionTextView = itemView.findViewById(R.id.mission_description);
            museumTypeChip = itemView.findViewById(R.id.mission_type_chip);
            addressTextView = itemView.findViewById(R.id.add_mission_address);
            distanceTextView = itemView.findViewById(R.id.add_mission_distance);
            this.onMissionListener = onMissionListener;

            itemView.setOnClickListener(this);
            context = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            onMissionListener.onMissionClick(getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public MissionListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the view from XML layout file
        View missionView = inflater.inflate(R.layout.add_mission_rv_layout, parent, false);
        // construct the viewholder with the new view
        MissionListRecyclerViewAdapter.ViewHolder viewHolder = new MissionListRecyclerViewAdapter.ViewHolder(missionView, mOnMissionListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MissionListRecyclerViewAdapter.ViewHolder viewHolder, int position) {
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

        TextView tvDescription = viewHolder.descriptionTextView;
        Chip typeChip = viewHolder.museumTypeChip;

        if (mission.getCategory() == 3) {
            tvDescription.setText(mission.getMuseumDescription());
            typeChip.setText(mission.getMuseumType());

            tvDescription.setVisibility(View.VISIBLE);
            typeChip.setVisibility(View.VISIBLE);
        } else {
            tvDescription.setVisibility(View.GONE);
            typeChip.setVisibility(View.GONE);
        }
    }


    public void setMissionList(List<Mission> missionList) {
        this.missionList = missionList;
        allMissions = new ArrayList<>(missionList);
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


    public interface OnMissionListener {
        void onMissionClick(int position);
    }


    public void filterBySchoolType(int schoolType) {
        List<Mission> filteredList = new ArrayList<>();

        if (schoolType == 0) {
            filteredList.addAll(allMissions);
        } else if (schoolType == 1 || schoolType == 2){
            for (Mission mission: allMissions) {
                if (mission.getSchoolType() == schoolType - 1 || mission.getSchoolType() == 2) {
                    filteredList.add(mission);
                }
            }
        } else {
            for (Mission mission: allMissions) {
                if (mission.getSchoolType() == schoolType) {
                    filteredList.add(mission);
                }
            }
        }

        missionList.clear();
        missionList.addAll(filteredList);
        notifyDataSetChanged();
    }


    public void filterByMuseumTypes(ArrayList<String> selectedMuseumTypes) {
        List<Mission> filteredList = new ArrayList<>();

        for (Mission mission: allMissions) {
            if (selectedMuseumTypes.contains(mission.getMuseumType())) {
                filteredList.add(mission);
            }
        }

        missionList.clear();
        missionList.addAll(filteredList);
        notifyDataSetChanged();
    }
}
