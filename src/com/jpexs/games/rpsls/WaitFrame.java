package com.jpexs.games.rpsls;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author JPEXS
 */
public class WaitFrame extends JFrame {

    public WaitFrame(String message, ActionListener onCancelAction) {
        setTitle("RPSLS wait...");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (onCancelAction != null) {
                    onCancelAction.actionPerformed(new ActionEvent(WaitFrame.this, 0, "CANCEL"));
                }
            }
        });
        Container container = getContentPane();

        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new GridBagLayout());
        centralPanel.add(new JLabel(message), new GridBagConstraints());

        container.setLayout(new BorderLayout());
        container.add(centralPanel, BorderLayout.CENTER);
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                if (onCancelAction != null) {
                    onCancelAction.actionPerformed(new ActionEvent(WaitFrame.this, 0, "CANCEL"));
                }
            }
        });
        buttonsPanel.add(cancelButton);
        container.add(buttonsPanel, BorderLayout.SOUTH);

        pack();
        setSize(300, 150);
        setResizable(false);
        Main.centerWindow(this);
    }
}
