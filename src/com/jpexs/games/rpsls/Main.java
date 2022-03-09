package com.jpexs.games.rpsls;

import com.jpexs.games.rpsls.model.GameType;
import com.jpexs.games.rpsls.model.RpslsModel;
import com.jpexs.games.rpsls.view.FrameView;
import com.jpexs.games.rpsls.view.NetworkView;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author JPEXS
 */
public class Main {

    private static final GameType defaultGameType = GameType.SMALL;

    public static final int PROTOCOL_VERSION_MAJOR = 1;
    public static final int PROTOCOL_VERSION_MINOR = 0;

    private static boolean forciblyTerminated = false;
    private static ServerSocket serverSocket;

    public synchronized static ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized static void setServerSocket(ServerSocket serverSocket) {
        Main.serverSocket = serverSocket;
    }

    public static void startNetworkGameDialog() {
        StartNetworkGameFrame startNetworkGameFrame = new StartNetworkGameFrame();
        startNetworkGameFrame.setVisible(true);
    }

    public static void joinNetworkGameDialog() {
        JoinNetworkGameFrame joinNetworkGameFrame = new JoinNetworkGameFrame();
        joinNetworkGameFrame.setVisible(true);
    }

    public static void startLocalGame() {
        RpslsModel model = new RpslsModel(defaultGameType);
        RpslsController controller = new RpslsController(model);
        FrameView frame0 = new FrameView(model, 0);
        controller.addView(frame0);
        FrameView frame1 = new FrameView(model, 1);
        controller.addView(frame1);

        frame0.setLocation(50, 20);
        frame1.setLocation(1000, 20);
        controller.start();
    }

    public static void startNetworkGame(int port) {
        WaitFrame waitFrame = new WaitFrame("Waiting for connection...", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                forciblyTerminated = true;
                try {
                    getServerSocket().close();
                } catch (IOException ex) {
                    //ignore
                }
                Main.mainMenu();
            }
        });
        waitFrame.setVisible(true);

        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    ServerSocket serverSocket = new ServerSocket(port);
                    setServerSocket(serverSocket);
                    Socket socket = null;
                    socket = serverSocket.accept();
                    waitFrame.setVisible(false);
                    networkGameConnected(socket, 0);
                } catch (IOException ex) {
                    if (!forciblyTerminated) {
                        waitFrame.setVisible(false);
                        showError(ex);
                    }
                }
                return null;
            }
        };
        worker.execute();

    }

    private static void networkGameConnected(Socket socket, int team) throws IOException {
        int otherTeam = team == 0 ? 1 : 0;
        RpslsModel model = new RpslsModel(defaultGameType);
        RpslsController controller = new RpslsController(model);
        FrameView localView = new FrameView(model, team);
        controller.addView(localView);
        NetworkView remoteView = new NetworkView(model, otherTeam, socket);
        controller.addView(remoteView);
        controller.initNetworkGame(socket);
        controller.start();
    }

    public static void showError(Throwable exception) {
        showError(exception.getMessage());
    }

    public static void showError(String error) {
        JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void joinNetworkGame(String address, int port) {
        WaitFrame waitFrame = new WaitFrame("Connecting...", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.mainMenu();
            }
        });
        waitFrame.setVisible(true);

        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    Socket socket = new Socket(address, port);
                    waitFrame.setVisible(false);
                    networkGameConnected(socket, 1);
                } catch (IOException ex) {
                    waitFrame.setVisible(false);
                    showError(ex);
                }
                return null;
            }
        };
        worker.execute();

    }

    public static void mainMenu() {
        MainMenuFrame gameMenu = new MainMenuFrame();
        gameMenu.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        initUi();
        mainMenu();
    }

    private static void initUi() {
        System.setProperty("sun.java2d.uiScale", "1.0");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
            //ignore
        }
    }

    public static void centerWindow(Window f) {
        GraphicsDevice[] allDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        int topLeftX, topLeftY, screenX, screenY, windowPosX, windowPosY;

        int screen = 0;

        if (screen < allDevices.length && screen > -1) {
            topLeftX = allDevices[screen].getDefaultConfiguration().getBounds().x;
            topLeftY = allDevices[screen].getDefaultConfiguration().getBounds().y;

            screenX = allDevices[screen].getDefaultConfiguration().getBounds().width;
            screenY = allDevices[screen].getDefaultConfiguration().getBounds().height;

            Insets bounds = Toolkit.getDefaultToolkit().getScreenInsets(allDevices[screen].getDefaultConfiguration());
            screenX = screenX - bounds.right;
            screenY = screenY - bounds.bottom;
        } else {
            topLeftX = allDevices[0].getDefaultConfiguration().getBounds().x;
            topLeftY = allDevices[0].getDefaultConfiguration().getBounds().y;

            screenX = allDevices[0].getDefaultConfiguration().getBounds().width;
            screenY = allDevices[0].getDefaultConfiguration().getBounds().height;

            Insets bounds = Toolkit.getDefaultToolkit().getScreenInsets(allDevices[0].getDefaultConfiguration());
            screenX = screenX - bounds.right;
            screenY = screenY - bounds.bottom;
        }

        windowPosX = ((screenX - f.getWidth()) / 2) + topLeftX;
        windowPosY = ((screenY - f.getHeight()) / 2) + topLeftY;

        f.setLocation(windowPosX, windowPosY);
    }

}
