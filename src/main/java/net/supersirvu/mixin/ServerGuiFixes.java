/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu.mixin;

import com.mojang.logging.LogQueues;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.MinecraftServerGui;
import net.supersirvu.gui.EnhancedLogPanel;
import net.supersirvu.gui.EnhancedPlayerListGui;
import net.supersirvu.gui.EnhancedPlayerStatsGui;
import net.supersirvu.gui.EnhancedServerMenuBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public final class ServerGuiFixes {
    private static final Logger LOGGER = LoggerFactory.getLogger("DedicatedPower");

    private ServerGuiFixes() {
    }

    @Mixin(DedicatedServer.class)
    public static final class AlwaysShowGui {
        @Inject(method = "initServer", at = @At("TAIL"))
        private void dedicatedpower$showGui(CallbackInfoReturnable<Boolean> callback) {
            if (!Boolean.TRUE.equals(callback.getReturnValue())) {
                return;
            }
            try {
                ((DedicatedServer) (Object) this).showGui();
            } catch (Throwable throwable) {
                LOGGER.error("Failed to show the enhanced server GUI", throwable);
            }
        }
    }

    @Mixin(MinecraftServerGui.class)
    public static final class MinecraftServerGuiMixin {
        @Shadow
        @Final
        private DedicatedServer server;

        @Shadow
        private Thread logAppenderThread;

        @Inject(method = "buildInfoPanel", at = @At("HEAD"), cancellable = true)
        private void dedicatedpower$replaceInfoPanel(CallbackInfoReturnable<JComponent> callback) {
            EnhancedPlayerStatsGui stats = new EnhancedPlayerStatsGui(server);
            EnhancedPlayerListGui players = new EnhancedPlayerListGui(server);

            JPanel panel = new JPanel(new BorderLayout(6, 6));
            panel.add(stats, BorderLayout.NORTH);
            panel.add(new JScrollPane(players), BorderLayout.CENTER);
            callback.setReturnValue(panel);
        }

        @Inject(method = "buildChatPanel", at = @At("HEAD"), cancellable = true)
        private void dedicatedpower$replaceChatPanel(CallbackInfoReturnable<JComponent> callback) {
            EnhancedLogPanel logPanel = new EnhancedLogPanel(server);

            Thread monitor = new Thread(() -> {
                while (true) {
                    String message = LogQueues.getNextLogEvent("ServerGuiConsole");
                    if (message != null) {
                        logPanel.processLogMessage(message);
                    } else {
                        try {
                            Thread.sleep(50L);
                        } catch (InterruptedException interrupted) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            }, "Server log monitor");
            monitor.setDaemon(true);
            // The vanilla GUI calls start() on this field after the panels are built.
            // Keeping it populated prevents a NullPointerException that would leave the window blank.
            this.logAppenderThread = monitor;

            callback.setReturnValue(logPanel);
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
                try {
                    Window window = SwingUtilities.getWindowAncestor(gui);
                    if (window instanceof JFrame frame) {
                        frame.setJMenuBar(new EnhancedServerMenuBar(server, frame));
                        installCloseConfirmation(frame, server);
                        frame.revalidate();
                        frame.repaint();
                    }
                } catch (Throwable throwable) {
                    LOGGER.error("Failed to install the enhanced server menu bar", throwable);
                }
            });
        }

        /**
         * Replaces the vanilla close behaviour (which shuts down the server whenever
         * the window X button is pressed) with a confirmation dialog that lets the
         * user choose between closing only the GUI, shutting down the server as well,
         * or cancelling.
         */
        private static void installCloseConfirmation(JFrame frame, DedicatedServer server) {
            // Remove the vanilla window listeners, which halt the server unconditionally.
            for (WindowListener listener : frame.getWindowListeners()) {
                frame.removeWindowListener(listener);
            }
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    // If the server has already stopped, just close the window without asking.
                    if (!server.isRunning()) {
                        frame.dispose();
                        return;
                    }

                    String[] options = {
                            "Close GUI Only",
                            "Close GUI and Server",
                            "Cancel"
                    };
                    int choice = JOptionPane.showOptionDialog(
                            frame,
                            "Do you want to close only the GUI (the server keeps running) or also shut down the server?",
                            "Close Dedicated Server",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );
                    if (choice == 0) {
                        // Close only the GUI window; the server keeps running in the background.
                        frame.dispose();
                    } else if (choice == 1) {
                        // Gracefully stop the server, then close the window so the JVM can exit.
                        frame.setTitle("Minecraft server - shutting down!");
                        server.halt(true);
                        frame.dispose();
                    }
                    // Otherwise (Cancel or the dialog was dismissed) keep everything as it is.
                }
            });
        }
    }
}
