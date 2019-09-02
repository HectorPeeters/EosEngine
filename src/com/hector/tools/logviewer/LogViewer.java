package com.hector.tools.logviewer;

import javax.swing.*;

public class LogViewer {

    public LogViewer() {
        LogViewerWindow window = new LogViewerWindow();

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);

        window.setResizable(false);
        window.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        new LogViewer();
    }

}
