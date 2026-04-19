package com.spacecolonyapp;

import android.os.Bundle;
import android.os.Handler;
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

    private CrewMember c1, c2;
    private int threatHP;

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

        btnAttack.setOnClickListener(v -> startBattle("ATTACK"));
        btnDefend.setOnClickListener(v -> startBattle("DEFEND"));

        return view;
    }

    private void startBattle(String action) {

        List<CrewMember> crew = getCrew();

        if (crew.size() < 2) {
            log.setText("Send 2 crew to Mission Control!");
            return;
        }

        c1 = crew.get(0);
        c2 = crew.get(1);

        threatHP = 20 + manager.getCompletedMissions() * 2;

        StringBuilder battleLog = new StringBuilder();
        Handler handler = new Handler();

        btnAttack.setEnabled(false);
        btnDefend.setEnabled(false);


        handler.postDelayed(() -> {

            int dmg1 = calculateDamage(c1);
            int dmg2 = calculateDamage(c2);

            threatHP -= (dmg1 + dmg2);

            battleLog.append("Round 1\n");
            battleLog.append(c1.getName()).append(" hits ").append(dmg1).append("\n");
            battleLog.append(c2.getName()).append(" hits ").append(dmg2).append("\n\n");

            log.setText(battleLog.toString());

        }, 500);


        handler.postDelayed(() -> {

            if (threatHP > 0) {
                int dmg1 = calculateDamage(c1);
                int dmg2 = calculateDamage(c2);

                threatHP -= (dmg1 + dmg2);

                battleLog.append("Round 2\n");
                battleLog.append(c1.getName()).append(" hits ").append(dmg1).append("\n");
                battleLog.append(c2.getName()).append(" hits ").append(dmg2).append("\n\n");

                log.setText(battleLog.toString());
            }

        }, 1500);


        handler.postDelayed(() -> {

            if (threatHP > 0) {
                int dmg1 = calculateDamage(c1);
                int dmg2 = calculateDamage(c2);

                threatHP -= (dmg1 + dmg2);

                battleLog.append("Round 3\n");
                battleLog.append(c1.getName()).append(" hits ").append(dmg1).append("\n");
                battleLog.append(c2.getName()).append(" hits ").append(dmg2).append("\n\n");
            }


            if (threatHP <= 0) {

                battleLog.append("MISSION SUCCESS!\n");
                battleLog.append("XP +5 each\n");
                battleLog.append("Morale +15\n");

                manager.incrementCompletedMissions();

                for (CrewMember m : crew) {
                    m.gainExperience(5);
                    m.updateMorale(15);
                    manager.moveCrew(m.getId(), Station.QUARTERS);
                }

            } else {

                battleLog.append("MISSION FAILED!\n");
                battleLog.append("One crew lost!\n");
                battleLog.append("Morale -20\n");

                // Remove first crew safely
                if (c1 != null) {
                    manager.removeCrewMember(c1.getId());
                }

                for (CrewMember m : crew) {
                    m.updateMorale(-20);
                }
            }

            log.setText(battleLog.toString());

            btnAttack.setEnabled(true);
            btnDefend.setEnabled(true);

            refresh();

        }, 2500);
    }


    private int calculateDamage(CrewMember c) {

        int base = c.getSkill() + (int)(Math.random() * 3);


        if (c.getMorale() > 75 && Math.random() < 0.2) {
            return base * 2;
        }

        // Low morale → hesitation
        if (c.getMorale() < 40 && Math.random() < 0.1) {
            return 0;
        }

        return base;
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