package com.spacecolony.backend;

public class Pilot extends CrewMember {
    public Pilot(int id, String name) {
        super(id, name, "Pilot", 8, 5, 0, 32, 65);
    }

    @Override
    public int useSpecialAbility(Threat threat, CrewMember ally, String missionType) {
        int damage = act(2);
        threat.takeDamage(damage);
        return damage;
    }
}