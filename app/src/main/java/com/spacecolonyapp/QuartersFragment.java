package com.spacecolonyapp;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;
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

        manager = new ViewModelProvider(requireActivity())
                .get(GameStateViewModel.class)
                .getColonyManager();

        adapter = new CrewAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        refresh();

        recruit.setOnClickListener(v -> {
            String[] roles = {"pilot","engineer","medic","scientist","soldier"};
            String role = roles[new Random().nextInt(roles.length)];
            manager.createCrewMember(role, role + "_" + System.currentTimeMillis());
            refresh();
        });

        move.setOnClickListener(v -> {
            CrewMember c = adapter.getSelected();
            if (c == null) return;

            manager.moveCrew(c.getId(), Station.SIMULATOR);
            adapter.clearSelection();
            refresh();
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