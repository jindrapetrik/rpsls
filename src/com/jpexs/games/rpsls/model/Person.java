package com.jpexs.games.rpsls.model;

/**
 *
 * @author JPEXS
 */
public class Person {

    private int team;
    private Weapon weapon = null;
    private SpecialItem specialItem = null;
    private boolean weaponVisible = false;

    public Person(int team, Weapon weapon, SpecialItem specialItem) {
        this.team = team;
        this.weapon = weapon;
        this.specialItem = specialItem;
    }

    public void setWeaponVisible(boolean weaponVisible) {
        this.weaponVisible = weaponVisible;
    }

    public boolean isWeaponVisible() {
        return weaponVisible;
    }

    public void setSpecialItem(SpecialItem specialItem) {
        this.specialItem = specialItem;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public SpecialItem getSpecialItem() {
        return specialItem;
    }

    public int getTeam() {
        return team;
    }

    public Weapon getWeapon() {
        return weapon;
    }

}
