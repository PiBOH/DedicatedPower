/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.level.GameType;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Comparator;
import java.util.Locale;

public class EnhancedPlayerListGui extends JPanel {
    private final MinecraftServer server;
    private final DefaultListModel<PlayerRow> model = new DefaultListModel<>();
    private final JList<PlayerRow> playerList = new JList<>(model);
    private final JTextField filter = new JTextField();

    public EnhancedPlayerListGui(MinecraftServer server) {
        this.server = server;
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        filter.setToolTipText("Filter players");
        filter.addActionListener(event -> refresh());

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(event -> refresh());

        JPanel header = new JPanel(new BorderLayout(6, 0));
        header.add(filter, BorderLayout.CENTER);
        header.add(refresh, BorderLayout.EAST);

        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerList.setFixedCellHeight(32);
        playerList.setCellRenderer((list, row, index, selected, focus) -> {
            String text = row.name + "  |  " + row.gameType.getName();
            if (selected) {
                text = "▶ " + text;
            }
            return new javax.swing.JLabel(text);
        });

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(playerList), BorderLayout.CENTER);
        refresh();
    }

    public void tick() {
        refresh();
    }

    private void refresh() {
        String query = filter.getText().trim().toLowerCase(Locale.ROOT);
        model.clear();
        server.getPlayerList().getPlayers().stream()
                .map(PlayerRow::new)
                .filter(row -> query.isEmpty() || row.name.toLowerCase(Locale.ROOT).contains(query))
                .sorted(Comparator.comparing(row -> row.name.toLowerCase(Locale.ROOT)))
                .forEach(model::addElement);
    }

    private static final class PlayerRow {
        private final String name;
        private final GameType gameType;

        private PlayerRow(ServerPlayer player) {
            this.name = player.getGameProfile().name();
            this.gameType = player.gameMode();
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
