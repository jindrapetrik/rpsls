package com.jpexs.games.rpsls.view;

import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.player.Player;

/**
 *
 * @author JPEXS
 */
public class SoundPlayer extends Thread {

    private String soundFile;
    private boolean terminated = false;
    private Object LOCK = new Object();

    public void playFile(String file) {
        this.soundFile = file;
        if (!isAlive()) {
            start();
        } else {
            synchronized (LOCK) {
                LOCK.notify();
            }
        }
    }

    public void terminate() {
        terminated = true;
        synchronized (LOCK) {
            LOCK.notify();
        }
    }

    @Override
    public void run() {

        while (true) {
            synchronized (LOCK) {
                if (soundFile != null) {
                    try {
                        Player playMP3 = new Player(FrameView.class.getResourceAsStream("/com/jpexs/games/rpsls/sound/" + soundFile));
                        playMP3.play();
                    } catch (Exception exc) {
                        //ignore
                    }
                    soundFile = null;
                }

                try {
                    LOCK.wait();
                } catch (InterruptedException ex) {
                    return;
                }
            }
            if (terminated) {
                return;
            }
        }
    }
}
