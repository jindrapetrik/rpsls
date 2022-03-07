package com.jpexs.games.rpsls;

/**
 *
 * @author JPEXS
 */
public class Attack {

    public Point source;
    public Point target;
    public Weapon sourceWeapon;
    public Weapon targetWeapon;
    public int sourceTeam;
    public int targetTeam;

    public Attack(Point source, Point target, Weapon sourceWeapon, Weapon targetWeapon, int sourceTeam, int targetTeam) {
        this.source = source;
        this.target = target;
        this.sourceWeapon = sourceWeapon;
        this.targetWeapon = targetWeapon;
        this.sourceTeam = sourceTeam;
        this.targetTeam = targetTeam;
    }

}
