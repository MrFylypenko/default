package com.todolist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

/**
 * Created by vano on 14.12.15.
 */
public class Test extends JFrame{

    public Test() {
        super();
        JButton closeButton = new JButton("Close");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setOpacity(0.80f);
        this.setSize(200, 200);
        // Center it on the screen
        this.setLocationRelativeTo(null);
        this.add(closeButton, BorderLayout.SOUTH);
        closeButton.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Test frame = new Test();
            frame.setVisible(true);
        });
    }

}
