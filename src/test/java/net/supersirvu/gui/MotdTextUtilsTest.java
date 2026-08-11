/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MotdTextUtilsTest {
    @Test
    void preservesLiteralSectionSignFormatting() {
        assertEquals("\u00A73>>> \u00A76\u00A7lJarock\u00A7r", 
                MotdTextUtils.normalize("\u00A73>>> \u00A76\u00A7lJarock\u00A7r"));
    }

    @Test
    void decodesBackslashAndSlashUnicodeSectionSignEscapes() {
        assertEquals("\u00A73Green \u00A7ltext", MotdTextUtils.normalize("\\u00A73Green \\u00a7ltext"));
        assertEquals("\u00A73Green \u00A7ltext", MotdTextUtils.normalize("/u00A73Green /u00a7ltext"));
    }

    @Test
    void decodesNewlineEscapesWithoutChangingOrdinaryUrlText() {
        assertEquals("First\nSecond", MotdTextUtils.normalize("First\\nSecond"));
        assertEquals("First/nSecond", MotdTextUtils.normalize("First/nSecond"));
        assertEquals("https://example.test/nVisit", MotdTextUtils.normalize("https://example.test/nVisit"));
        assertEquals("\u00A7r\nVisit", MotdTextUtils.normalize("\u00A7r/nVisit"));
    }

    @Test
    void normalizesTheCompleteExampleMotd() {
        String input = "/u00A73>>> /u00A76/u00A7lJa/u00A79/u00A7lrock/u00A7r Minecraft 26.2 Server /u00A7r/n"
                + "Visit /u00A73piboh.github.io/jarock/u00A7r for more informations.";
        String expected = "\u00A73>>> \u00A76\u00A7lJa\u00A79\u00A7lrock\u00A7r Minecraft 26.2 Server \u00A7r\n"
                + "Visit \u00A73piboh.github.io/jarock\u00A7r for more informations.";

        assertEquals(expected, MotdTextUtils.normalize(input));
    }

    @Test
    void doesNotDecodeSlashNewlineInAnInsertedMiddleFragmentUnlessAfterReset() {
        assertEquals("/nSuffix", MotdTextUtils.normalize("/nSuffix", false));
        assertEquals("§r\nSuffix", MotdTextUtils.normalize("§r/nSuffix", false));
    }

    @Test
    void normalizesWindowsAndUnixLineEndings() {
        assertEquals("First\nSecond", MotdTextUtils.normalize("First\r\nSecond"));
        assertEquals("First\nSecond", MotdTextUtils.normalize("First\rSecond"));
    }

    @Test
    void handlesLineLimitBoundaries() {
        assertEquals("single line", MotdTextUtils.limitLines("single line", 2));
        assertEquals("first", MotdTextUtils.limitLines("first\nsecond", 1));
        assertEquals("first", MotdTextUtils.limitLines("first\nsecond", 0));
    }

    @Test
    void limitsMotdToTwoLinesWithoutLimitingLineLength() {
        String longFirstLine = "A".repeat(500);
        String motd = longFirstLine + "\nSecond line\nThird line";

        assertEquals(longFirstLine + "\nSecond line", MotdTextUtils.limitLines(motd, 2));
    }
}
