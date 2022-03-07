package com.jpexs.games.rpsls.model;

/**
 *
 * @author JPEXS
 */
public class Point {

    private int x;
    private int y;
    private int observingTeam;

    public Point(int x, int y, int observingTeam) {
        this.x = x;
        this.y = y;
        this.observingTeam = observingTeam;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getXForTeam(int team) {
        if (team == observingTeam) {
            return x;
        }
        return RpslsModel.BOARD_WIDTH - x - 1;
    }

    public int getYForTeam(int team) {
        if (team == observingTeam) {
            return y;
        }
        return RpslsModel.BOARD_HEIGHT - y - 1;
    }

    public int getBaseTeamX() {
        if (observingTeam == 0) {
            return RpslsModel.BOARD_WIDTH - x - 1;
        }
        return x;
    }

    public int getBaseTeamY() {
        if (observingTeam == 0) {
            return RpslsModel.BOARD_HEIGHT - y - 1;
        }
        return y;
    }

    public int getObservingTeam() {
        return observingTeam;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.getBaseTeamX();
        hash = 97 * hash + this.getBaseTeamY();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Point other = (Point) obj;
        if (this.getBaseTeamX() != other.getBaseTeamX()) {
            return false;
        }
        if (this.getBaseTeamY() != other.getBaseTeamY()) {
            return false;
        }
        return true;
    }

}
