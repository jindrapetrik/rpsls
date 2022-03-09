package com.jpexs.games.rpsls.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author JPEXS
 */
public class RpslsModel {

    public static final int NUM_TEAMS = 2;

    private Person[][] board;

    private Phase[] teamPhases = new Phase[NUM_TEAMS];

    public static int NO_TEAM = -1;

    private int teamOnTurn = NO_TEAM;

    private int winner = NO_TEAM;

    private Point[] duel = null;
    private Weapon[] duelNewWeapons = null;

    private List<ActionListener> modelUpdateListeners = new ArrayList<>();
    private List<ActionListener> proceedRequiredListeners = new ArrayList<>();
    private List<ActionListener> attackListeners = new ArrayList<>();
    private List<ActionListener> flagFoundListeners = new ArrayList<>();
    private List<ActionListener> trapFoundListeners = new ArrayList<>();

    private boolean proceed[];

    private Attack attack;

    private ActionListener proceedAction = null;

    private Move lastMove;

    private GameType gameType;

    public RpslsModel(GameType gameType) {
        this.gameType = gameType;
        newGame();
    }

    public GameType getGameType() {
        return gameType;
    }

    public boolean isWaitingOnOpponent(int team) {

        int otherTeam = team == 0 ? 1 : 0;

        if (getTeamPhase(team) == Phase.PLAY && getTeamPhase(otherTeam) != Phase.PLAY) {
            return true;
        }

        if (teamOnTurn == NO_TEAM) {
            return false;
        }
        if (isWeaponSelectionNeeded(team)) {
            return false;
        }

        if (isWeaponSelectionNeeded(otherTeam)) {
            return true;
        }

        if (proceedRequired()) {
            return false;
        }

        if (teamOnTurn != team) {
            return true;
        }
        return false;
    }

    private synchronized void setAttack(Attack attack) {
        this.attack = attack;
    }

