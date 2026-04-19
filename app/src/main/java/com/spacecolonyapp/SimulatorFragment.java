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

public class SimulatorFragment extends Fragment {

    private ColonyManager manager;
    private CrewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_simulator, container, false);

        RecyclerView rv = view.findViewById(R.id.recyclerView);
        Button train = view.findViewById(R.id.btnTrain);
        Button back = view.findViewById(R.id.btnBack);

        GameStateViewModel vm = new ViewModelProvider(requireActivity())
                .get(GameStateViewModel.class);

        manager = vm.getColonyManager();

        adapter = new CrewAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        refresh();


        train.setOnClickListener(v -> {

        });

        back.setOnClickListener(v -> {
            CrewMember selected = adapter.getSelected();
            if (selected != null) {
                manager.moveCrew(selected.getId(), Station.QUARTERS);
                refresh();
            }
        });

        return view;
    }

    private void refresh() {
        List<CrewMember> list = new ArrayList<>();

        for (CrewMember m : manager.getCrewList()) {
            if (manager.getCrewStation(m.getId()) == Station.SIMULATOR) {
                list.add(m);
            }
        }

        adapter.setData(list);
    }
}