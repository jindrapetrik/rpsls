package com.jpexs.games.rpsls.view;

import com.jpexs.games.rpsls.Main;
import com.jpexs.games.rpsls.model.NetworkPackets;
import com.jpexs.games.rpsls.model.Point;
import com.jpexs.games.rpsls.model.RpslsModel;
import com.jpexs.games.rpsls.model.Weapon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author JPEXS
 */
public class NetworkView implements IRpslView {

    private int team;
    private Socket socket;
    private boolean forceTerminate = false;

    public NetworkView(RpslsModel model, int team, Socket socket) {
        this.team = team;
        this.socket = socket;

        Thread readThread = new Thread() {
            @Override
            public void run() {
                try {
                    InputStream is = socket.getInputStream();
                    DataInputStream dais = new DataInputStream(is);
                    byte[] signature = new byte[5];
                    dais.readFully(signature);
                    if (!new String(signature).equals("RPSLS")) {
                        throw new IOException("Invalid signature");
                    }
                    int versionMajor = is.read();
                    int versionMinor = is.read();
                    if (versionMajor != Main.PROTOCOL_VERSION_MAJOR) {
                        throw new IOException("Major version does not match");
                    }

                    while (true) {
                        int packetType = is.read();
                        int x;
                        int y;
                        int t;
                        int x2;
                        int y2;
                        int t2;
                        switch (packetType) {
                            case NetworkPackets.PACKET_SET_FLAG:
                                x = is.read();
                                y = is.read();
                                t = is.read();

                                fireSetFlag(new Point(x, y, t));
                                break;
                            case NetworkPackets.PACKET_SET_TRAP:
                                x = is.read();
                                y = is.read();
                                t = is.read();

                                fireSetTrap(new Point(x, y, t));
                                break;
                            case NetworkPackets.PACKET_SET_CHOOSER:
                                x = is.read();
                                y = is.read();
                                t = is.read();

                                fireSetChooser(new Point(x, y, t));
                                break;
                            case NetworkPackets.PACKET_STARTUP_WEAPONS:
                                Weapon weapons[] = new Weapon[model.getNumRowsPerTeam() * model.getBoardWidth()];
                                for (int i = 0; i < weapons.length; i++) {
                                    int w = is.read();
                                    if (w == 255) {
                                        weapons[i] = null;
                                    } else {
                                        weapons[i] = Weapon.values()[w];
                                    }
                                }
                                fireSetStartupWeapons(weapons);
                                break;
                            case NetworkPackets.PACKET_START:
                                fireStart();
                                break;
                            case NetworkPackets.PACKET_MOVE:
                                x = is.read();
                                y = is.read();
                                t = is.read();
                                x2 = is.read();
                                y2 = is.read();
                                t2 = is.read();
                                fireMove(new Point(x, y, t), new Point(x2, y2, t2));
                                break;
                            case NetworkPackets.PACKET_PROCEED:
                                fireProceed();
                                break;
                            case NetworkPackets.PACKET_SELECT_WEAPON:
                                int w = is.read();
                                fireSelectWeapon(Weapon.values()[w]);
                                break;
                            case NetworkPackets.PACKET_EXIT:
                                fireExit();
                                break;
                        }
                    }
                } catch (IOException ex) {
                    if (!forceTerminate) {
                        Main.showError(ex);
                        fireExit();
                    }
                }
            }
        };
        readThread.start();
    }

    private final List<SetPointListener> setFlagListeners = new ArrayList<>();
    private final List<SetPointListener> setTrapListeners = new ArrayList<>();
    private final List<SetPointListener> setChooserListeners = new ArrayList<>();
    private final List<ActionListener> startListeners = new ArrayList<>();
    private final List<SetStartupWeaponsListener> setStartupWeaponsListeners = new ArrayList<>();
    private final List<ActionListener> proceedListeners = new ArrayList<>();
    private final List<MoveListener> moveListeners = new ArrayList<>();
    private final List<SelectWeaponListener> selectWeaponListeners = new ArrayList<>();
    private final List<ActionListener> exitListeners = new ArrayList<>();

