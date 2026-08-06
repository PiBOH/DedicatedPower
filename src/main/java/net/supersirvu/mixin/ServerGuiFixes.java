/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu.mixin;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.MinecraftServerGui;
import net.supersirvu.gui.EnhancedLogPanel;
import net.supersirvu.gui.EnhancedPlayerListGui;
import net.supersirvu.gui.EnhancedPlayerStatsGui;
import net.supersirvu.gui.EnhancedServerMenuBar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Window;

public final class ServerGuiFixes {
    private ServerGuiFixes() {
    }

    @Mixin(DedicatedServer.class)
    public static final class AlwaysShowGui {
        @Inject(method = "initServer", at = @At("TAIL"))
        private void dedicatedpower$showGui(CallbackInfoReturnable<Boolean> callback) {
            if (Boolean.TRUE.equals(callback.getReturnValue())) {
                ((DedicatedServer) (Object) this).showGui();
            }
        }
    }

    @Mixin(MinecraftServerGui.class)
    public static final class MinecraftServerGuiMixin {
        @Shadow
        @Final
        private DedicatedServer server;

        @Inject(method = "buildInfoPanel", at = @At("HEAD"), cancellable = true)
        private void dedicatedpower$replaceInfoPanel(CallbackInfoReturnable<javax.swing.JComponent> callback) {
            EnhancedPlayerStatsGui stats = new EnhancedPlayerStatsGui(server);
            EnhancedPlayerListGui players = new EnhancedPlayerListGui(server);

            JPanel panel = new JPanel(new BorderLayout(6, 6));
            panel.add(stats, BorderLayout.NORTH);
            panel.add(new JScrollPane(players), BorderLayout.CENTER);
            callback.setReturnValue(panel);
        }

        @Inject(method = "buildChatPanel", at = @At("HEAD"), cancellable = true)
        private void dedicatedpower$replaceChatPanel(CallbackInfoReturnable<javax.swing.JComponent> callback) {
            callback.setReturnValue(new EnhancedLogPanel(server));
        }

        @Inject(method = "showFrameFor", at = @At("TAIL"))
        private static void dedicatedpower$installMenu(
                DedicatedServer server,
                CallbackInfoReturnable<MinecraftServerGui> callback
        ) {
            MinecraftServerGui gui = callback.getReturnValue();
            if (gui == null) {
                return;
            }

            SwingUtilities.invokeLater(() -> {
                Window window = SwingUtilities.getWindowAncestor(gui);
                if (window instanceof JFrame frame) {
                    frame.setJMenuBar(new EnhancedServerMenuBar(server, frame));
                    frame.revalidate();
                    frame.repaint();
                }
            });
        }
    }
}
