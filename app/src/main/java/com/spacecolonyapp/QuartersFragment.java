package com.spacecolonyapp;

import android.os.Bundle;
import android.view.*;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import com.spacecolony.backend.*;

public class QuartersFragment extends Fragment {

    private ColonyManager manager;
    private CrewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quarters, container, false);

        RecyclerView rv = view.findViewById(R.id.recyclerView);
        Button recruit = view.findViewById(R.id.btnRecruit);
        Button move = view.findViewById(R.id.btnMove);

        GameStateViewModel vm = new ViewModelProvider(requireActivity())
                .get(GameStateViewModel.class);

        manager = vm.getColonyManager();

        adapter = new CrewAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        refresh();

        recruit.setOnClickListener(v -> {
            manager.createCrewMember("pilot", "Crew" + System.currentTimeMillis());
            refresh();
        });

        move.setOnClickListener(v -> {
            CrewMember c = adapter.getSelected();
            if (c != null) {
                manager.moveCrew(c.getId(), Station.SIMULATOR);
                refresh();
            }
        });

        return view;
    }

    private void refresh() {
        List<CrewMember> list = new ArrayList<>();

        for (CrewMember m : manager.getCrewList()) {
            if (manager.getCrewStation(m.getId()) == Station.QUARTERS) {
                list.add(m);
            }
        }

        adapter.setData(list);
    }
}