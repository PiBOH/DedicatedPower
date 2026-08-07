/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Shared state used by the server GUI mixins without referencing the mixin package. */
public final class ServerGuiState {
    public static final Logger LOGGER = LoggerFactory.getLogger("DedicatedPower");

    /** True when the server was launched with --nogui; the GUI must not be forced. */
    public static volatile boolean noGuiRequested;

    private ServerGuiState() {
    }

    public static boolean isNoGuiRequested() {
        return noGuiRequested;
    }
}
