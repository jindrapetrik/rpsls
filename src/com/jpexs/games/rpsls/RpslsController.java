package com.jpexs.games.rpsls;

import com.jpexs.games.rpsls.model.NetworkPackets;
import com.jpexs.games.rpsls.model.Point;
import com.jpexs.games.rpsls.model.RpslsModel;
import com.jpexs.games.rpsls.model.Weapon;
import com.jpexs.games.rpsls.view.IRpslView;
import com.jpexs.games.rpsls.view.MoveListener;
import com.jpexs.games.rpsls.view.NetworkView;
import com.jpexs.games.rpsls.view.SelectWeaponListener;
import com.jpexs.games.rpsls.view.SetPointListener;
import com.jpexs.games.rpsls.view.SetStartupWeaponsListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author JPEXS
 */
public class RpslsController {

    private RpslsModel model;
    private List<IRpslView> views;
    private boolean started = false;
    private Socket socket = null;
    private OutputStream outStream = null;

    public RpslsController(RpslsModel model) {
        this.model = model;
        this.views = new ArrayList<>();
    }

    public void initNetworkGame(Socket socket) throws IOException {
        this.socket = socket;
        outStream = socket.getOutputStream();
        outStream.write("RPSLS".getBytes());
        outStream.write(Main.PROTOCOL_VERSION_MAJOR);
        outStream.write(Main.PROTOCOL_VERSION_MINOR);
    }

    public void addView(IRpslView view) {
        if (started) {
            throw new RuntimeException("Game alread started, cannot add view");
        }
        views.add(view);

        view.addExitListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (outStream != null && !(view instanceof NetworkView)) {
                    try {
                        outStream.write(NetworkPackets.PACKET_EXIT);
                    } catch (IOException ex) {
                        //ignore                        
                    }
                }
                if (view instanceof NetworkView) {
                    JOptionPane.showMessageDialog(null, "Other user terminated the game", "Exit", JOptionPane.INFORMATION_MESSAGE);
                }
                destroyViews();
                Main.mainMenu();
            }
        });

        view.addSetFlagListener(new SetPointListener() {
            @Override
            public void setPoint(Point point) {
                model.setFlagPosition(view.getTeam(), point);

                if (outStream != null && !(view instanceof NetworkView)) {
                    try {
                        outStream.write(NetworkPackets.PACKET_SET_FLAG);
                        outStream.write(point.getX());
                        outStream.write(point.getY());
                        outStream.write(point.getObservingTeam());
                    } catch (IOException ex) {
                        Main.showError(ex);
                        destroyViews();
                    }
                }
            }
        }
        );

        view.addSetTrapListener(new SetPointListener() {
            @Override
            public void setPoint(Point point) {
                model.setTrapPosition(view.getTeam(), point);

                if (outStream != null && !(view instanceof NetworkView)) {
                    try {
                        outStream.write(NetworkPackets.PACKET_SET_TRAP);
                        outStream.write(point.getX());
                        outStream.write(point.getY());
                        outStream.write(point.getObservingTeam());
                    } catch (IOException ex) {
                        Main.showError(ex);
                        destroyViews();
                    }
                }
                if (!model.getGameType().usesChooser()) {
                    sendWeapons(view);
                }
            }
        }
        );

        view.addSetChooserListener(new SetPointListener() {
            @Override
            public void setPoint(Point point) {
                model.setChooserPosition(view.getTeam(), point);

                if (outStream != null && !(view instanceof NetworkView)) {
                    try {
                        outStream.write(NetworkPackets.PACKET_SET_CHOOSER);
                        outStream.write(point.getX());
                        outStream.write(point.getY());
                        outStream.write(point.getObservingTeam());
                    } catch (IOException ex) {
                        Main.showError(ex);
                        destroyViews();
                    }
                }
                sendWeapons(view);
            }
        }
        );

        view.addMoveListener(new MoveListener() {
            @Override
            public void move(Point source, Point destination) {
                model.move(view.getTeam(), source, destination);

                if (outStream != null && !(view instanceof NetworkView)) {
                    try {
                        outStream.write(NetworkPackets.PACKET_MOVE);
                        outStream.write(source.getX());
                        outStream.write(source.getY());
                        outStream.write(source.getObservingTeam());
                        outStream.write(destination.getX());
                        outStream.write(destination.getY());
                        outStream.write(destination.getObservingTeam());
                    } catch (IOException ex) {
                        Main.showError(ex);
                        destroyViews();
                    }
                }
            }
        });

        view.addStartListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.beginPlay(view.getTeam());
                if (outStream != null && !(view instanceof NetworkView)) {
                    try {
                        outStream.write(NetworkPackets.PACKET_START);
                    } catch (IOException ex) {
                        Main.showError(ex);
                        destroyViews();
                    }
                }
            }
        });

        view.addShuffleListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.shuffleWeapons(view.getTeam());
                sendWeapons(view);
            }
        });

        view.addSetStartupWeaponsListener(new SetStartupWeaponsListener() {
            @Override
            public void setStartupWeapons(Weapon[] weapons) {
                int i = 0;
                for (int y = model.getBoardHeight() - model.getNumRowsPerTeam(); y < model.getBoardHeight(); y++) {
                    for (int x = 0; x < model.getBoardWidth(); x++) {
                        Weapon w = weapons[i];
                        model.setWeaponAt(view.getTeam(), new Point(x, y, view.getTeam(), model.getGameType()), w);
                        i++;
                    }
                }
            }
        });

        view.addSelectWeaponListener(new SelectWeaponListener() {
            @Override
            public void setWeapon(Weapon weapon) {
                model.selectDuelWeapon(view.getTeam(), weapon);
                if (outStream != null && !(view instanceof NetworkView)) {
                    try {
                        outStream.write(NetworkPackets.PACKET_SELECT_WEAPON);
                        outStream.write(weapon.ordinal());
                    } catch (IOException ex) {
                        Main.showError(ex);
                        destroyViews();
                    }
                }
            }
        });

        view.addProceedListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.proceed(view.getTeam());
                if (outStream != null && !(view instanceof NetworkView)) {
                    try {
                        outStream.write(NetworkPackets.PACKET_PROCEED);
                    } catch (IOException ex) {
                        Main.showError(ex);
                        destroyViews();
                    }
                }
            }
        });
    }

    private void sendWeapons(IRpslView view) {
        if (outStream != null && !(view instanceof NetworkView)) {
            try {
                outStream.write(NetworkPackets.PACKET_STARTUP_WEAPONS);
                for (int y = model.getBoardHeight() - model.getNumRowsPerTeam(); y < model.getBoardHeight(); y++) {
                    for (int x = 0; x < model.getBoardWidth(); x++) {
                        Weapon w = model.getWeaponAt(view.getTeam(), new Point(x, y, view.getTeam(), model.getGameType()));
                        if (w == null) {
                            outStream.write(255);
                        } else {
                            outStream.write(w.ordinal());
                        }
                    }
                }
            } catch (IOException ex) {
                Main.showError(ex);
                destroyViews();
            }
        }
    }

    private void destroyViews() {
        for (IRpslView view : views) {
            view.destroyView();
        }
    }

    public void start() {
        started = true;
        for (IRpslView view : views) {
            view.initView();
        }
    }
}
