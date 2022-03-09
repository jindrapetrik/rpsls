package com.jpexs.games.rpsls.view;

import com.jpexs.games.rpsls.Main;
import com.jpexs.games.rpsls.model.Attack;
import com.jpexs.games.rpsls.model.Move;
import com.jpexs.games.rpsls.model.Phase;
import com.jpexs.games.rpsls.model.Point;
import com.jpexs.games.rpsls.model.RpslsModel;
import com.jpexs.games.rpsls.model.SpecialItem;
import com.jpexs.games.rpsls.model.Weapon;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author JPEXS
 */
public class FrameView extends JFrame implements IRpslView {

    private BufferedImage spritesImage;
    private BufferedImage[] teamSpritesImage;
    private RpslsModel model;

    private Color[] teamColors = new Color[]{Color.red, Color.blue};
    private String[] teamColorNames = new String[]{"RED", "BLUE"};

    private JPanel contentPanel = new JPanel();

    private final int FIELD_SIZE = 100;
    private final int AVAILABLE_MOVE_SIZE = 50;

    private final int BOARD_BORDER = FIELD_SIZE / 2;

    private final int SPRITE_Y_OFFSET = -20;

    private final int SPRITE_WIDTH = 100;
    private final int SPRITE_HEIGHT = 100;

    private final int WEAPON_SELECTION_TOP_HEIGHT = 30;

    private int myTeam = RpslsModel.NO_TEAM;

    private JPanel shuffleStartPanel;

    private JButton shuffleButton;

    private JButton startButton;

    private Point hilightedPoint = null;

    private Point selectedPoint = null;

    private boolean hilightedShuffle = false;

    private boolean hilightedStart = false;

    private int fightPhase = 0;

    private Weapon hilightedWeapon = null;

    private JPanel rightPanel;

    private Move trapMove = null;
    private int trapPhase = 0;

    private List<SetPointListener> setFlagListeners = new ArrayList<>();
    private List<SetPointListener> setTrapListeners = new ArrayList<>();
    private List<SetPointListener> setChooserListeners = new ArrayList<>();
    private List<ActionListener> startListeners = new ArrayList<>();
    private List<ActionListener> shuffleListeners = new ArrayList<>();
    private List<ActionListener> proceedListeners = new ArrayList<>();
    private List<MoveListener> moveListeners = new ArrayList<>();
    private List<SelectWeaponListener> selectWeaponListeners = new ArrayList<>();
    private List<ActionListener> exitListeners = new ArrayList<>();

    private final int ANIMATIION_PHASES_COUNT = 3;
    private int animationPhases[][];

    private Timer animationTimer;

    private void setTrapPhase(int trapPhase) {
        this.trapPhase = trapPhase;
    }

