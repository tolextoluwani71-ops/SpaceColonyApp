package com.spacecolonyapp;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;

import java.util.*;

import com.spacecolony.backend.*;

public class MissionFragment extends Fragment {

    private ColonyManager manager;
    private CrewAdapter adapter;
    private TextView log;

    private Button btnAttack, btnDefend;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mission, container, false);

        RecyclerView rv = view.findViewById(R.id.recyclerView);
        btnAttack = view.findViewById(R.id.btnAttack);
        btnDefend = view.findViewById(R.id.btnDefend);
        log = view.findViewById(R.id.txtLog);

        manager = new ViewModelProvider(requireActivity())
                .get(GameStateViewModel.class)
                .getColonyManager();

        adapter = new CrewAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        refresh();

        btnAttack.setOnClickListener(v -> startBattle());
        btnDefend.setOnClickListener(v -> startBattle());

        return view;
    }

    private void startBattle() {

        List<CrewMember> crew = getCrew();

        if (crew.size() < 2) {
            log.setText("Send 2 crew to Mission Control!");
            return;
        }

        CrewMember c1 = crew.get(0);
        CrewMember c2 = crew.get(1);

        Mission mission = new Mission(
                manager,
                "frontline assault",
                new SimpleActionSelector()
        );

        MissionResult result = mission.runMission(c1.getId(), c2.getId());

        StringBuilder output = new StringBuilder();

        
        for (String line : result.getBattleLog()) {
            output.append(line).append("\n");
        }

        if (result.isVictory()) {
            output.append("\nMISSION SUCCESS!");
        } else {
            output.append("\nMISSION FAILED!");
        }

        log.setText(output.toString());

        refresh();
    }

    private List<CrewMember> getCrew() {
        List<CrewMember> list = new ArrayList<>();

        for (CrewMember m : manager.getCrewList()) {
            if (manager.getCrewStation(m.getId()) == Station.MISSION_CONTROL) {
                list.add(m);
            }
        }

        return list;
    }

    private void refresh() {
        adapter.setData(getCrew());
    }
}