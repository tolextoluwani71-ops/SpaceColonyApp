package com.spacecolony.backend;

import java.io.Serializable;

public class Threat implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int skill;
    private final int resilience;
    private int energy;
    private final int maxEnergy;

    public Threat(String name, int skill, int resilience, int maxEnergy) {
        this.name = name;
        this.skill = Math.max(0, skill);
        this.resilience = Math.max(0, resilience);
        this.maxEnergy = Math.max(1, maxEnergy);
        this.energy = this.maxEnergy;
    }

    public int attack(CrewMember target) {
        if (target == null || !target.isAlive() || isDefeated()) {
            return 0;
        }
        int damage = skill + (int) (Math.random() * 3);
        target.defend(damage);
        return damage;
    }

    public void takeDamage(int rawDamage) {
        int damage = Math.max(0, rawDamage - resilience / 2);
        this.energy = Math.max(0, this.energy - damage);
    }

    public boolean isDefeated() {
        return energy <= 0;
    }

    public String getName() {
        return name;
    }

    public int getSkill() {
        return skill;
    }

    public int getResilience() {
        return resilience;
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }
}