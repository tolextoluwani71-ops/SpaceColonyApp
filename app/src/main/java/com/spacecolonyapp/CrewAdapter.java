package com.spacecolonyapp;

import android.graphics.Color;
import android.view.*;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import com.spacecolony.backend.CrewMember;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.ViewHolder> {

    private List<CrewMember> data = new ArrayList<>();
    private CrewMember selected;

    public void setData(List<CrewMember> list) {
        data = list;
        selected = null; // reset selection when data changes
        notifyDataSetChanged();
    }

    public CrewMember getSelected() {
        return selected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CrewMember m = data.get(position);

        holder.name.setText(m.getName());
        holder.stats.setText("Energy: " + m.getEnergy() + " XP: " + m.getExperience());


        if (m == selected) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }


        holder.itemView.setOnClickListener(v -> {
            selected = m;
            notifyDataSetChanged(); // refresh UI
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, stats;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(android.R.id.text1);
            stats = itemView.findViewById(android.R.id.text2);
        }
    }
}