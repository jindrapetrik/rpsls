package com.jpexs.games.rpsls;

import com.jpexs.games.rpsls.model.GameType;
import com.jpexs.games.rpsls.model.NetworkPackets;
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
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author JPEXS
 */
public class Main {

    private static Map<String, String> config = new LinkedHashMap<>();

    public static final String SHORT_APP_NAME = "rpsls";

    public static final String CONFIG_NAME = "config.bin";

    public static final String VERSION_STRING = "v2.0";

    public static final String VENDOR_NAME = "JPEXS";

    private static final GameType DEFAULT_GAME_TYPE = GameType.RPS;

    public static final int PROTOCOL_VERSION_MAJOR = 4;
    public static final int PROTOCOL_VERSION_MINOR = 0;

    public static final int CONFIG_VERSION_MAJOR = 1;
    public static final int CONFIG_VERSION_MINOR = 0;

    /*
      Protocol changes:
        2.0 - scissors cuts paper fix
        3.0 - requested proceed after move and after same weapons attack
        4.0 - set game type
     */
    private static boolean forciblyTerminated = false;
    private static ServerSocket serverSocket;

    public static final int SOUND_MODE_OTHER_PLAYS = 0;
    public static final int SOUND_MODE_CURRENT_PLAYS = 1;
    public static final int SOUND_MODE_BOTH_PLAYS = 2;

    private static List<BufferedImage> imageIcons;

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

    public static void startLocalGameDialog() {
        StartLocalGameFrame startLocalGameFrame = new StartLocalGameFrame();
        startLocalGameFrame.setVisible(true);
    }

    public static void startLocalGame(GameType gameType) {
        RpslsModel model = new RpslsModel(gameType);
        RpslsController controller = new RpslsController(model);
        FrameView frame0 = new FrameView(model, 0, true);
        controller.addView(frame0);
        FrameView frame1 = new FrameView(model, 1, false);
        controller.addView(frame1);

        frame0.setLocation(50, 20);
        frame1.setLocation(1000, 20);
        controller.start();
    }

