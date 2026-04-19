package com.spacecolony.backend;

import java.util.List;

public interface ActionSelector {
    MissionAction chooseAction(
            CrewMember actingCrew,
            Threat threat,
            List<CrewMember> team,
            int turnNumber,
            String missionType);
}