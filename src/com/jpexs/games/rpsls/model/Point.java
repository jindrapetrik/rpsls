package com.jpexs.games.rpsls.model;

/**
 *
 * @author JPEXS
 */
public class Point {

    private int x;
    private int y;
    private int observingTeam;
    private GameType gameType;

    public Point(int x, int y, int observingTeam, GameType gameType) {
        this.x = x;
        this.y = y;
        this.observingTeam = observingTeam;
        this.gameType = gameType;
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
        return gameType.getBoardWidth() - x - 1;
    }

    public int getYForTeam(int team) {
        if (team == observingTeam) {
            return y;
        }
        return gameType.getBoardHeight() - y - 1;
    }

    public Point toTeamPoint(int team) {
        return new Point(getXForTeam(team), getYForTeam(team), team, gameType);
    }

    public int getBaseTeamX() {
        if (observingTeam == 0) {
            return gameType.getBoardWidth() - x - 1;
        }
        return x;
    }

    public int getBaseTeamY() {
        if (observingTeam == 0) {
            return gameType.getBoardHeight() - y - 1;
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
