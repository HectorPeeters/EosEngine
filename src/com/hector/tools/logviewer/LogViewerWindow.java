package com.hector.tools.logviewer;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogViewerWindow extends JFrame {

    public LogViewerWindow() {
        setTitle("Log Viewer");

        JTabbedPane pane = new JTabbedPane();

        for (File f : new File("logs/").listFiles()) {
            if (f.getName().endsWith(".log")) {
                if (f.length() == 0)
                    continue;

                String name = f.getName().replace(".log", "");
                pane.add(getLogPane(f), name);
            }
        }

        add(pane);
        pack();
    }

    private JScrollPane getLogPane(File logFile) {
        setPreferredSize(new Dimension(900, 600));

        String[] columnNames = {"Time", "Level", "Channel", "Message"};

        String[][] data = loadLogFile(logFile);

        JTable table = new JTable(data, columnNames);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setMaxWidth(50);
        table.getColumnModel().getColumn(2).setMaxWidth(70);

        return new JScrollPane(table);
    }

    private String[][] loadLogFile(File file) {
        List<String[]> result = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            final Pattern pattern = Pattern.compile("\\[(.*?)]");

            while ((line = br.readLine()) != null) {
                String time = line.split(": ")[0];
                String level = "-1";
                String channel = "-1";

                Matcher m = pattern.matcher(line);

                int i = 0;
                while (m.find()) {
                    if (i == 0)
                        level = m.group();
                    else if (i == 1)
                        channel = m.group();
                    else
                        break;

                    i++;
                }

                String message = line.replace(time + ": ", "");
                message = message.replace(level + " ", "");
                message = message.replace(channel + " ", "");

                result.add(new String[]{time, level, channel, message});
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toArray(new String[result.size()][]);
    }

}
