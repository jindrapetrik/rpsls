package com.jpexs.games.rpsls;

/**
 *
 * @author JPEXS
 */
public class Move {

    public Point source;
    public Point target;

    public int sourceTeam;
    public int targetTeam;

    public Move(Point source, Point target, int sourceTeam, int targetTeam) {
        this.source = source;
        this.target = target;
        this.sourceTeam = sourceTeam;
        this.targetTeam = targetTeam;
    }

}
