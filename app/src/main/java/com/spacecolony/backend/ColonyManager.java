package com.spacecolony.backend;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColonyManager {
    private final Map<Integer, CrewMember> crewRoster = new HashMap<>();
    private final Map<Integer, Station> crewStations = new HashMap<>();
    private final Map<Integer, Integer> trainingSessions = new HashMap<>();
    private final Map<Integer, Integer> quartersIdleSessions = new HashMap<>();

    private int completedMissions;
    private int nextId = 1;

    public CrewMember createCrewMember(String role, String name) {
        String normalized = role == null ? "" : role.trim().toLowerCase();
        CrewMember member;
        int id = nextId++;

        switch (normalized) {
            case "pilot":
                member = new Pilot(id, name);
                break;
            case "engineer":
                member = new Engineer(id, name);
                break;
            case "medic":
                member = new Medic(id, name);
                break;
            case "scientist":
                member = new Scientist(id, name);
                break;
            case "soldier":
                member = new Soldier(id, name);
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }

        addCrewMember(member, Station.MISSION_CONTROL);
        return member;
    }

    public void addCrewMember(CrewMember member, Station initialStation) {
        crewRoster.put(member.getId(), member);
        crewStations.put(member.getId(), initialStation);
        trainingSessions.putIfAbsent(member.getId(), member.getTrainingSessions());
        quartersIdleSessions.putIfAbsent(member.getId(), 0);
    }

    public void removeCrewMember(int crewId) {
        if (!crewRoster.containsKey(crewId)) {
            return;
        }
        crewRoster.remove(crewId);
        crewStations.remove(crewId);
        trainingSessions.remove(crewId);
        quartersIdleSessions.remove(crewId);

        for (CrewMember survivor : crewRoster.values()) {
            survivor.updateMorale(-20);
        }
    }

    public boolean moveCrew(int crewId, Station destination) {
        CrewMember member = crewRoster.get(crewId);
        if (member == null) {
            return false;
        }

        crewStations.put(crewId, destination);

        if (destination == Station.QUARTERS) {
            member.rest();
            quartersIdleSessions.put(crewId, 0);
        }

        if (destination != Station.QUARTERS) {
            quartersIdleSessions.put(crewId, 0);
        }

        return true;
    }

    public void processQuartersSession() {
        for (Map.Entry<Integer, Station> entry : crewStations.entrySet()) {
            Integer crewId = entry.getKey();
            if (entry.getValue() == Station.QUARTERS) {
                CrewMember member = crewRoster.get(crewId);
                if (member != null) {
                    member.updateMorale(-5);
                    int idle = quartersIdleSessions.getOrDefault(crewId, 0) + 1;
                    quartersIdleSessions.put(crewId, idle);
                }
            }
        }
    }

    public void trainInSimulator(int crewId) {
        CrewMember member = crewRoster.get(crewId);
        if (member == null) {
            return;
        }
        if (crewStations.getOrDefault(crewId, Station.MISSION_CONTROL) != Station.SIMULATOR) {
            throw new IllegalStateException("Crew member must be in Simulator to train.");
        }

        member.gainExperience(2);
        member.updateMorale(5);
        member.incrementTrainingSessions();

        int sessions = trainingSessions.getOrDefault(crewId, 0) + 1;
        trainingSessions.put(crewId, sessions);
    }

    public void onMissionVictory(Collection<CrewMember> survivingParticipants) {
        for (CrewMember member : survivingParticipants) {
            member.updateMorale(15);
            member.incrementVictory();
            member.gainExperience(5);
            moveCrew(member.getId(), Station.MISSION_CONTROL);
        }
    }

    public void incrementCompletedMissions() {
        completedMissions++;
    }

    public int getCompletedMissions() {
        return completedMissions;
    }

    public Map<Integer, CrewMember> getCrewRoster() {
        return Collections.unmodifiableMap(crewRoster);
    }

    public List<CrewMember> getCrewList() {
        return new ArrayList<>(crewRoster.values());
    }

    public CrewMember getCrewMember(int crewId) {
        return crewRoster.get(crewId);
    }

    public Station getCrewStation(int crewId) {
        return crewStations.getOrDefault(crewId, Station.MISSION_CONTROL);
    }

    public int getTrainingSessions(int crewId) {
        return trainingSessions.getOrDefault(crewId, 0);
    }

    public Map<Integer, Integer> getTrainingSessionsSnapshot() {
        return Collections.unmodifiableMap(trainingSessions);
    }

    public void saveGame(String filePath) throws IOException {
        GameState snapshot = new GameState();
        snapshot.crewRoster = new HashMap<>(crewRoster);
        snapshot.crewStations = new HashMap<>(crewStations);
        snapshot.trainingSessions = new HashMap<>(trainingSessions);
        snapshot.quartersIdleSessions = new HashMap<>(quartersIdleSessions);
        snapshot.completedMissions = completedMissions;
        snapshot.nextId = nextId;

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(snapshot);
        }
    }

    public void loadGame(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            GameState loaded = (GameState) in.readObject();

            crewRoster.clear();
            crewStations.clear();
            trainingSessions.clear();
            quartersIdleSessions.clear();

            crewRoster.putAll(loaded.crewRoster);
            crewStations.putAll(loaded.crewStations);
            trainingSessions.putAll(loaded.trainingSessions);
            quartersIdleSessions.putAll(loaded.quartersIdleSessions);
            completedMissions = loaded.completedMissions;
            nextId = loaded.nextId;
        }
    }

    private static class GameState implements Serializable {
        private static final long serialVersionUID = 1L;

        private Map<Integer, CrewMember> crewRoster;
        private Map<Integer, Station> crewStations;
        private Map<Integer, Integer> trainingSessions;
        private Map<Integer, Integer> quartersIdleSessions;
        private int completedMissions;
        private int nextId;
    }
}