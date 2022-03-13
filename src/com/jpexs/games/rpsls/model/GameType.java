package com.jpexs.games.rpsls.model;

/**
 *
 * @author JPEXS
 */
public enum GameType {
    RPSLS(2, 2, 6, 6, false, 5),
    RPS(2, 4, 7, 6, false, 3),
    RPSLS_LARGE(3, 3, 6, 8, true, 5);

    private final boolean usesChooser;
    private final int numRows;
    private final int boardWidth;
    private final int boardHeight;
    private final int numEachWeapon;
    private final int weaponKindCount;

    private GameType(int numRows, int numEachWeapon, int boardWidth, int boardHeight, boolean usesChooser, int weaponKindCount) {
        this.numRows = numRows;
        this.numEachWeapon = numEachWeapon;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.usesChooser = usesChooser;
        this.weaponKindCount = weaponKindCount;
    }

    public boolean usesChooser() {
        return usesChooser;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumEachWeapon() {
        return numEachWeapon;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    public int getWeaponKindCount() {
        return weaponKindCount;
    }

    public Weapon[] getWeapons() {
        Weapon[] ret = new Weapon[getWeaponKindCount()];
        System.arraycopy(Weapon.values(), 0, ret, 0, getWeaponKindCount());
        return ret;
    }
}
