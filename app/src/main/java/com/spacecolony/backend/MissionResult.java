package com.spacecolony.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MissionResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String missionType;
    private final String threatName;
    private final List<String> battleLog = new ArrayList<>();
    private final List<Integer> fallenCrewIds = new ArrayList<>();
    private final List<Integer> survivorIds = new ArrayList<>();
    private boolean victory;

    public MissionResult(String missionType, String threatName) {
        this.missionType = missionType;
        this.threatName = threatName;
    }

    public void addLog(String line) {
        battleLog.add(line);
    }

    public void addFallenCrew(int crewId) {
        if (!fallenCrewIds.contains(crewId)) {
            fallenCrewIds.add(crewId);
        }
    }

    public void addSurvivor(int crewId) {
        if (!survivorIds.contains(crewId)) {
            survivorIds.add(crewId);
        }
    }

    public String getMissionType() {
        return missionType;
    }

    public String getThreatName() {
        return threatName;
    }

    public List<String> getBattleLog() {
        return Collections.unmodifiableList(battleLog);
    }

    public List<Integer> getFallenCrewIds() {
        return Collections.unmodifiableList(fallenCrewIds);
    }

    public List<Integer> getSurvivorIds() {
        return Collections.unmodifiableList(survivorIds);
    }

    public boolean isVictory() {
        return victory;
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }
}