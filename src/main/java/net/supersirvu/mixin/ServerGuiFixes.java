/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu.mixin;

import com.mojang.logging.LogQueues;
import net.minecraft.server.Main;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

public final class ServerGuiFixes {
    private static final Logger LOGGER = LoggerFactory.getLogger("DedicatedPower");

    /** True when the server was launched with --nogui; the GUI must not be forced. */
    private static volatile boolean noGuiRequested;

    static boolean isNoGuiRequested() {
        return noGuiRequested;
    }

    private ServerGuiFixes() {
    }

    @Mixin(Main.class)
    public static final class CaptureNoGui {
        @Inject(method = "main", at = @At("HEAD"))
        private static void dedicatedpower$captureNoGui(String[] args, CallbackInfo callback) {
            if (args == null) {
                return;
            }
            for (String arg : args) {
                // Accepts "nogui", "--nogui" and "-nogui", matching joptsimple usage.
                if (arg != null && arg.replace("-", "").equalsIgnoreCase("nogui")) {
                    noGuiRequested = true;
                    return;
                }
            }
        }
    }

    @Mixin(DedicatedServer.class)
    public static final class AlwaysShowGui {
        @Inject(method = "initServer", at = @At("TAIL"))
        private void dedicatedpower$showGui(CallbackInfoReturnable<Boolean> callback) {
            if (!Boolean.TRUE.equals(callback.getReturnValue())) {
                return;
            }
            // Respect the --nogui launch option and headless environments, exactly like
            // vanilla does in Main.main, so the enhanced GUI is never forced open.
            if (isNoGuiRequested() || GraphicsEnvironment.isHeadless()) {
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
                        try {
                            frame.setJMenuBar(new EnhancedServerMenuBar(server, frame));
                            EnhancedServerMenuBar.installCloseConfirmation(frame, server);
                        } finally {
                            // Always re-layout and repaint so the menu bar is visible even
                            // if the menu or close-confirmation setup fails for any reason.
                            frame.revalidate();
                            frame.repaint();
                        }
                    }
                } catch (Throwable throwable) {
                    LOGGER.error("Failed to install the enhanced server menu bar", throwable);
                }
            });
        }
    }
}
