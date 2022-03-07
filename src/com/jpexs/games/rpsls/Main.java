package com.jpexs.games.rpsls;

import com.jpexs.games.rpsls.model.RpslsModel;
import com.jpexs.games.rpsls.view.FrameView;
import com.jpexs.games.rpsls.view.IRpslView;
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
        RpslsController controller = new RpslsController(model);
        controller.addView(new FrameView(model, 0));
        controller.addView(new FrameView(model, 1));
        controller.start();
    }

}