    public static void startNetworkGame(int port, GameType gameType) {
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

                    OutputStream outStream = socket.getOutputStream();
                    outStream.write("RPSLS".getBytes());
                    outStream.write(Main.PROTOCOL_VERSION_MAJOR);
                    outStream.write(Main.PROTOCOL_VERSION_MINOR);
                    outStream.write(NetworkPackets.PACKET_SET_GAMETYPE);
                    outStream.write(gameType.ordinal());

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

                    networkGameConnected(socket, 0, true, gameType);
                } catch (IOException ex) {
                    if (!forciblyTerminated) {
                        waitFrame.setVisible(false);
                        showError(ex);
                        mainMenu();
                    }
                }
                return null;
            }
        };
        worker.execute();

    }

    private static void networkGameConnected(Socket socket, int team, boolean playSounds, GameType gameType) throws IOException {
        int otherTeam = team == 0 ? 1 : 0;
        RpslsModel model = new RpslsModel(gameType);
        RpslsController controller = new RpslsController(model);
        FrameView localView = new FrameView(model, team, playSounds);
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
                    int packet = is.read();
                    if (packet != NetworkPackets.PACKET_SET_GAMETYPE) {
                        throw new IOException("Set gametype packet expected");
                    }
                    int gameTypeInt = is.read();
                    GameType gameType = GameType.values()[gameTypeInt];
                    OutputStream outStream = socket.getOutputStream();
                    outStream.write("RPSLS".getBytes());
                    outStream.write(Main.PROTOCOL_VERSION_MAJOR);
                    outStream.write(Main.PROTOCOL_VERSION_MINOR);

                    networkGameConnected(socket, 1, !"localhost".equals(address), gameType); //Do not play sounds twice when on localhost
                } catch (IOException ex) {
                    waitFrame.setVisible(false);
                    showError(ex);
                    mainMenu();
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
        readConfig();
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

        imageIcons = new ArrayList<>();
        try {
            imageIcons.add(ImageIO.read(Main.class.getResourceAsStream("/com/jpexs/games/rpsls/graphics/icon256.png")));
            imageIcons.add(ImageIO.read(Main.class.getResourceAsStream("/com/jpexs/games/rpsls/graphics/icon128.png")));
            imageIcons.add(ImageIO.read(Main.class.getResourceAsStream("/com/jpexs/games/rpsls/graphics/icon32.png")));
            imageIcons.add(ImageIO.read(Main.class.getResourceAsStream("/com/jpexs/games/rpsls/graphics/icon16.png")));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
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

    public static void setWindowIcon(Window f) {
        f.setIconImages(imageIcons);
    }

    private static final File unspecifiedFile = new File("unspecified");

    private static File homeDirectory = unspecifiedFile;

    private enum OSId {
        WINDOWS, OSX, UNIX
    }

    private static OSId getOSId() {
        PrivilegedAction<String> doGetOSName = new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("os.name");
            }
        };
        OSId id = OSId.UNIX;
        String osName = AccessController.doPrivileged(doGetOSName);
        if (osName != null) {
            if (osName.toLowerCase().startsWith("mac os x")) {
                id = OSId.OSX;
            } else if (osName.contains("Windows")) {
                id = OSId.WINDOWS;
            }
        }
        return id;
    }

    public static File getConfigFile() {
        String homeDir = getHomeDir();
        return new File(homeDir + CONFIG_NAME);
    }

    public static String getHomeDir() {
        if (homeDirectory == unspecifiedFile) {
            homeDirectory = null;
            String userHome = null;
            try {
                userHome = System.getProperty("user.home");
            } catch (SecurityException ignore) {
            }
            if (userHome != null) {
                String applicationId = SHORT_APP_NAME;
                OSId osId = getOSId();
                if (osId == OSId.WINDOWS) {
                    File appDataDir = null;
                    try {
                        String appDataEV = System.getenv("APPDATA");
                        if ((appDataEV != null) && (appDataEV.length() > 0)) {
                            appDataDir = new File(appDataEV);
                        }
                    } catch (SecurityException ignore) {
                    }
                    String vendorId = VENDOR_NAME;
                    if ((appDataDir != null) && appDataDir.isDirectory()) {
                        // ${APPDATA}\{vendorId}\${applicationId}
                        String path = vendorId + "\\" + applicationId + "\\";
                        homeDirectory = new File(appDataDir, path);
                    } else {
                        // ${userHome}\Application Data\${vendorId}\${applicationId}
                        String path = "Application Data\\" + vendorId + "\\" + applicationId + "\\";
                        homeDirectory = new File(userHome, path);
                    }
                } else if (osId == OSId.OSX) {
                    // ${userHome}/Library/Application Support/${applicationId}
                    String path = "Library/Application Support/" + applicationId + "/";
                    homeDirectory = new File(userHome, path);
                } else {
                    // ${userHome}/.${applicationId}/
                    String path = "." + applicationId + "/";
                    homeDirectory = new File(userHome, path);
                }
            } else {
                //no home, then use application directory
                homeDirectory = new File(".");
            }
        }
        if (!homeDirectory.exists()) {
            if (!homeDirectory.mkdirs()) {
                if (!homeDirectory.exists()) {
                    homeDirectory = new File("."); //fallback to current directory
                }
            }
        }
        String ret = homeDirectory.getAbsolutePath();
        if (!ret.endsWith(File.separator)) {
            ret += File.separator;
        }
        return ret;
    }

    public static void setConfig(String key, String value) {
        config.put(key, value);
    }

    public static String getConfig(String key, String defaultVal) {
        if (!config.containsKey(key)) {
            return defaultVal;
        }
        return config.get(key);
    }

    public static void saveConfig() {
        File configFile = getConfigFile();
        try (FileOutputStream fos = new FileOutputStream(configFile);
                DataOutputStream daos = new DataOutputStream(fos)) {
            daos.write(CONFIG_VERSION_MAJOR);
            daos.write(CONFIG_VERSION_MINOR);
            daos.writeInt(config.size());
            for (String key : config.keySet()) {
                byte data[] = key.getBytes("UTF-8");
                daos.writeInt(data.length);
                daos.write(data);

                data = config.get(key).getBytes("UTF-8");
                daos.writeInt(data.length);
                daos.write(data);
            }
        } catch (IOException ex) {
            //ignore 
        }
    }

    public static void readConfig() {
        Map<String, String> newConfig = new LinkedHashMap<>();
        File configFile = getConfigFile();
        try (FileInputStream fis = new FileInputStream(configFile);
                DataInputStream dais = new DataInputStream(fis)) {
            int versionMajor = dais.read();
            if (versionMajor != CONFIG_VERSION_MAJOR) {
                return;
            }
            int versionMinor = dais.read();
            int size = dais.readInt();
            for (int i = 0; i < size; i++) {
                int len;
                byte data[];

                len = dais.readInt();
                data = new byte[len];
                dais.readFully(data);
                String key = new String(data, "UTF-8");
                len = dais.readInt();
                data = new byte[len];
                dais.readFully(data);
                String value = new String(data, "UTF-8");
                newConfig.put(key, value);
            }
            config = newConfig;
        } catch (IOException ex) {
            //ignore
        }
    }
}
