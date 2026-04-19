package com.spacecolonyapp;

import android.graphics.Color;
import android.view.*;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import com.spacecolony.backend.CrewMember;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.ViewHolder> {

    private List<CrewMember> data = new ArrayList<>();
    private CrewMember selected;

    public void setData(List<CrewMember> list) {
        data = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    public CrewMember getSelected() {
        return selected;
    }

    public void clearSelection() {
        selected = null;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position >= data.size()) return;

        CrewMember m = data.get(position);
        if (m == null) return;

        // NAME
        holder.name.setText(m.getClass().getSimpleName() + ": " + m.getName());

        // SAFE VALUES
        int energy = Math.max(0, Math.min(m.getEnergy(), 100));
        int morale = Math.max(0, Math.min(m.getMorale(), 100));

        holder.energyBar.setProgress(energy);
        holder.moraleBar.setProgress(morale);

        // STATS DISPLAY
        holder.stats.setText(
                "XP: " + m.getExperience() +
                        " | Morale: " + m.getMorale()
        );

        // SELECTION
        if (m.equals(selected)) {
            holder.itemView.setBackgroundColor(Color.parseColor("#334155"));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

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
        TextView stats;
        ProgressBar energyBar;
        ProgressBar moraleBar;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            stats = itemView.findViewById(R.id.stats);
            energyBar = itemView.findViewById(R.id.energyBar);
            moraleBar = itemView.findViewById(R.id.moraleBar);
        }
    }
}