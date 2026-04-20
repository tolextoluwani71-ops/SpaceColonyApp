package com.spacecolony.backend;

import java.util.List;

public class SimpleActionSelector implements ActionSelector {

    @Override
    public MissionAction chooseAction(
            CrewMember actingCrew,
            Threat threat,
            List<CrewMember> team,
            int turnNumber,
            String missionType) {

        // Low energy → defend
        if (actingCrew.getEnergy() < 10) {
            return MissionAction.DEFEND;
        }

        // Random chance for special ability
        if (Math.random() < 0.2) {
            return MissionAction.SPECIAL_ABILITY;
        }

        // Default → attack
        return MissionAction.ATTACK;
    }
}