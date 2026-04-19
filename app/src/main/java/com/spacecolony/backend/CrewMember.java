package com.spacecolony.backend;

import java.io.Serializable;

public abstract class CrewMember implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final String specialization;
    private int skill;
    private int resilience;
    private int experience;
    private int energy;
    private int maxEnergy;
    private int morale;
    private int missionsCompleted;
    private int victories;
    private int trainingSessions;
    private boolean defending;

    protected CrewMember(
            int id,
            String name,
            String specialization,
            int skill,
            int resilience,
            int experience,
            int maxEnergy,
            int morale) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.skill = skill;
        this.resilience = resilience;
        this.experience = experience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.morale = clampMorale(morale);
    }

    public int act() {
        return act(0);
    }

    public int act(int missionBonus) {
        if (!isAlive()) {
            return 0;
        }

        if (morale <= 39 && Math.random() < 0.10) {
            setDefending(false);
            return 0;
        }

        int effectiveSkill = skill + getExperienceBonus() + missionBonus;
        int damage = effectiveSkill + (int) (Math.random() * 3);

        if (morale >= 75 && Math.random() < 0.20) {
            damage *= 2;
        }

        energy = Math.max(0, energy - 5);
        setDefending(false);
        return Math.max(0, damage);
    }

    public void defend(int damage) {
        int incoming = Math.max(0, damage);
        if (defending) {
            incoming = incoming / 2;
            defending = false;
        }

        int reduced = Math.max(0, incoming - resilience / 2);
        energy = Math.max(0, energy - reduced);
    }

    public int useSpecialAbility(Threat threat, CrewMember ally, String missionType) {
        int bonus = 1;
        int damage = act(bonus);
        threat.takeDamage(damage);
        return damage;
    }

    public void rest() {
        this.energy = this.maxEnergy;
    }

    public void updateMorale(int amount) {
        this.morale = clampMorale(this.morale + amount);
    }

    public void gainExperience(int amount) {
        this.experience = Math.max(0, this.experience + amount);
    }

    public void incrementMissionCompleted() {
        this.missionsCompleted++;
    }

    public void incrementVictory() {
        this.victories++;
    }

    public void incrementTrainingSessions() {
        this.trainingSessions++;
    }

    public boolean isAlive() {
        return energy > 0;
    }

    public int getExperienceBonus() {
        return experience / 5;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        this.skill = Math.max(0, skill);
    }

    public int getResilience() {
        return resilience;
    }

    public void setResilience(int resilience) {
        this.resilience = Math.max(0, resilience);
    }

    public int getExperience() {
        return experience;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(maxEnergy, energy));
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = Math.max(1, maxEnergy);
        if (this.energy > this.maxEnergy) {
            this.energy = this.maxEnergy;
        }
    }

    public int getMorale() {
        return morale;
    }

    public int getMissionsCompleted() {
        return missionsCompleted;
    }

    public int getVictories() {
        return victories;
    }

    public int getTrainingSessions() {
        return trainingSessions;
    }

    public boolean isDefending() {
        return defending;
    }

    public void setDefending(boolean defending) {
        this.defending = defending;
    }

    private int clampMorale(int value) {
        return Math.max(0, Math.min(100, value));
    }
}