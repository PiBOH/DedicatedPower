/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu.gui;

import net.minecraft.server.MinecraftServer;

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Locale;

public class EnhancedPlayerStatsGui extends JComponent {
    private final MinecraftServer server;
    private final Timer timer;
    private String status = "Collecting server statistics...";

    public EnhancedPlayerStatsGui(MinecraftServer server) {
        this.server = server;
        setPreferredSize(new Dimension(456, 90));
        timer = new Timer(500, event -> updateStats());
        timer.start();
        updateStats();
    }

    private void updateStats() {
        double tickMs = server.getCurrentSmoothedTickTime() / 1_000_000.0;
        double tps = tickMs <= 0.0 ? 20.0 : Math.min(20.0, 1000.0 / tickMs);
        status = String.format(Locale.ROOT,
                "TPS: %.2f / 20.0    Average tick: %.2f ms    Players: %d    Levels: %d",
                tps,
                tickMs,
                server.getPlayerCount(),
                server.levelKeys().size());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            g.drawString(status, 10, 30);
            g.setColor(new Color(52, 152, 219));
            g.fillRect(10, 48, Math.min(getWidth() - 20, Math.max(0, server.getPlayerCount() * 12)), 12);
        } finally {
            g.dispose();
        }
    }

    public void stop() {
        timer.stop();
    }
}
