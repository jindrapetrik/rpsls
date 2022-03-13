package com.jpexs.games.rpsls;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author JPEXS
 */
public class MainMenuFrame extends JFrame {

    private BufferedImage manualImage;

    public MainMenuFrame() {

        try {
            manualImage = ImageIO.read(getClass().getResourceAsStream("/com/jpexs/games/rpsls/graphics/manual.png"));
        } catch (IOException ex) {
            Logger.getLogger(MainMenuFrame.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        setTitle("RPSLS " + Main.VERSION_STRING + " by " + Main.VENDOR_NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container container = getContentPane();

        JPanel menuItemsPanel = new JPanel(new GridLayout(4, 1));
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
                Main.startLocalGameDialog();
            }
        });

        JButton exitGameButton = new JButton("Exit");
        exitGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuItemsPanel.add(startNetGameButton);
        menuItemsPanel.add(joinNetGameButton);
        menuItemsPanel.add(localGameButton);
        menuItemsPanel.add(exitGameButton);

        container.setLayout(new BorderLayout());
        container.add(menuItemsPanel, BorderLayout.CENTER);

        JPanel manualPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.white);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.drawImage(manualImage, getWidth() / 2 - manualImage.getWidth() / 2, 25, null);
            }

        };
        container.add(manualPanel, BorderLayout.WEST);
        manualPanel.setPreferredSize(new Dimension(250, 250));

        pack();

        setResizable(false);
        Main.centerWindow(this);
        Main.setWindowIcon(this);
    }

}
