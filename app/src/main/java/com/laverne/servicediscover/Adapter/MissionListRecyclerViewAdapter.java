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

public class MissionListRecyclerViewAdapter extends RecyclerView.Adapter<MissionListRecyclerViewAdapter.ViewHolder>{

    private List<Mission> missionList;
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
        public OnMissionListener onMissionListener;

        public ViewHolder(@NonNull View itemView, OnMissionListener onMissionListener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.add_mission_name);
            addressTextView = itemView.findViewById(R.id.add_mission_address);
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


    public interface OnMissionListener {
        void onMissionClick(int position);
    }
}
