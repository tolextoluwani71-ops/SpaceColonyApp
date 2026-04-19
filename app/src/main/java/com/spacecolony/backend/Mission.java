package com.spacecolony.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mission {
    private final ColonyManager colonyManager;
    private final String missionType;
    private final ActionSelector actionSelector;

    public Mission(ColonyManager colonyManager, String missionType, ActionSelector actionSelector) {
        this.colonyManager = colonyManager;
        this.missionType = missionType;
        this.actionSelector = actionSelector;
    }

    public MissionResult runMission(int firstCrewId, int secondCrewId) {
        CrewMember first = colonyManager.getCrewMember(firstCrewId);
        CrewMember second = colonyManager.getCrewMember(secondCrewId);

        if (first == null || second == null) {
            throw new IllegalArgumentException("Both selected crew members must exist in roster.");
        }

        Threat threat = generateThreat();
        MissionResult result = new MissionResult(missionType, threat.getName());
        List<CrewMember> team = new ArrayList<>(Arrays.asList(first, second));

        for (CrewMember member : team) {
            member.incrementMissionCompleted();
            colonyManager.moveCrew(member.getId(), Station.MISSION_CONTROL);
        }

        int turn = 1;
        while (!threat.isDefeated() && hasLivingCrew(team)) {
            for (CrewMember actor : team) {
                if (!actor.isAlive() || threat.isDefeated()) {
                    continue;
                }

                MissionAction action = actionSelector.chooseAction(actor, threat, new ArrayList<>(team), turn, missionType);
                executeAction(actor, team, threat, action, result);

                if (threat.isDefeated()) {
                    break;
                }

                CrewMember retaliationTarget = pickRetaliationTarget(team);
                if (retaliationTarget != null) {
                    int before = retaliationTarget.getEnergy();
                    int rawDamage = threat.attack(retaliationTarget);
                    int taken = Math.max(0, before - retaliationTarget.getEnergy());
                    result.addLog("Threat strikes " + retaliationTarget.getName() + " for " + taken + " damage (raw " + rawDamage + ").");
                    processDeathIfNeeded(retaliationTarget, result);
                }
            }
            turn++;
        }

        boolean victory = threat.isDefeated();
        result.setVictory(victory);

        List<CrewMember> survivors = new ArrayList<>();
        for (CrewMember member : team) {
            if (member.isAlive() && colonyManager.getCrewMember(member.getId()) != null) {
                survivors.add(member);
                result.addSurvivor(member.getId());
            }
        }

        if (victory) {
            colonyManager.onMissionVictory(survivors);
            result.addLog("Mission success. Threat neutralized: " + threat.getName());
        } else {
            result.addLog("Mission failed. All selected crew members were lost.");
        }

        colonyManager.incrementCompletedMissions();
        return result;
    }

    private Threat generateThreat() {
        int threatSkill = 4 + colonyManager.getCompletedMissions();
        int threatResilience = 3 + (colonyManager.getCompletedMissions() / 2);
        int threatEnergy = 24 + colonyManager.getCompletedMissions() * 2;
        return new Threat("Rogue Entity " + (colonyManager.getCompletedMissions() + 1), threatSkill, threatResilience, threatEnergy);
    }

    private void executeAction(
            CrewMember actor,
            List<CrewMember> team,
            Threat threat,
            MissionAction action,
            MissionResult result) {
        int missionBonus = getSpecializationBonus(actor);

        if (action == null) {
            action = MissionAction.ATTACK;
        }

        switch (action) {
            case ATTACK:
                performAttack(actor, threat, missionBonus, result);
                break;
            case DEFEND:
                actor.setDefending(true);
                result.addLog(actor.getName() + " takes a defensive stance.");
                break;
            case SPECIAL_ABILITY:
                CrewMember ally = pickAlly(actor, team);
                int before = threat.getEnergy();
                int specialDamage = actor.useSpecialAbility(threat, ally, missionType);
                int actual = Math.max(0, before - threat.getEnergy());
                result.addLog(actor.getName() + " uses special ability for " + actual + " damage (base " + specialDamage + ").");
                break;
            default:
                performAttack(actor, threat, missionBonus, result);
                break;
        }
    }

    private void performAttack(CrewMember actor, Threat threat, int missionBonus, MissionResult result) {
        int before = threat.getEnergy();
        int dealt = actor.act(missionBonus);
        threat.takeDamage(dealt);
        int actual = Math.max(0, before - threat.getEnergy());

        if (dealt == 0) {
            result.addLog(actor.getName() + " hesitates and loses the turn.");
        } else {
            result.addLog(actor.getName() + " attacks for " + actual + " damage.");
        }
    }

    private int getSpecializationBonus(CrewMember member) {
        if (missionType == null) {
            return 0;
        }
        String type = missionType.trim().toLowerCase();
        if (member instanceof Engineer && type.equals("repair station")) {
            return 2;
        }
        if (member instanceof Pilot && type.equals("asteroid field")) {
            return 2;
        }
        if (member instanceof Medic && type.equals("biohazard")) {
            return 2;
        }
        if (member instanceof Scientist && type.equals("research anomaly")) {
            return 2;
        }
        if (member instanceof Soldier && type.equals("frontline assault")) {
            return 2;
        }
        return 0;
    }

    private void processDeathIfNeeded(CrewMember crew, MissionResult result) {
        if (crew.getEnergy() <= 0 && colonyManager.getCrewMember(crew.getId()) != null) {
            result.addLog(crew.getName() + " has fallen in mission.");
            result.addFallenCrew(crew.getId());
            colonyManager.removeCrewMember(crew.getId());
        }
    }

    private CrewMember pickRetaliationTarget(List<CrewMember> team) {
        List<CrewMember> alive = new ArrayList<>();
        for (CrewMember member : team) {
            if (member.isAlive() && colonyManager.getCrewMember(member.getId()) != null) {
                alive.add(member);
            }
        }
        if (alive.isEmpty()) {
            return null;
        }
        int index = (int) (Math.random() * alive.size());
        return alive.get(index);
    }

    private CrewMember pickAlly(CrewMember actor, List<CrewMember> team) {
        for (CrewMember member : team) {
            if (member.getId() != actor.getId() && member.isAlive()) {
                return member;
            }
        }
        return actor;
    }

    private boolean hasLivingCrew(List<CrewMember> team) {
        for (CrewMember member : team) {
            if (member.isAlive() && colonyManager.getCrewMember(member.getId()) != null) {
                return true;
            }
        }
        return false;
    }
}