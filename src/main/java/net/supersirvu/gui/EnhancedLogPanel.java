/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu.gui;

import net.minecraft.server.dedicated.DedicatedServer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class EnhancedLogPanel extends JPanel {
    private final DedicatedServer server;
    private final JTextArea logArea;
    private final JTextField commandInput;
    private final List<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;

    public EnhancedLogPanel(DedicatedServer server) {
        this.server = server;
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        commandInput = new JTextField();
        commandInput.addActionListener(event -> executeCommand());
        commandInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_UP) {
                    navigateHistory(-1);
                    event.consume();
                } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
                    navigateHistory(1);
                    event.consume();
                }
            }
        });

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(event -> logArea.setText(""));

        JPanel commandBar = new JPanel(new BorderLayout(6, 0));
        commandBar.add(commandInput, BorderLayout.CENTER);
        commandBar.add(clearButton, BorderLayout.EAST);

        add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(commandBar, BorderLayout.SOUTH);
    }

    public void processLogMessage(String message) {
        appendLog(message, LogLevel.INFO);
    }

    public void appendLog(String message, LogLevel level) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + level + "] " + message + System.lineSeparator());
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void executeCommand() {
        String command = commandInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        commandHistory.add(command);
        historyIndex = commandHistory.size();
        appendLog("> " + command, LogLevel.COMMAND);
        server.handleConsoleInput(command, server.createCommandSourceStack());
        commandInput.setText("");
    }

    private void navigateHistory(int direction) {
        if (commandHistory.isEmpty()) {
            return;
        }
        historyIndex = Math.max(0, Math.min(commandHistory.size(), historyIndex + direction));
        commandInput.setText(historyIndex < commandHistory.size() ? commandHistory.get(historyIndex) : "");
    }

    public enum LogLevel {
        INFO, COMMAND, WARNING, ERROR
    }

    public enum ConsoleMode {
        SERVER_LOG("Server Log"),
        CHAT_ONLY("Chat Only");

        private final String displayName;

        ConsoleMode(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
