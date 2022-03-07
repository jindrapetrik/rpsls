package com.jpexs.games.rpsls;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author JPEXS
 */
public class MainMenuFrame extends JFrame {

    public MainMenuFrame() {
        setTitle("RPSLS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container container = getContentPane();
        container.setLayout(new GridLayout(4, 1));
        JButton startNetGameButton = new JButton("Start network game");
        startNetGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                Main.startNetworkGameDialog();
            }
        });

        JButton joinNetGameButton = new JButton("Join network game");
        joinNetGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                Main.joinNetworkGameDialog();
            }
        });

        JButton localGameButton = new JButton("Local game");
        localGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                Main.startLocalGame();
            }
        });

        JButton exitGameButton = new JButton("Exit");
        exitGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        container.add(startNetGameButton);
        container.add(joinNetGameButton);
        container.add(localGameButton);
        container.add(exitGameButton);

        //pack();
        setSize(400, 300);

        setResizable(false);
        Main.centerWindow(this);
    }

}
