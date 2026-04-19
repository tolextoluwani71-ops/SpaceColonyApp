package com.spacecolony.backend;

public class ConsoleTest {
    public static void main(String[] args) {
        ColonyManager manager = new ColonyManager();

        // Test recruitment
        CrewMember pilot = manager.createCrewMember("pilot", "Nova");
        CrewMember soldier = manager.createCrewMember("soldier", "Rex");

        System.out.println("Initial Pilot Energy: " + pilot.getEnergy());

        // Test movement + training
        manager.moveCrew(pilot.getId(), Station.SIMULATOR);
        manager.trainInSimulator(pilot.getId());
        System.out.println("Pilot Experience after training: " + pilot.getExperience());
        System.out.println("Pilot Training Sessions: " + manager.getTrainingSessions(pilot.getId()));

        // Test combat math with the same scaling formula used by Mission
        int threatSkill = 4 + manager.getCompletedMissions();
        int threatResilience = 3 + (manager.getCompletedMissions() / 2);
        int threatEnergy = 24 + manager.getCompletedMissions() * 2;
        Threat threat = new Threat("Console Threat", threatSkill, threatResilience, threatEnergy);

        System.out.println("Threat generated with skill: " + threat.getSkill());

        int damage = pilot.act();
        int beforeEnergy = threat.getEnergy();
        threat.takeDamage(damage);
        int appliedDamage = beforeEnergy - threat.getEnergy();
        System.out.println("Pilot rolled damage: " + damage + " (applied: " + appliedDamage + ")");
        System.out.println("Threat energy after pilot attack: " + threat.getEnergy());

        // Full mission loop smoke test (always choose ATTACK)
        Mission mission = new Mission(
                manager,
                "Repair Station",
                (actingCrew, activeThreat, team, turnNumber, missionType) -> MissionAction.ATTACK);

        MissionResult result = mission.runMission(pilot.getId(), soldier.getId());
        System.out.println("Mission victory: " + result.isVictory());
        System.out.println("Completed missions in manager: " + manager.getCompletedMissions());

        System.out.println("--- Mission Log ---");
        for (String line : result.getBattleLog()) {
            System.out.println(line);
        }
    }
}