    public synchronized Attack getAttack() {
        return attack;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public void addProceedRequiredListener(ActionListener listener) {
        proceedRequiredListeners.add(listener);
    }

    public void removeProceedRequiredListener(ActionListener listener) {
        proceedRequiredListeners.remove(listener);
    }

    public void addTrapFoundListener(ActionListener listener) {
        trapFoundListeners.add(listener);
    }

    public void removeTrapFoundListener(ActionListener listener) {
        trapFoundListeners.remove(listener);
    }

    public void addFlagFoundListener(ActionListener listener) {
        flagFoundListeners.add(listener);
    }

    public void removeFlagFoundListener(ActionListener listener) {
        flagFoundListeners.remove(listener);
    }

    public void addUpdateListener(ActionListener listener) {
        modelUpdateListeners.add(listener);
    }

    public void removeUpdateListener(ActionListener listener) {
        modelUpdateListeners.remove(listener);
    }

    public void addAttackListener(ActionListener listener) {
        attackListeners.add(listener);
    }

    public void removeAttackListener(ActionListener listener) {
        attackListeners.remove(listener);
    }

    private void fireProceedRequired() {
        for (ActionListener listener : proceedRequiredListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "PROCEED_REQUIRED"));
        }
    }

    private void fireUpdate() {
        for (ActionListener listener : modelUpdateListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "UPDATE"));
        }
    }

    private void fireAttack() {
        for (ActionListener listener : attackListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "ATTACK"));
        }
    }

    private void fireFlagFound() {
        for (ActionListener listener : flagFoundListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "FLAG_FOUND"));
        }
    }

    private void fireTrapFound() {
        for (ActionListener listener : trapFoundListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "TRAP_FOUND"));
        }
    }

    public synchronized void newGame() {
        board = new Person[getBoardWidth()][getBoardHeight()];
        teamPhases[0] = Phase.FLAGS;
        teamPhases[1] = Phase.FLAGS;

        int numRowsPerTeam = getNumRowsPerTeam();

        int startRow0 = getTeamStartRow(0);

        for (int y = startRow0; y < startRow0 + numRowsPerTeam; y++) {
            for (int x = 0; x < getBoardWidth(); x++) {
                board[x][y] = new Person(0, null, null);
            }
        }

        int startRow1 = getTeamStartRow(1);

        for (int y = startRow1; y < startRow1 + numRowsPerTeam; y++) {
            for (int x = 0; x < getBoardWidth(); x++) {
                board[x][y] = new Person(1, null, null);
            }
        }

        duel = null;
        duelNewWeapons = null;
        teamOnTurn = NO_TEAM;
        proceed = new boolean[]{true, true};
        proceedAction = null;
    }

    public Point getDuelSource() {
        if (duel == null) {
            return null;
        }
        return duel[0];
    }

    public Point getDuelTarget() {
        if (duel == null) {
            return null;
        }
        return duel[1];
    }

    private void requestProceed(ActionListener proceedAction) {
        proceed = new boolean[]{false, false};
        this.proceedAction = proceedAction;
        fireProceedRequired();
    }

    public boolean proceedRequired(int team) {
        return !proceed[team];
    }

    public synchronized void proceed(int team) {
        proceed[team] = true;
        if (!proceedRequired() && proceedAction != null) {
            proceedAction.actionPerformed(new ActionEvent(this, 0, "PROCEED"));
            proceedAction = null;
        }
    }

    private boolean proceedRequired() {
        return !proceed[0] || !proceed[1];
    }

    private boolean isPointOutsideBoard(Point p) {
        if (p.getX() < 0 || p.getX() >= getBoardWidth() || p.getY() < 0 || p.getY() >= getBoardHeight()) {
            return true;
        }
        return false;
    }

    private Person getPersonOnLocation(Point location) {
        return board[location.getBaseTeamX()][location.getBaseTeamY()];
    }

    private void setPersonOnLocation(Point location, Person person) {
        board[location.getBaseTeamX()][location.getBaseTeamY()] = person;
    }

    public int getTeamAt(int observingTeam, Point location) {
        if (isPointOutsideBoard(location)) {
            return NO_TEAM;
        }
        Person person = getPersonOnLocation(location);
        if (person == null) {
            return NO_TEAM;
        }
        return person.getTeam();
    }

    public Weapon getWeaponAt(int observingTeam, Point location) {
        if (isPointOutsideBoard(location)) {
            return null;
        }
        Person person = getPersonOnLocation(location);
        if (person == null) {
            return null;
        }
        if (person.getTeam() != observingTeam) {
            if (person.isWeaponVisible()) {
                return person.getWeapon();
            }
            return null;
        }

        return person.getWeapon();
    }

    /**
     * WARNING: use only for networkplay
     *
     * @param observingTeam
     * @param location
     * @param weapon
     */
    public void setWeaponAt(int observingTeam, Point location, Weapon weapon) {
        if (getTeamPhase(observingTeam) != Phase.WEAPONS) {
            throw new RuntimeException("Cannot set weapons in this phase");
        }
        if (isPointOutsideBoard(location)) {
            throw new IllegalArgumentException("Not valid point");
        }
        Person person = getPersonOnLocation(location);
        if (person == null) {
            throw new IllegalArgumentException("Not a person");
        }
        if (person.getSpecialItem() != null && weapon != null) {
            throw new IllegalArgumentException("Cannot set weapon for person with special item");
        }
        person.setWeapon(weapon);
    }

    public boolean isWeaponVisibleAt(int observingTeam, Point location) {
        if (isPointOutsideBoard(location)) {
            return false;
        }
        Person person = getPersonOnLocation(location);
        if (person == null) {
            return false;
        }
        return person.isWeaponVisible();
    }

    public SpecialItem getSpecialAt(int observingTeam, Point location) {
        if (isPointOutsideBoard(location)) {
            return null;
        }
        Person person = getPersonOnLocation(location);
        if (person == null) {
            return null;
        }

        if (person.getSpecialItem() == SpecialItem.OPONENT_FLAG) {
            return SpecialItem.OPONENT_FLAG;
        }

        if (person.getTeam() != observingTeam) {
            return null;
        }
        return person.getSpecialItem();
    }

    public boolean isValidMove(int team, Point source, Point destination) {
        return getValidMoves(team, source).contains(destination);
    }

    private synchronized void win() {
        winner = teamOnTurn;
        teamOnTurn = NO_TEAM;
    }

    public int getWinner() {
        return winner;
    }

    public List<Point> getValidMoves(int team, Point location) {

        List<Point> validMoves = new ArrayList<>();

        if (teamOnTurn != team) {
            return validMoves;
        }

        if (proceedRequired()) {
            return validMoves;
        }

        if (isPointOutsideBoard(location)) {
            return validMoves;
        }

        if (isDuelActive()) {
            return validMoves;
        }

        Person person = getPersonOnLocation(location);
        if (person == null) {
            return validMoves;
        }

        if (person.getTeam() != team) {
            return validMoves;
        }

        if (person.getSpecialItem() == SpecialItem.FLAG || person.getSpecialItem() == SpecialItem.TRAP) {
            return validMoves;
        }

        Point up = new Point(location.getX(), location.getY() - 1, location.getObservingTeam(), gameType);
        if (canMoveTo(team, up)) {
            validMoves.add(up);
        }

        Point down = new Point(location.getX(), location.getY() + 1, location.getObservingTeam(), gameType);
        if (canMoveTo(team, down)) {
            validMoves.add(down);
        }

        Point left = new Point(location.getX() - 1, location.getY(), location.getObservingTeam(), gameType);
        if (canMoveTo(team, left)) {
            validMoves.add(left);
        }

        Point right = new Point(location.getX() + 1, location.getY(), location.getObservingTeam(), gameType);

        if (canMoveTo(team, right)) {
            validMoves.add(right);
        }

        return validMoves;
    }

    private boolean canMoveTo(int team, Point location) {
        if (isPointOutsideBoard(location)) {
            return false;
        }
        Person person = getPersonOnLocation(location);
        if (person != null && person.getTeam() == team) {
            return false;
        }
        return true;
    }

    private synchronized void advanceTurn() {
        int newTeamOnTurn = (teamOnTurn + 1) % NUM_TEAMS;
        teamOnTurn = newTeamOnTurn;
        fireUpdate();
    }

    public synchronized boolean isWeaponSelectionNeeded(int team) {
        if (duel != null) {
            Person attackingPerson = getPersonOnLocation(duel[0]);
            if (attackingPerson.getTeam() == team) {
                return duelNewWeapons[0] == null;
            } else {
                return duelNewWeapons[1] == null;
            }

        }
        return false;
    }

    public synchronized void selectDuelWeapon(int team, Weapon weapon) {
        if (!isWeaponSelectionNeeded(team)) {
            throw new RuntimeException("Weapon already selected or not in a duel");
        }
        Person attackingPerson = getPersonOnLocation(duel[0]);
        if (attackingPerson.getTeam() == team) {
            duelNewWeapons[0] = weapon;
        } else {
            duelNewWeapons[1] = weapon;
        }
        if (duelNewWeapons[0] != null && duelNewWeapons[1] != null) {
            Point sourcePos = duel[0];
            Point targetPos = duel[1];
            Person sourcePerson = getPersonOnLocation(sourcePos);
            Person targetPerson = getPersonOnLocation(targetPos);

            sourcePerson.setWeapon(duelNewWeapons[0]);
            targetPerson.setWeapon(duelNewWeapons[1]);
            duel = null;
            duelNewWeapons = null;
            move(sourcePerson.getTeam(), sourcePos, targetPos);
        } else {
            fireUpdate();
        }
    }

    public synchronized void move(int team, Point source, Point destination) {
        if (!isValidMove(team, source, destination)) {
            throw new RuntimeException("Invalid move");
        }
        Person attackingPerson = getPersonOnLocation(source);
        Person defendingPerson = getPersonOnLocation(destination);
        Move move = new Move(source, destination, attackingPerson == null ? NO_TEAM : attackingPerson.getTeam(), defendingPerson == null ? NO_TEAM : defendingPerson.getTeam());

        if (defendingPerson != null) {
            fireUpdate();
            if (defendingPerson.getSpecialItem() == SpecialItem.FLAG) {
                lastMove = move;
                setPersonOnLocation(destination, attackingPerson);
                setPersonOnLocation(source, null);
                attackingPerson.setSpecialItem(SpecialItem.OPONENT_FLAG);
                fireUpdate();
                win();
                fireFlagFound();
            } else if (defendingPerson.getSpecialItem() == SpecialItem.TRAP) {
                lastMove = move;
                fireTrapFound();
                requestProceed(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setPersonOnLocation(source, null);
                        advanceTurn();
                    }
                });

            } else if (defendingPerson.getWeapon() == attackingPerson.getWeapon()
                    || attackingPerson.getSpecialItem() == SpecialItem.CHOOSER
                    || defendingPerson.getSpecialItem() == SpecialItem.CHOOSER) {
                duel = new Point[]{
                    source,
                    destination
                };
                if (attackingPerson.getSpecialItem() == SpecialItem.CHOOSER
                        && defendingPerson.getSpecialItem() != SpecialItem.CHOOSER) {
                    duelNewWeapons = new Weapon[]{null, defendingPerson.getWeapon()};
                } else if (attackingPerson.getSpecialItem() != SpecialItem.CHOOSER
                        && defendingPerson.getSpecialItem() == SpecialItem.CHOOSER) {
                    duelNewWeapons = new Weapon[]{attackingPerson.getWeapon(), null};
                } else {
                    duelNewWeapons = new Weapon[2];
                }

                if (attackingPerson.getSpecialItem() == SpecialItem.CHOOSER) {
                    attackingPerson.setSpecialItem(null);
                }
                if (defendingPerson.getSpecialItem() == SpecialItem.CHOOSER) {
                    defendingPerson.setSpecialItem(null);
                }
                if (defendingPerson.getWeapon() == attackingPerson.getWeapon()) {
                    attackingPerson.setWeaponVisible(true);
                    defendingPerson.setWeaponVisible(true);
                }
                fireUpdate();
            } else {
                attackingPerson.setWeaponVisible(true);
                defendingPerson.setWeaponVisible(true);
                setAttack(new Attack(source, destination, attackingPerson.getWeapon(), defendingPerson.getWeapon(), attackingPerson.getTeam(), defendingPerson.getTeam()));
                fireAttack();
                requestProceed(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int fightResult = attackingPerson.getWeapon().fight(defendingPerson.getWeapon());
                        if (fightResult == 1) {
                            setPersonOnLocation(destination, attackingPerson);
                            setPersonOnLocation(source, null);
                        } else {
                            setPersonOnLocation(source, null);
                        }
                        setAttack(null);
                        lastMove = move;
                        advanceTurn();
                    }
                });
            }
        } else {
            setPersonOnLocation(destination, attackingPerson);
            setPersonOnLocation(source, null);
            advanceTurn();
        }
    }

    public Phase getTeamPhase(int team) {
        return teamPhases[team];
    }

    private void setTeamPhase(int team, Phase phase) {
        teamPhases[team] = phase;
    }

    public void beginPlay(int team) {
        if (getTeamPhase(team) != Phase.WEAPONS) {
            throw new RuntimeException("Not in a phase to begin play");
        }
        setTeamPhase(team, Phase.PLAY);

        if (allTeamsReadyToBegin()) {
            teamOnTurn = 0;
        }
        fireUpdate();
    }

    public synchronized int getTeamOnTurn() {
        return teamOnTurn;
    }

    public boolean allTeamsReadyToBegin() {
        for (int team = 0; team < NUM_TEAMS; team++) {
            if (getTeamPhase(team) != Phase.PLAY) {
                return false;
            }
        }
        return true;
    }

    public int getBoardWidth() {
        return gameType.getBoardWidth();
    }

    public int getBoardHeight() {
        return gameType.getBoardHeight();
    }

    public void shuffleWeapons(int team) {
        if (!canShuffleWeapons(team)) {
            throw new RuntimeException("Cannot shuffle weapons now");
        }
        List<Weapon> allWeapons = new ArrayList<>();
        Weapon[] weaponValues = Weapon.values();
        int weaponCount = Weapon.values().length;
        Random random = new Random();
        for (int w = 0; w < weaponCount; w++) {
            for (int i = 0; i < gameType.getNumEachWeapon(); i++) {
                allWeapons.add(weaponValues[w]);
            }
        }
        Collections.shuffle(allWeapons, random);

        int numRowsPerTeam = getNumRowsPerTeam();

        int startRow = getTeamStartRow(team);

        int pos = 0;
        for (int y = startRow; y < startRow + numRowsPerTeam; y++) {
            for (int x = 0; x < getBoardWidth(); x++) {
                if (board[x][y].getSpecialItem() == null) {
                    board[x][y].setWeapon(allWeapons.get(pos));
                    pos++;
                }
            }
        }
        fireUpdate();
    }

    private int getTeamStartRow(int team) {
        return team == 0 ? 0 : getBoardHeight() - getNumRowsPerTeam();
    }

    public int getNumRowsPerTeam() {
        return gameType.getNumRows();
        //return (NUM_EACH_WEAPON * Weapon.values().length + SpecialItem.values().length) / getBoardWidth();
    }

    public boolean canShuffleWeapons(int team) {
        return getTeamPhase(team) == Phase.WEAPONS;
    }

    private boolean canSetSpecialOnPosition(Phase requiredPhase, int team, Point location) {
        if (getTeamPhase(team) != requiredPhase) {
            return false;
        }
        if (isPointOutsideBoard(location)) {
            return false;
        }
        Person person = getPersonOnLocation(location);
        if (person == null) {
            return false;
        }
        if (person.getTeam() != team) {
            return false;
        }
        if (person.getSpecialItem() != null) {
            return false;
        }
        return true;
    }

    public boolean canSetFlagOnPosition(int team, Point location) {
        return canSetSpecialOnPosition(Phase.FLAGS, team, location);
    }

    public boolean canSetTrapOnPosition(int team, Point location) {
        return canSetSpecialOnPosition(Phase.TRAPS, team, location);
    }

    public boolean canSetChooserOnPosition(int team, Point location) {
        return canSetSpecialOnPosition(Phase.CHOOSERS, team, location);
    }

    public void setFlagPosition(int team, Point location) {
        if (!canSetFlagOnPosition(team, location)) {
            throw new RuntimeException("Cannot set flag on position " + location);
        }
        getPersonOnLocation(location).setSpecialItem(SpecialItem.FLAG);
        setTeamPhase(team, Phase.TRAPS);
        fireUpdate();
    }

    public void setTrapPosition(int team, Point location) {
        if (!canSetTrapOnPosition(team, location)) {
            throw new RuntimeException("Cannot set trap on position " + location);
        }
        getPersonOnLocation(location).setSpecialItem(SpecialItem.TRAP);
        if (gameType.usesChooser()) {
            setTeamPhase(team, Phase.CHOOSERS);
            fireUpdate();
        } else {
            startWeaponsPhase(team);
        }

    }

    public void setChooserPosition(int team, Point location) {
        if (!canSetChooserOnPosition(team, location)) {
            throw new RuntimeException("Cannot set chooser on position " + location);
        }
        getPersonOnLocation(location).setSpecialItem(SpecialItem.CHOOSER);
        startWeaponsPhase(team);
    }

    private void startWeaponsPhase(int team) {
        setTeamPhase(team, Phase.WEAPONS);
        shuffleWeapons(team);
        fireUpdate();
    }

    public boolean isDuelActive() {
        return duel != null;
    }

}
