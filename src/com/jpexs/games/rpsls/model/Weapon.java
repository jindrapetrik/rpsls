package com.jpexs.games.rpsls.model;

/**
 *
 * @author JPEXS
 */
public enum Weapon {
    ROCK, PAPER, SCIZZORS, LIZARD, SPOCK;

    public int fight(Weapon otherWeapon) {
        if (otherWeapon == this) {
            return 0;
        }
        switch (this) {
            case ROCK:
                //rock crushes scizzors
                if (otherWeapon == SCIZZORS) {
                    return 1;
                }
                //rock crushes lizard
                if (otherWeapon == LIZARD) {
                    return 1;
                }
                break;
            case PAPER:
                //paper covers rock
                if (otherWeapon == ROCK) {
                    return 1;
                }
                //paper dispoves spock
                if (otherWeapon == SPOCK) {
                    return 1;
                }
                break;
            case SCIZZORS:
                //scizzors cuts paper
                if (otherWeapon == SCIZZORS) {
                    return 1;
                }
                //scizzors decapitates lizard
                if (otherWeapon == LIZARD) {
                    return 1;
                }
                break;
            case LIZARD:
                //lizard eats paper
                if (otherWeapon == PAPER) {
                    return 1;
                }
                //lizard poisons spock
                if (otherWeapon == SPOCK) {
                    return 1;
                }
                break;
            case SPOCK:
                //spock vaporizes rock
                if (otherWeapon == ROCK) {
                    return 1;
                }
                //spock smashes scizzors
                if (otherWeapon == SCIZZORS) {
                    return 1;
                }
                break;
        }
        return -1;
    }
}
