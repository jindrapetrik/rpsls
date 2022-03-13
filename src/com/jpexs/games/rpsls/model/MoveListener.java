package com.jpexs.games.rpsls.model;

import com.jpexs.games.rpsls.model.Point;

/**
 *
 * @author JPEXS
 */
public interface MoveListener {

    public void move(Point source, Point destination);
}