    private synchronized int getTrapPhase() {
        return trapPhase;
    }

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
        shuffleListeners.add(listener);
    }

    @Override
    public void removeShuffleListener(ActionListener listener) {
        shuffleListeners.remove(listener);
    }

    protected void fireShuffle() {
        for (ActionListener listener : shuffleListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "SHUFFLE"));
        }
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

    }

    @Override
    public void removeSetStartupWeaponsListener(SetStartupWeaponsListener listener) {

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

    private synchronized int getFightPhase() {
        return fightPhase;
    }

    private synchronized void setFightPhase(int fightPhase) {
        this.fightPhase = fightPhase;
    }

    public FrameView(RpslsModel model, int team) {
        this.model = model;
        myTeam = team;

        animationPhases = new int[model.getBoardWidth()][model.getBoardHeight()];
        animationTimer = new Timer();
        animationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Random rnd = new Random();
                final int DO_MOVE_PERCENT = 30;
                if (rnd.nextInt(100) > DO_MOVE_PERCENT) {
                    return;
                }

                int x = rnd.nextInt(model.getBoardWidth());
                int y = rnd.nextInt(model.getBoardHeight());
                int phase = rnd.nextInt(ANIMATIION_PHASES_COUNT);
                animationPhases[x][y] = phase;
                contentPanel.repaint();
            }
        }, 1000, 1000);

        setTitle("RPSLS, team " + (team + 1) + " - " + teamColorNames[team]);
        setSize(800, 800);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                fireExit();
            }
        });

        try {
            spritesImage = ImageIO.read(getClass().getResourceAsStream("/com/jpexs/games/rpsls/graphics/sprites.png"));
        } catch (IOException ex) {
            Logger.getLogger(FrameView.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        teamSpritesImage = new BufferedImage[2];
        for (int i = 0; i < teamColors.length; i++) {
            teamSpritesImage[i] = dye(spritesImage, teamColors[i]);
        }
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Attack attack = model.getAttack();
                if (!model.proceedRequired(myTeam)) {
                    attack = null;
                }
                for (int y = 0; y < model.getBoardHeight(); y++) {
                    for (int x = 0; x < model.getBoardWidth(); x++) {
                        if ((x + y % 2) % 2 == 0) {
                            g.setColor(new Color(0x88, 0x55, 0x22));
                        } else {
                            g.setColor(new Color(0xdd, 0xcc, 0xcc));
                        }
                        g.fillRect(BOARD_BORDER + x * FIELD_SIZE, BOARD_BORDER + y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
                    }
                }
                Point currentSelectedPoint = selectedPoint;
                if (currentSelectedPoint != null) {
                    g.setColor(Color.yellow);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setStroke(new BasicStroke(4f));
                    g2d.drawRect(BOARD_BORDER + currentSelectedPoint.getX() * FIELD_SIZE, BOARD_BORDER + currentSelectedPoint.getY() * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);

                    List<Point> moves = model.getValidMoves(myTeam, currentSelectedPoint);
                    g.setColor(Color.black);
                    for (Point move : moves) {
                        g2d.setStroke(new BasicStroke(1f));
                        g2d.drawOval(BOARD_BORDER + move.getX() * FIELD_SIZE + FIELD_SIZE / 2 - AVAILABLE_MOVE_SIZE / 2, BOARD_BORDER + move.getY() * FIELD_SIZE + FIELD_SIZE / 2 - AVAILABLE_MOVE_SIZE / 2, AVAILABLE_MOVE_SIZE, AVAILABLE_MOVE_SIZE);
                    }
                }
                for (int y = 0; y < model.getBoardHeight(); y++) {
                    for (int x = 0; x < model.getBoardWidth(); x++) {
                        Point location = new Point(x, y, myTeam, model.getGameType());
                        Move currentTrapMove = trapMove;
                        if (currentTrapMove != null) {
                            if (currentTrapMove.target.equals(new Point(x, y, myTeam, model.getGameType()))) {
                                paintSprite(g, BOARD_BORDER + x * FIELD_SIZE, SPRITE_Y_OFFSET + BOARD_BORDER + y * FIELD_SIZE, currentTrapMove.sourceTeam, 1, 5 + trapPhase, false);
                                if (currentTrapMove.targetTeam == myTeam) {
                                    paintSprite(g, BOARD_BORDER + x * FIELD_SIZE, SPRITE_Y_OFFSET + BOARD_BORDER + y * FIELD_SIZE, currentTrapMove.targetTeam, 1, 4, false);
                                } else {
                                    paintSprite(g, BOARD_BORDER + x * FIELD_SIZE, SPRITE_Y_OFFSET + BOARD_BORDER + y * FIELD_SIZE, currentTrapMove.targetTeam, 0, 4, false);
                                }
                                continue;
                            }
                            if (currentTrapMove.source.equals(new Point(x, y, myTeam, model.getGameType()))) {
                                continue;
                            }
                        }
                        int team = model.getTeamAt(myTeam, location);
                        Weapon weapon = model.getWeaponAt(myTeam, location);
                        boolean weaponVisible = model.isWeaponVisibleAt(myTeam, location);
                        SpecialItem specialItem = model.getSpecialAt(myTeam, location);
                        if (attack != null) {
                            if (attack.source.equals(location) || attack.target.equals(location)) {
                                continue;
                            }
                        }
                        int deltaX = 0;
                        int deltaY = 0;

                        if (attack != null) {
                            if (attack.source.getYForTeam(myTeam) != attack.target.getYForTeam(myTeam)) {
                                if (y == attack.target.getYForTeam(myTeam)) {
                                    if (x == attack.target.getXForTeam(myTeam) - 1) {
                                        deltaX -= FIELD_SIZE / 3;
                                    } else if (x == attack.target.getXForTeam(myTeam) + 1) {
                                        deltaX += FIELD_SIZE / 3;
                                    }
                                }
                            }
                        }

                        int animationPhase = animationPhases[x][y];
                        paintPerson(g, deltaX + BOARD_BORDER + x * FIELD_SIZE, deltaY + BOARD_BORDER + SPRITE_Y_OFFSET + y * FIELD_SIZE, team, weapon, weaponVisible, specialItem, animationPhase);
                    }
                }

                Phase phase = model.getTeamPhase(myTeam);
                switch (phase) {
                    case FLAGS:
                        paintMessage(g, "Set your flag position");
                        break;
                    case TRAPS:
                        paintMessage(g, "Set trap position");
                        break;
                    case CHOOSERS:
                        paintMessage(g, "Set chooser position");
                        break;
                }

                if (attack != null) {
                    paintAttack(g, attack);
                }

                if (model.isWeaponSelectionNeeded(myTeam)) {
                    paintWeaponSelection(g);
                }

                if (model.getTeamPhase(myTeam) == Phase.WEAPONS) {
                    paintShuffleStartOptions(g);
                }
            }

            private void paintShuffleStartOptions(Graphics g) {
                g.setColor(Color.yellow);
                int windowHeight = FIELD_SIZE;

                g.fillRect(BOARD_BORDER, BOARD_BORDER + model.getBoardHeight() * FIELD_SIZE / 2 - windowHeight / 2, model.getBoardWidth() * FIELD_SIZE, windowHeight);
                String textToPaint;
                int textWidth;
                Rectangle r;

                g.setColor(Color.black);
                g.setFont(g.getFont().deriveFont(20f));
                textToPaint = "SHUFFLE";
                textWidth = g.getFontMetrics().stringWidth(textToPaint);
                g.drawString(textToPaint,
                        BOARD_BORDER + model.getBoardWidth() * FIELD_SIZE / 2 - 10 - textWidth,
                        BOARD_BORDER + model.getBoardHeight() * FIELD_SIZE / 2 - windowHeight / 2 + 38 + g.getFont().getSize());
                r = getShuffleButtonRect();
                g.drawRect(r.x, r.y, r.width, r.height);
                textToPaint = "START";
                textWidth = g.getFontMetrics().stringWidth(textToPaint);
                g.drawString(textToPaint,
                        BOARD_BORDER + model.getBoardWidth() * FIELD_SIZE / 2 + 10,
                        BOARD_BORDER + model.getBoardHeight() * FIELD_SIZE / 2 - windowHeight / 2 + 38 + g.getFont().getSize());
                r = getStartButtonRect();
                g.drawRect(r.x, r.y, r.width, r.height);
            }

            private void paintWeaponSelection(Graphics g) {
                g.setColor(Color.yellow);
                int weaponsCount = Weapon.values().length;
                int selectionTop = getWeaponSelectionTop();
                g.fillRect(BOARD_BORDER, selectionTop, model.getBoardWidth() * FIELD_SIZE, WEAPON_SELECTION_TOP_HEIGHT + FIELD_SIZE);
                g.setColor(Color.black);
                g.setFont(g.getFont().deriveFont(20f));
                String textToPrint = "Select weapon";
                int textWidth = g.getFontMetrics().stringWidth(textToPrint);
                g.drawString(textToPrint, BOARD_BORDER + FIELD_SIZE * model.getBoardWidth() / 2 - textWidth / 2, selectionTop + 5 + g.getFont().getSize());
                for (int i = 0; i < weaponsCount; i++) {
                    Rectangle drawRect = getWeaponSelectionRect(Weapon.values()[i]);
                    paintSprite(g, drawRect.x, drawRect.y, myTeam, 2 + i, 10, false);
                }
            }

            private void paintAttack(Graphics g, Attack attack) {
                int teamLeft = attack.sourceTeam;
                int teamRight = attack.targetTeam;
                Weapon weaponLeft = attack.sourceWeapon;
                Weapon weaponRight = attack.targetWeapon;
                boolean switchSides = false;
                if (attack.target.getXForTeam(myTeam) < attack.source.getXForTeam(myTeam)) {
                    switchSides = true;
                } else if (attack.target.getXForTeam(myTeam) == attack.source.getXForTeam(myTeam) && attack.target.getYForTeam(myTeam) < attack.source.getYForTeam(myTeam)) {
                    switchSides = true;
                }

                int attackPosLeft = attack.source.getXForTeam(myTeam);
                if (switchSides) {
                    teamLeft = attack.targetTeam;
                    teamRight = attack.sourceTeam;
                    weaponLeft = attack.targetWeapon;
                    weaponRight = attack.sourceWeapon;
                    attackPosLeft = attack.target.getXForTeam(myTeam);
                }
                int attackPosTop = attack.target.getYForTeam(myTeam);
                boolean vertical = attack.source.getYForTeam(myTeam) != attack.target.getYForTeam(myTeam);

                int posLeftPixels = BOARD_BORDER + attackPosLeft * FIELD_SIZE + (vertical ? -FIELD_SIZE / 2 : 0);
                int posTopPixels = BOARD_BORDER + SPRITE_Y_OFFSET + attackPosTop * FIELD_SIZE;

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(Color.yellow);
                g2d.fillOval(posLeftPixels, posTopPixels + FIELD_SIZE - 20, FIELD_SIZE * 2, 40);

                int phase = getFightPhase();
                if (phase > 0 && weaponLeft != weaponRight) {
                    int spriteX = -1;
                    int spriteY = -1;
                    Weapon[][] weaponCombination = new Weapon[][]{
                        {Weapon.ROCK, Weapon.SCIZZORS}, {Weapon.ROCK, Weapon.LIZARD},
                        {Weapon.PAPER, Weapon.ROCK}, {Weapon.PAPER, Weapon.SPOCK},
                        {Weapon.SCIZZORS, Weapon.PAPER}, {Weapon.SCIZZORS, Weapon.LIZARD},
                        {Weapon.LIZARD, Weapon.PAPER}, {Weapon.LIZARD, Weapon.SPOCK},
                        {Weapon.SPOCK, Weapon.ROCK}, {Weapon.SPOCK, Weapon.SCIZZORS}
                    };
                    boolean flipped = false;
                    for (int i = 0; i < weaponCombination.length; i++) {
                        spriteX = 2;
                        if (i % 2 == 1) {
                            spriteX = 4;
                        }
                        if (phase == 2) {
                            spriteX += 4;
                        }
                        spriteY = 5 + i / 2;
                        if (weaponLeft == weaponCombination[i][0] && weaponRight == weaponCombination[i][1]) {
                            break;
                        }
                        if (weaponRight == weaponCombination[i][0] && weaponLeft == weaponCombination[i][1]) {
                            flipped = true;
                            break;
                        }
                    }
                    if (flipped) {
                        paintSprite(g, posLeftPixels, posTopPixels, teamLeft, spriteX + 1, spriteY, true);
                        paintSprite(g, posLeftPixels + FIELD_SIZE, posTopPixels, teamRight, spriteX, spriteY, true);
                    } else {
                        paintSprite(g, posLeftPixels, posTopPixels, teamLeft, spriteX, spriteY, false);
                        paintSprite(g, posLeftPixels + FIELD_SIZE, posTopPixels, teamRight, spriteX + 1, spriteY, false);
                    }
                } else {
                    paintSprite(g, posLeftPixels, posTopPixels, teamLeft, 2 + weaponLeft.ordinal(), 3, false);
                    paintSprite(g, posLeftPixels + FIELD_SIZE, posTopPixels, teamRight, 2 + weaponRight.ordinal(), 4, false);
                }

            }

            private void paintMessage(Graphics g, String message) {
                g.setColor(Color.yellow);
                g.fillRect(BOARD_BORDER, BOARD_BORDER + FIELD_SIZE * model.getBoardHeight() / 2 - FIELD_SIZE / 2,
                        model.getBoardWidth() * FIELD_SIZE, FIELD_SIZE);
                g.setColor(Color.black);
                g.setFont(g.getFont().deriveFont(20f));
                int width = g.getFontMetrics().stringWidth(message);
                g.drawString(message, BOARD_BORDER + model.getBoardWidth() * FIELD_SIZE / 2 - width / 2, BOARD_BORDER + FIELD_SIZE * model.getBoardHeight() / 2 + 4);
            }

            private void paintPerson(Graphics g, int x, int y, int team, Weapon weapon, boolean weaponVisible, SpecialItem specialItem, int animationPhase) {
                int spriteX = -1;
                int spriteY = -1;
                if (team == RpslsModel.NO_TEAM) {
                    return;
                }
                if (specialItem == SpecialItem.OPONENT_FLAG) {
                    paintSprite(g, x, y, team, 0, 7, false);
                    paintSprite(g, x, y, team == 0 ? 1 : 0, 0, 6, false);
                    return;
                } else if (team != myTeam && !weaponVisible) {
                    spriteX = 7 + animationPhase;
                    spriteY = 3;
                } else if (team == myTeam && specialItem == SpecialItem.CHOOSER) {
                    spriteX = 7 + animationPhase;
                    spriteY = 0;
                } else if (team == myTeam && specialItem == SpecialItem.TRAP) {
                    spriteX = 0;
                    spriteY = 5;
                } else if (team == myTeam && specialItem == SpecialItem.FLAG) {
                    spriteX = 1;
                    spriteY = 7;
                } else if (team == myTeam && weapon == null) {
                    spriteX = 7 + animationPhase;
                    spriteY = 0;
                } else if (team == myTeam && weapon != null && !weaponVisible) {
                    paintSprite(g, x, y, team, 7 + animationPhase, 0, false);
                    paintSprite(g, x, y, team, 2 + weapon.ordinal(), 0, false);
                    return;
                } else if (team == myTeam && weapon != null && weaponVisible) {
                    paintSprite(g, x, y, team, 7 + animationPhase, 1, false);
                    paintSprite(g, x, y, team, 2 + weapon.ordinal(), 1, false);
                    paintSprite(g, x, y, team, 1, 1, false);
                    return;
                } else if (team != myTeam && weapon != null && weaponVisible) {
                    paintSprite(g, x, y, team, 7 + animationPhase, 2, false);
                    paintSprite(g, x, y, team, 2 + weapon.ordinal(), 2, false);
                    paintSprite(g, x, y, team, 1, 2, false);
                    return;
                }
                paintSprite(g, x, y, team, spriteX, spriteY, false);
            }

            private void paintSprite(Graphics g, int x, int y, int team, int spriteX, int spriteY, boolean flipped) {
                if (spriteX > -1 && spriteY > -1) {
                    if (flipped) {
                        g.drawImage(teamSpritesImage[team], x + SPRITE_WIDTH, y, x, y + SPRITE_HEIGHT,
                                spriteX * SPRITE_WIDTH, spriteY * SPRITE_HEIGHT, spriteX * SPRITE_WIDTH + SPRITE_WIDTH, spriteY * SPRITE_HEIGHT + SPRITE_HEIGHT, null);
                    } else {
                        g.drawImage(teamSpritesImage[team], x, y, x + SPRITE_WIDTH, y + SPRITE_HEIGHT,
                                spriteX * SPRITE_WIDTH, spriteY * SPRITE_HEIGHT, spriteX * SPRITE_WIDTH + SPRITE_WIDTH, spriteY * SPRITE_HEIGHT + SPRITE_HEIGHT, null);
                    }
                }
            }
        };

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (model.getTeamPhase(myTeam) == Phase.WEAPONS) {
                        if (hilightedShuffle) {
                            fireShuffle();
                        }
                        if (hilightedStart) {
                            fireStart();
                        }
                    }
                    if (model.isWeaponSelectionNeeded(myTeam)) {
                        Weapon weaponToSelect = hilightedWeapon;
                        if (weaponToSelect != null) {
                            fireSelectWeapon(weaponToSelect);
                        }
                        return;
                    }
                    if (hilightedPoint != null) {
                        switch (model.getTeamPhase(myTeam)) {
                            case FLAGS:
                                fireSetFlag(hilightedPoint);
                                hilightedPoint = null;
                                break;
                            case TRAPS:
                                fireSetTrap(hilightedPoint);
                                hilightedPoint = null;
                                break;
                            case CHOOSERS:
                                fireSetChooser(hilightedPoint);
                                hilightedPoint = null;
                                pack();
                                break;
                            case PLAY:
                                if (selectedPoint != null) {
                                    if (model.getTeamAt(myTeam, hilightedPoint) == myTeam) {
                                        selectedPoint = hilightedPoint;
                                    } else if (model.getValidMoves(myTeam, selectedPoint).contains(hilightedPoint)) {
                                        fireMove(selectedPoint, hilightedPoint);
                                        selectedPoint = null;
                                    } else {
                                        selectedPoint = null;
                                    }
                                } else {
                                    selectedPoint = hilightedPoint;
                                }
                                break;
                        }
                        repaint();
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

                if (model.getTeamPhase(myTeam) == Phase.WEAPONS) {
                    Rectangle shuffleRect = getShuffleButtonRect();

                    if (shuffleRect.contains(e.getPoint())) {
                        hilightedShuffle = true;
                        hilightedStart = false;
                        contentPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        return;
                    }
                    Rectangle startRect = getStartButtonRect();
                    if (startRect.contains(e.getPoint())) {
                        hilightedShuffle = false;
                        hilightedStart = true;
                        contentPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        return;
                    }
                    contentPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    return;
                }

                hilightedShuffle = false;
                hilightedStart = false;
                if (model.isWeaponSelectionNeeded(myTeam)) {
                    int weaponCount = Weapon.values().length;
                    for (int i = 0; i < weaponCount; i++) {
                        Rectangle weaponRect = getWeaponSelectionRect(Weapon.values()[i]);
                        if (weaponRect.contains(e.getPoint())) {
                            hilightedWeapon = Weapon.values()[i];
                            contentPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            contentPanel.repaint();
                            return;
                        }
                    }
                    hilightedWeapon = null;
                    contentPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    contentPanel.repaint();
                    return;
                }
                hilightedWeapon = null;
                int pixelX = e.getX() - BOARD_BORDER;
                int pixelY = e.getY() - BOARD_BORDER;
                int x = pixelX / FIELD_SIZE;
                int y = pixelY / FIELD_SIZE;
                Point newHilightedPoint = null;
                Attack attack = model.getAttack();

                if (pixelX < 0 || pixelY < 0 || x < 0 || x > model.getBoardWidth() || y < 0 || y > model.getBoardHeight() || (attack != null)) {
                    if (hilightedPoint != null) {
                        hilightedPoint = null;
                        contentPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        contentPanel.repaint();
                    }
                    return;
                }

                newHilightedPoint = new Point(x, y, myTeam, model.getGameType());

                switch (model.getTeamPhase(myTeam)) {
                    case WEAPONS:
                        newHilightedPoint = null;
                        break;
                    case FLAGS:
                        if (!model.canSetFlagOnPosition(myTeam, newHilightedPoint)) {
                            newHilightedPoint = null;
                        }
                        break;
                    case TRAPS:
                        if (!model.canSetTrapOnPosition(myTeam, newHilightedPoint)) {
                            newHilightedPoint = null;
                        }
                        break;
                    case CHOOSERS:
                        if (!model.canSetChooserOnPosition(myTeam, newHilightedPoint)) {
                            newHilightedPoint = null;
                        }
                        break;
                    case PLAY:
                        if (model.getTeamOnTurn() != myTeam) {
                            newHilightedPoint = null;
                        } else if (selectedPoint != null) {
                            if (!model.getValidMoves(myTeam, selectedPoint).contains(newHilightedPoint) && model.getValidMoves(myTeam, newHilightedPoint).isEmpty()) {
                                newHilightedPoint = null;
                            }
                        } else if (model.getValidMoves(myTeam, newHilightedPoint).isEmpty()) {
                            newHilightedPoint = null;
                        }
                        break;
                }

                if (newHilightedPoint == null) {
                    contentPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                } else {
                    contentPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                if (!Objects.equals(newHilightedPoint, hilightedPoint)) {
                    hilightedPoint = newHilightedPoint;
                    contentPanel.repaint();
                }
            }

        };

        contentPanel.addMouseListener(mouseAdapter);
        contentPanel.addMouseMotionListener(mouseAdapter);

        contentPanel.setPreferredSize(new Dimension(model.getBoardWidth() * FIELD_SIZE + 2 * BOARD_BORDER, model.getBoardHeight() * FIELD_SIZE + 2 * BOARD_BORDER));
        container.add(contentPanel, BorderLayout.CENTER);

        model.addUpdateListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentPanel.repaint();
                rightPanel.repaint();
            }
        });

        model.addFlagFoundListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentPanel.repaint();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (model.getWinner() == myTeam) {
                            JOptionPane.showMessageDialog(contentPanel, "You won!");
                        } else {
                            JOptionPane.showMessageDialog(contentPanel, "You lost!");
                        }
                    }
                });

            }
        });

        model.addAttackListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPoint = null;
                setFightPhase(0);
                contentPanel.repaint();

                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        setFightPhase(1);
                        contentPanel.repaint();

                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                setFightPhase(2);
                                contentPanel.repaint();

                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        fireProceed();
                                    }

                                }, 2000);
                            }

                        }, 1500);
                    }
                }, 2000);
            }
        });

        model.addTrapFoundListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trapMove = model.getLastMove();
                setTrapPhase(0);
                contentPanel.repaint();
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        setTrapPhase(1);
                        contentPanel.repaint();

                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                trapMove = null;
                                contentPanel.repaint();
                                fireProceed();
                            }
                        }, 1500);
                    }
                }, 1500);
            }
        });

        rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int currentTeam = model.getTeamOnTurn();
                int textWidth;
                String textToPrint;
                g.setFont(g.getFont().deriveFont(18f));
                if (currentTeam != RpslsModel.NO_TEAM) {

                    g.setColor(Color.black);
                    textToPrint = "Team on turn: " + teamColorNames[currentTeam];
                    textWidth = g.getFontMetrics().stringWidth(textToPrint);
                    g.drawString(textToPrint, getWidth() / 2 - textWidth / 2, getHeight() / 2 - 20);

                    g.setColor(teamColors[currentTeam]);
                    int teamRectWidth = 50;
                    g.fillRect(getWidth() / 2 - teamRectWidth / 2, getHeight() / 2, teamRectWidth, 30);

                }
                if (model.isWaitingOnOpponent(myTeam)) {
                    g.setColor(Color.black);
                    textToPrint = "Waiting for opponent...";
                    textWidth = g.getFontMetrics().stringWidth(textToPrint);
                    g.drawString(textToPrint, getWidth() / 2 - textWidth / 2, getHeight() / 2 + 60);
                }
            }

        };
        rightPanel.setPreferredSize(new Dimension(200, 100));

        container.add(rightPanel, BorderLayout.EAST);

        pack();
        Main.centerWindow(this);
    }

    public static BufferedImage dye(BufferedImage image, Color color) {
        int w = image.getWidth();
        int h = image.getHeight();

        float[] hsbDye = new float[3];
        float[] hsb = new float[3];

        hsbDye = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbDye);

        if (hsbDye[2] < 0.4f) {
            hsbDye[2] = 0.4f;
        }
        BufferedImage dyed = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int rgb = image.getRGB(x, y);
                int a = (rgb >> 24) & 0xff;

                hsb = Color.RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, hsb);
                if (hsb[0] == 0.6666667f) {
                    hsb[0] = hsbDye[0];
                    hsb[1] = hsb[1] * hsbDye[1];
                    hsb[2] = hsb[2] * hsbDye[2];
                    rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                    rgb = rgb + (a << 24);
                }

                dyed.setRGB(x, y, rgb);

            }
        }
        return dyed;
    }

    private int getWeaponSelectionTop() {
        Point source = model.getDuelSource();
        Point target = model.getDuelTarget();

        int minY = Math.min(source.getYForTeam(myTeam), target.getYForTeam(myTeam));
        int maxY = Math.max(source.getYForTeam(myTeam), target.getYForTeam(myTeam));

        if (maxY < model.getBoardHeight() / 2) {
            return BOARD_BORDER + maxY * FIELD_SIZE + FIELD_SIZE;
        } else {
            return BOARD_BORDER + minY * FIELD_SIZE - FIELD_SIZE + SPRITE_Y_OFFSET - WEAPON_SELECTION_TOP_HEIGHT;
        }
    }

    private Rectangle getWeaponSelectionRect(Weapon weapon) {
        int weaponsCount = Weapon.values().length;
        return new Rectangle(BOARD_BORDER + FIELD_SIZE * model.getBoardWidth() / 2 - SPRITE_WIDTH * weaponsCount / 2 + weapon.ordinal() * SPRITE_WIDTH,
                getWeaponSelectionTop() + WEAPON_SELECTION_TOP_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
    }

    private Rectangle getShuffleButtonRect() {
        Graphics g = contentPanel.getGraphics();
        g.setFont(g.getFont().deriveFont(20f));
        String textToPaint = "SHUFFLE";
        int textWidth = g.getFontMetrics().stringWidth(textToPaint);
        int windowHeight = FIELD_SIZE;
        return new Rectangle(BOARD_BORDER + model.getBoardWidth() * FIELD_SIZE / 2 - 10 - textWidth - 5,
                BOARD_BORDER + model.getBoardHeight() * FIELD_SIZE / 2 - windowHeight / 2 + 38 - 5,
                textWidth + 2 * 5, 20 + 2 * 5);

    }

    private Rectangle getStartButtonRect() {
        int windowHeight = FIELD_SIZE;
        Graphics g = contentPanel.getGraphics();
        g.setFont(g.getFont().deriveFont(20f));
        String textToPaint = "START";
        int textWidth = g.getFontMetrics().stringWidth(textToPaint);
        return new Rectangle(BOARD_BORDER + model.getBoardWidth() * FIELD_SIZE / 2 + 10 - 5,
                BOARD_BORDER + model.getBoardHeight() * FIELD_SIZE / 2 - windowHeight / 2 + 38 - 5,
                textWidth + 2 * 5, 20 + 2 * 5);
    }

    @Override
    public void initView() {
        setVisible(true);
    }

    @Override
    public int getTeam() {
        return myTeam;
    }

    @Override
    public void destroyView() {
        animationTimer.cancel();
        setVisible(false);
    }
}
