/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;

import java.awt.GraphicsEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DedicatedPower implements ModInitializer {
	public static final String MOD_ID = "dedicatedpower";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/*
	 * Inspired by Jarock's server-ready-banner.txt:
	 * https://github.com/PiBOH/jarock
	 */
	private static final String READY_BANNER = "                           ▄▄\n"
			+ "████▄   ▄▄▄  ▄▄  ▄▄ ▄▄▄▄▄  ██\n"
			+ "██  ██ ██▀██ ███▄██ ██▄▄   ██\n"
			+ "████▀  ▀███▀ ██ ▀██ ██▄▄▄  ▄▄";

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				registerCommands(dispatcher));

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			// Queue the banner after all SERVER_STARTED callbacks have run. This keeps
			// it after optional integrations such as Geyser finish their startup work.
			server.execute(() -> LOGGER.info("\n{}\nThe DedicatedPower server has finished loading.\n", READY_BANNER));
		});
	}

	private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(net.minecraft.commands.Commands.literal("opengui")
				.requires(net.minecraft.commands.Commands.hasPermission(net.minecraft.commands.Commands.LEVEL_GAMEMASTERS))
				.executes(context -> {
					if (!(context.getSource().getServer() instanceof DedicatedServer server)) {
						context.getSource().sendFailure(Component.literal("DedicatedPower GUI is available only on a dedicated server."));
						return 0;
					}
					if (GraphicsEnvironment.isHeadless()) {
						context.getSource().sendFailure(Component.literal("DedicatedPower GUI cannot open because this server is running in a headless environment."));
						return 0;
					}

					// --nogui suppresses automatic startup only. DedicatedServer.showGui()
					// is the explicit opt-in path used by this command and is idempotent.
					try {
						// Vanilla invokes showGui from the server lifecycle thread. Keep the
						// same ownership model when opening explicitly after --nogui.
						server.showGui();
					} catch (Throwable throwable) {
						LOGGER.error("Failed to open the DedicatedPower GUI from /opengui", throwable);
					}
					context.getSource().sendSuccess(() -> Component.literal("Opening the DedicatedPower GUI..."), false);
					return 1;
				}));
	}
}