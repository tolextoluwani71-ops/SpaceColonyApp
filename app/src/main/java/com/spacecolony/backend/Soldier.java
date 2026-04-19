package com.spacecolony.backend;

public class Soldier extends CrewMember {
    public Soldier(int id, String name) {
        super(id, name, "Soldier", 9, 8, 0, 38, 60);
    }

    @Override
    public int useSpecialAbility(Threat threat, CrewMember ally, String missionType) {
        int damage = act(2);
        threat.takeDamage(damage);
        updateMorale(2);
        return damage;
    }
}