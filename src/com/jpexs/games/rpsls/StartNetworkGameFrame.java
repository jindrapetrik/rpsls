package com.jpexs.games.rpsls;

import com.jpexs.games.rpsls.model.GameType;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author JPEXS
 */
public class StartNetworkGameFrame extends JFrame {

    public StartNetworkGameFrame() {
        setTitle("RPSLS - Start network game");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.mainMenu();
            }
        });
        Container container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        JPanel portPanel = new JPanel(new FlowLayout());
        JTextField portField = new JTextField(Main.getConfig("start_network_game.port", "1024"), 5);

        portPanel.add(new JLabel("Port:"));
        portPanel.add(portField);
        container.add(portPanel);

        JLabel gameTypeLabel = new JLabel("Game type:");
        gameTypeLabel.setAlignmentX(0.5f);
        container.add(gameTypeLabel);
        JComboBox<GameType> gameTypeComboBox = new JComboBox<>(GameType.values());
        gameTypeComboBox.setSelectedIndex(Integer.parseInt(Main.getConfig("start_network_game.game_type", "0")));
        container.add(gameTypeComboBox);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int port = Integer.parseInt(portField.getText());

                    Main.setConfig("start_network_game.port", "" + port);
                    Main.setConfig("start_network_game.game_type", "" + gameTypeComboBox.getSelectedIndex());

                    setVisible(false);
                    Main.startNetworkGame(port, (GameType) gameTypeComboBox.getSelectedItem());

                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(StartNetworkGameFrame.this, "Invalid port", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                Main.mainMenu();
            }
        });

        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        container.add(buttonsPanel);

        pack();
        setResizable(false);
        Main.centerWindow(this);
        Main.setWindowIcon(this);
    }

}