    @Override
    public void addStartListener(ActionListener listener) {
        startListeners.add(listener);
    }

    @Override
    public void removeStartListener(ActionListener listener) {
        startListeners.remove(listener);
    }

    protected void fireStart() {
        for (ActionListener listener : startListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "START"));
        }
    }

    @Override
    public void addShuffleListener(ActionListener listener) {

    }

    @Override
    public void removeShuffleListener(ActionListener listener) {

    }

    protected void fireShuffle() {

    }

    @Override
    public void addProceedListener(ActionListener listener) {
        proceedListeners.add(listener);
    }

    @Override
    public void removeProceedListener(ActionListener listener) {
        proceedListeners.remove(listener);
    }

    protected void fireProceed() {
        for (ActionListener listener : proceedListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "PROCEED"));
        }
    }

    @Override
    public void addMoveListener(MoveListener listener) {
        moveListeners.add(listener);
    }

    @Override
    public void removeMoveListener(MoveListener listener) {
        moveListeners.remove(listener);
    }

    protected void fireMove(Point source, Point destination) {
        for (MoveListener listener : moveListeners) {
            listener.move(source, destination);
        }
    }

    @Override
    public void addSetFlagListener(SetPointListener listener) {
        setFlagListeners.add(listener);
    }

    @Override
    public void removeSetFlagListener(SetPointListener listener) {
        setFlagListeners.remove(listener);
    }

    protected void fireSetFlag(Point point) {
        for (SetPointListener listener : setFlagListeners) {
            listener.setPoint(point);
        }
    }

    @Override
    public void addSetTrapListener(SetPointListener listener) {
        setTrapListeners.add(listener);
    }

    @Override
    public void removeSetTrapListener(SetPointListener listener) {
        setTrapListeners.remove(listener);
    }

    protected void fireSetTrap(Point point) {
        for (SetPointListener listener : setTrapListeners) {
            listener.setPoint(point);
        }
    }

    @Override
    public void addSetChooserListener(SetPointListener listener) {
        setChooserListeners.add(listener);
    }

    @Override
    public void removeSetChooserListener(SetPointListener listener) {
        setChooserListeners.remove(listener);
    }

    protected void fireSetChooser(Point point) {
        for (SetPointListener listener : setChooserListeners) {
            listener.setPoint(point);
        }
    }

    @Override
    public void addSelectWeaponListener(SelectWeaponListener listener) {
        selectWeaponListeners.add(listener);
    }

    @Override
    public void removeSelectWeaponListener(SelectWeaponListener listener) {
        selectWeaponListeners.remove(listener);
    }

    protected void fireSelectWeapon(Weapon weapon) {
        for (SelectWeaponListener listener : selectWeaponListeners) {
            listener.setWeapon(weapon);
        }
    }

    @Override
    public void addSetStartupWeaponsListener(SetStartupWeaponsListener listener) {
        setStartupWeaponsListeners.add(listener);
    }

    @Override
    public void removeSetStartupWeaponsListener(SetStartupWeaponsListener listener) {
        setStartupWeaponsListeners.remove(listener);
    }

    protected void fireSetStartupWeapons(Weapon[] weapons) {
        for (SetStartupWeaponsListener listener : setStartupWeaponsListeners) {
            listener.setStartupWeapons(weapons);
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public int getTeam() {
        return team;
    }

    @Override
    public void addExitListener(ActionListener listener) {
        exitListeners.add(listener);
    }

    @Override
    public void removeExitListener(ActionListener listener) {
        exitListeners.remove(listener);
    }

    protected void fireExit() {
        for (ActionListener listener : exitListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "EXIT"));
        }
    }

    @Override
    public void destroyView() {
        forceTerminate = true;
        try {
            socket.close();
        } catch (IOException ex) {
            //ignore
        }
    }
}
