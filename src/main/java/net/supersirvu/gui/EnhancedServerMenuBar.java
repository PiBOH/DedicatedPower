/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu.gui;

import net.minecraft.server.dedicated.DedicatedServer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.Frame;

public class EnhancedServerMenuBar extends JMenuBar {
    private final DedicatedServer server;
    private final Frame parentFrame;

    public EnhancedServerMenuBar(DedicatedServer server, Frame parentFrame) {
        this.server = server;
        this.parentFrame = parentFrame;
        createMenus();
    }

    private void createMenus() {
        JMenu serverMenu = new JMenu("Server");
        JMenuItem saveItem = new JMenuItem("Save all worlds");
        saveItem.addActionListener(event -> execute("save-all flush"));
        JMenuItem reloadItem = new JMenuItem("Reload datapacks");
        reloadItem.addActionListener(event -> execute("reload"));
        JMenuItem stopItem = new JMenuItem("Stop server");
        stopItem.addActionListener(event -> execute("stop"));
        serverMenu.add(saveItem);
        serverMenu.add(reloadItem);
        serverMenu.addSeparator();
        serverMenu.add(stopItem);

        JMenu worldMenu = new JMenu("World");
        JMenuItem dayItem = new JMenuItem("Set day");
        dayItem.addActionListener(event -> execute("time set day"));
        JMenuItem nightItem = new JMenuItem("Set night");
        nightItem.addActionListener(event -> execute("time set night"));
        JMenuItem clearWeatherItem = new JMenuItem("Clear weather");
        clearWeatherItem.addActionListener(event -> execute("weather clear"));
        worldMenu.add(dayItem);
        worldMenu.add(nightItem);
        worldMenu.add(clearWeatherItem);

        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem commandItem = new JMenuItem("Run command...");
        commandItem.addActionListener(event -> {
            String command = JOptionPane.showInputDialog(parentFrame, "Command:");
            if (command != null && !command.isBlank()) {
                execute(command);
            }
        });
        toolsMenu.add(commandItem);

        add(serverMenu);
        add(worldMenu);
        add(toolsMenu);
    }

    private void execute(String command) {
        server.handleConsoleInput(command, server.createCommandSourceStack());
    }
}
