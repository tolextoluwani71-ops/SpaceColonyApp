package com.spacecolony.backend;

public class Engineer extends CrewMember {
    public Engineer(int id, String name) {
        super(id, name, "Engineer", 7, 7, 0, 34, 65);
    }

    @Override
    public int useSpecialAbility(Threat threat, CrewMember ally, String missionType) {
        rest();
        int damage = act(1);
        threat.takeDamage(damage);
        return damage;
    }
}