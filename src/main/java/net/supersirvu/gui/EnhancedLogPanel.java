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
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EnhancedLogPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(24, 26, 32);
    private static final Color INFO_COLOR = new Color(214, 218, 224);
    private static final Color COMMAND_COLOR = new Color(88, 190, 255);
    private static final Color WARNING_COLOR = new Color(255, 180, 71);
    private static final Color ERROR_COLOR = new Color(255, 99, 99);

    private final DedicatedServer server;
    private final JTextPane logPane;
    private final StyledDocument logDocument;
    private final JTextField commandInput;
    private final List<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;

    public EnhancedLogPanel(DedicatedServer server) {
        this.server = server;
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logPane.setBackground(BACKGROUND_COLOR);
        logDocument = logPane.getStyledDocument();
        createStyles();

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
        clearButton.addActionListener(event -> logPane.setText(""));

        JPanel commandBar = new JPanel(new BorderLayout(6, 0));
        commandBar.add(commandInput, BorderLayout.CENTER);
        commandBar.add(clearButton, BorderLayout.EAST);

        add(new JScrollPane(logPane), BorderLayout.CENTER);
        add(commandBar, BorderLayout.SOUTH);
    }

    private void createStyles() {
        Style base = logPane.getStyle(javax.swing.text.StyleContext.DEFAULT_STYLE);
        Style info = logDocument.addStyle("info", base);
        StyleConstants.setForeground(info, INFO_COLOR);
        Style command = logDocument.addStyle("command", base);
        StyleConstants.setForeground(command, COMMAND_COLOR);
        StyleConstants.setBold(command, true);
        Style warning = logDocument.addStyle("warning", base);
        StyleConstants.setForeground(warning, WARNING_COLOR);
        Style error = logDocument.addStyle("error", base);
        StyleConstants.setForeground(error, ERROR_COLOR);
    }

    /** Forwards a raw vanilla log line into the console panel. */
    public void processLogMessage(String message) {
        appendLog(message, levelOf(message));
    }

    public void appendLog(String message, LogLevel level) {
        SwingUtilities.invokeLater(() -> {
            try {
                String line = message + System.lineSeparator();
                logDocument.insertString(logDocument.getLength(), line, styleFor(level));
                logPane.setCaretPosition(logDocument.getLength());
            } catch (BadLocationException ignored) {
                // The document is always valid here; nothing to recover.
            }
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

    private static LogLevel levelOf(String message) {
        String upper = message.toUpperCase(Locale.ROOT);
        if (upper.contains("ERROR]") || upper.contains("FATAL")) {
            return LogLevel.ERROR;
        }
        if (upper.contains("WARN]")) {
            return LogLevel.WARNING;
        }
        return LogLevel.INFO;
    }

    private Style styleFor(LogLevel level) {
        return logDocument.getStyle(level.styleName);
    }

    public enum LogLevel {
        INFO("info"),
        COMMAND("command"),
        WARNING("warning"),
        ERROR("error");

        private final String styleName;

        LogLevel(String styleName) {
            this.styleName = styleName;
        }
    }
}
