package com.spacecolony.backend;

public class Scientist extends CrewMember {
    public Scientist(int id, String name) {
        super(id, name, "Scientist", 7, 5, 0, 30, 68);
    }

    @Override
    public int useSpecialAbility(Threat threat, CrewMember ally, String missionType) {
        int damage = act(3);
        threat.takeDamage(damage);
        gainExperience(2);
        return damage;
    }
}