/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu.gui;

/** Text helpers shared by the MOTD editor and its automated tests. */
public final class MotdTextUtils {
    private static final char SECTION_SIGN = (char) 0xA7;
    private static final char NEWLINE = (char) 10;

    private MotdTextUtils() {
    }

    /**
     * Converts escaped MOTD notation into Minecraft's canonical representation.
     * Literal section signs and ordinary text are preserved.
     */
    public static String normalize(String text) {
        return normalize(text, true);
    }

    /**
     * Normalizes an inserted fragment. Slash-prefixed {@code /n} is only treated
     * as a newline at the beginning when explicitly allowed, or after {@code §r}.
     * This prevents URLs and ordinary text containing {@code /n} from changing.
     */
    static String normalize(String text, boolean allowSlashNewlineAtStart) {
        if (text == null || text.isEmpty()) {
            return text == null ? "" : text;
        }

        StringBuilder normalized = new StringBuilder(text.length());
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if ((character == 92 || character == '/') && index + 1 < text.length()) {
                if (text.regionMatches(true, index + 1, "u00a7", 0, 5)) {
                    normalized.append(SECTION_SIGN);
                    index += 5;
                    continue;
                }
                boolean slashNewline = character == '/'
                        && ((index == 0 && allowSlashNewlineAtStart)
                        || (normalized.length() >= 2
                        && normalized.charAt(normalized.length() - 2) == SECTION_SIGN));
                if (text.charAt(index + 1) == 'n' && (character == 92 || slashNewline)) {
                    normalized.append(NEWLINE);
                    index++;
                    continue;
                }
            }
            if (character == 13) {
                if (index + 1 < text.length() && text.charAt(index + 1) == NEWLINE) {
                    index++;
                }
                normalized.append(NEWLINE);
            } else {
                normalized.append(character);
            }
        }
        return normalized.toString();
    }

    /** Returns at most {@code maxLines} newline-delimited MOTD lines. */
    public static String limitLines(String text, int maxLines) {
        if (maxLines <= 0) {
            int firstNewline = text.indexOf(NEWLINE);
            return firstNewline < 0 ? text : text.substring(0, firstNewline);
        }
        int newlineCount = 0;
        for (int index = 0; index < text.length(); index++) {
            if (text.charAt(index) == NEWLINE && ++newlineCount == maxLines) {
                return text.substring(0, index);
            }
        }
        return text;
    }
}
