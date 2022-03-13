package com.jpexs.games.rpsls;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author JPEXS
 */
public class JoinNetworkGameFrame extends JFrame {

    public JoinNetworkGameFrame() {
        setTitle("RPSLS - Join network game");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.mainMenu();
            }
        });
        Container container = getContentPane();

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setAlignmentX(0.5f);
        container.add(addressLabel);

        JTextField addressField = new JTextField(Main.getConfig("join_network_game.address", "localhost"), 10);
        container.add(addressField);

        JPanel portPanel = new JPanel(new FlowLayout());
        JTextField portField = new JTextField(Main.getConfig("join_network_game.port", "1024"), 5);

        portPanel.add(new JLabel("Port:"));
        portPanel.add(portField);
        container.add(portPanel);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addressField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(JoinNetworkGameFrame.this, "Address must be non empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    int port = Integer.parseInt(portField.getText());

                    Main.setConfig("join_network_game.address", addressField.getText());
                    Main.setConfig("join_network_game.port", "" + port);

                    setVisible(false);
                    Main.joinNetworkGame(addressField.getText(), port);

                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(JoinNetworkGameFrame.this, "Invalid port", "Error", JOptionPane.ERROR_MESSAGE);
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
