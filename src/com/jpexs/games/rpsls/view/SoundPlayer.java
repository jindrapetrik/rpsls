package com.jpexs.games.rpsls.view;

import java.util.ArrayList;
import java.util.List;
import javazoom.jl.player.Player;

/**
 *
 * @author JPEXS
 */
public class SoundPlayer extends Thread {

    private boolean terminated = false;
    private Object LOCK = new Object();
    private Player playMP3;

    private List<String> soundFiles = new ArrayList<>();

    public void playFile(String file) {
        soundFiles.add(file);

        if (!isAlive()) {
            start();
        } else {
            if (playMP3 != null) {
                playMP3.close();
                playMP3 = null;
            }
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
            while (!soundFiles.isEmpty()) {
                String soundFile = soundFiles.get(0);
                try {
                    playMP3 = new Player(SoundPlayer.class.getResourceAsStream("/com/jpexs/games/rpsls/sound/" + soundFile));
                    playMP3.play();
                    playMP3 = null;
                } catch (Exception exc) {
                    //ignore
                }
                soundFiles.remove(0);
            }
            synchronized (LOCK) {
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
