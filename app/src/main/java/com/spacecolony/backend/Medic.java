package com.spacecolony.backend;

public class Medic extends CrewMember {
    public Medic(int id, String name) {
        super(id, name, "Medic", 6, 6, 0, 36, 70);
    }

    @Override
    public int useSpecialAbility(Threat threat, CrewMember ally, String missionType) {
        if (ally != null && ally.isAlive()) {
            ally.setEnergy(Math.min(ally.getMaxEnergy(), ally.getEnergy() + 8));
            ally.updateMorale(5);
        }
        int damage = act(1);
        threat.takeDamage(damage);
        return damage;
    }
}