package com.spacecolonyapp;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;
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
        Button mission = view.findViewById(R.id.btnMission);

        manager = new ViewModelProvider(requireActivity())
                .get(GameStateViewModel.class)
                .getColonyManager();

        adapter = new CrewAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        refresh();

        // TRAIN
        train.setOnClickListener(v -> {
            CrewMember c = adapter.getSelected();

            if (c == null) {
                Toast.makeText(getContext(), "Select a crew first!", Toast.LENGTH_SHORT).show();
                return;
            }

            manager.trainInSimulator(c.getId());

            Toast.makeText(getContext(), "Crew trained!", Toast.LENGTH_SHORT).show();

            adapter.clearSelection();
            refresh();
        });

        // BACK TO QUARTERS
        back.setOnClickListener(v -> {
            CrewMember c = adapter.getSelected();

            if (c == null) {
                Toast.makeText(getContext(), "Select a crew first!", Toast.LENGTH_SHORT).show();
                return;
            }

            manager.moveCrew(c.getId(), Station.QUARTERS);

            Toast.makeText(getContext(), "Moved to Quarters!", Toast.LENGTH_SHORT).show();

            adapter.clearSelection();
            refresh();
        });

        // 🔥 MOVE TO MISSION CONTROL (FIX)
        mission.setOnClickListener(v -> {
            CrewMember c = adapter.getSelected();

            if (c == null) {
                Toast.makeText(getContext(), "Select a crew first!", Toast.LENGTH_SHORT).show();
                return;
            }

            manager.moveCrew(c.getId(), Station.MISSION_CONTROL);

            Toast.makeText(getContext(), "Moved to Mission!", Toast.LENGTH_SHORT).show();

            adapter.clearSelection();
            refresh();
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