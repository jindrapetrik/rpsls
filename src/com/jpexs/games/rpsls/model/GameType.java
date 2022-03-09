package com.jpexs.games.rpsls.model;

/**
 *
 * @author JPEXS
 */
public enum GameType {
    SMALL(2, 2, 6, 6, false),
    LARGE(3, 3, 6, 8, true);

    private final boolean usesChooser;
    private final int numRows;
    private final int boardWidth;
    private final int boardHeight;
    private final int numEachWeapon;

    private GameType(int numRows, int numEachWeapon, int boardWidth, int boardHeight, boolean usesChooser) {
        this.numRows = numRows;
        this.numEachWeapon = numEachWeapon;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.usesChooser = usesChooser;
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

}
