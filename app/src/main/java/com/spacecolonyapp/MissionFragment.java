package com.spacecolonyapp;

import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import com.spacecolony.backend.*;

public class MissionFragment extends Fragment {

    private ColonyManager manager;
    private CrewAdapter adapter;
    private TextView log;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mission, container, false);

        RecyclerView rv = view.findViewById(R.id.recyclerView);
        Button start = view.findViewById(R.id.btnStart);
        log = view.findViewById(R.id.txtLog);

        GameStateViewModel vm = new ViewModelProvider(requireActivity())
                .get(GameStateViewModel.class);

        manager = vm.getColonyManager();

        adapter = new CrewAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        refresh();

        start.setOnClickListener(v -> {

            List<CrewMember> list = getMissionCrew();

            if (list.size() < 2) return;

            Mission mission = new Mission(
                    manager,
                    "Mission",
                    (a, t, team, turn, type) -> MissionAction.ATTACK
            );

            MissionResult result = mission.runMission(
                    list.get(0).getId(),
                    list.get(1).getId()
            );

            log.setText(result.getBattleLog().toString());
            refresh();
        });

        return view;
    }

    private List<CrewMember> getMissionCrew() {
        List<CrewMember> list = new ArrayList<>();

        for (CrewMember m : manager.getCrewList()) {
            if (manager.getCrewStation(m.getId()) == Station.MISSION_CONTROL) {
                list.add(m);
            }
        }

        return list;
    }

    private void refresh() {
        adapter.setData(getMissionCrew());
    }
}