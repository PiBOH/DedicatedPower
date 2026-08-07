/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

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
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			// Queue the banner after all SERVER_STARTED callbacks have run. This keeps
			// it after optional integrations such as Geyser finish their startup work.
			server.execute(() -> LOGGER.info("\n{}\nThe DedicatedPower server has finished loading.\n", READY_BANNER));
		});
	}
}