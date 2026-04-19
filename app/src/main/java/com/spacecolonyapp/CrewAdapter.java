package com.spacecolonyapp;

import android.graphics.Color;
import android.view.*;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import com.spacecolony.backend.CrewMember;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.ViewHolder> {

    private List<CrewMember> data = new ArrayList<>();
    private CrewMember selected;

    public void setData(List<CrewMember> list) {
        data = list;
        notifyDataSetChanged();
    }

    public CrewMember getSelected() {
        return selected;
    }

    public void clearSelection() {
        selected = null;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew, parent, false); // 🔥 custom layout
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CrewMember m = data.get(position);

        // NAME
        holder.name.setText(m.getClass().getSimpleName() + ": " + m.getName());

        // BARS
        holder.energyBar.setProgress(m.getEnergy());
        holder.moraleBar.setProgress(m.getMorale());

        // SELECTION EFFECT
        if (m == selected) {
            holder.itemView.setBackgroundColor(Color.parseColor("#334155"));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // CLICK
        holder.itemView.setOnClickListener(v -> {
            selected = m;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ProgressBar energyBar, moraleBar;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            energyBar = itemView.findViewById(R.id.energyBar);
            moraleBar = itemView.findViewById(R.id.moraleBar);
        }
    }
}