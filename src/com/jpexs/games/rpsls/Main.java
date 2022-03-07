package com.jpexs.games.rpsls;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author JPEXS
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
            //ignore
        }

        RpslsModel model = new RpslsModel();
        RpslsView view0 = new RpslsView(model, 0);
        view0.setVisible(true);

        RpslsView view1 = new RpslsView(model, 1);
        view1.setVisible(true);
    }

}
