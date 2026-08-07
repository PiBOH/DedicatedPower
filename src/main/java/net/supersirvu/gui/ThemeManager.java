/*
 * Copyright (c) 2026 SuperSirvu
 *
 * Licensed under the MIT License.
 */

package net.supersirvu.gui;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/** Centralized appearance settings for the DedicatedPower Swing interface. */
public final class ThemeManager {
    public enum Theme {
        LIGHT,
        DARK
    }

    public interface ThemeListener {
        void themeChanged(ThemeManager themeManager);
    }

    private static final Path SETTINGS_FILE = Path.of("config", "dedicatedpower-gui.properties");
    private static final EnumMap<EnhancedLogPanel.LogLevel, Color> DEFAULT_LOG_COLORS = createDefaultLogColors();
    private static final ThemeManager INSTANCE = new ThemeManager();

    private final CopyOnWriteArrayList<WeakReference<ThemeListener>> listeners = new CopyOnWriteArrayList<>();
    private final EnumMap<EnhancedLogPanel.LogLevel, Color> logColors = new EnumMap<>(EnhancedLogPanel.LogLevel.class);
    private Theme theme = Theme.LIGHT;

    private ThemeManager() {
        load();
        applySwingDefaults();
    }

    public static ThemeManager getInstance() {
        return INSTANCE;
    }

    public Theme getTheme() {
        return theme;
    }

    public boolean isDark() {
        return theme == Theme.DARK;
    }

    public Color getLogColor(EnhancedLogPanel.LogLevel level) {
        return logColors.get(level);
    }

    public Color getPanelBackground() {
        return isDark() ? new Color(43, 43, 43) : new Color(240, 240, 240);
    }

    public Color getSurfaceBackground() {
        return isDark() ? new Color(50, 50, 50) : new Color(250, 250, 250);
    }

    public Color getInputBackground() {
        return isDark() ? new Color(60, 60, 60) : Color.WHITE;
    }

    public Color getGraphBackground() {
        return isDark() ? new Color(50, 50, 50) : Color.WHITE;
    }

    public Color getForeground() {
        return isDark() ? new Color(235, 235, 235) : Color.BLACK;
    }

    public Color getMutedForeground() {
        return isDark() ? new Color(180, 180, 180) : Color.GRAY;
    }

    public Color getBorderColor() {
        return isDark() ? new Color(105, 105, 105) : new Color(150, 150, 150);
    }

    public Color getGridColor() {
        return isDark() ? new Color(80, 80, 80) : new Color(220, 220, 220);
    }

    public Color getSelectionBackground() {
        return isDark() ? new Color(70, 95, 125) : new Color(184, 207, 229);
    }

    public void addListener(ThemeListener listener) {
        listeners.add(new WeakReference<>(listener));
    }

    public void removeListener(ThemeListener listener) {
        listeners.removeIf(reference -> {
            ThemeListener current = reference.get();
            return current == null || current == listener;
        });
    }

    public void applySettings(Theme newTheme, Map<EnhancedLogPanel.LogLevel, Color> newLogColors) {
        theme = newTheme;
        logColors.clear();
        logColors.putAll(newLogColors);
        save();
        applySwingDefaults();
        notifyListeners();
    }

    public void applyTo(Window window) {
        applySwingDefaults();
        if (window != null) {
            SwingUtilities.updateComponentTreeUI(window);
            applyComponentTheme(window);
            window.repaint();
        }
        for (Window openWindow : Window.getWindows()) {
            if (openWindow != window && openWindow.isDisplayable()) {
                SwingUtilities.updateComponentTreeUI(openWindow);
                applyComponentTheme(openWindow);
                openWindow.repaint();
            }
        }
    }

    private void applySwingDefaults() {
        Color panel = getPanelBackground();
        Color surface = getSurfaceBackground();
        Color input = getInputBackground();
        Color foreground = getForeground();
        Color border = getBorderColor();

        UIManager.put("Panel.background", panel);
        UIManager.put("Panel.foreground", foreground);
        UIManager.put("Label.foreground", foreground);
        UIManager.put("TextField.background", input);
        UIManager.put("TextField.foreground", foreground);
        UIManager.put("TextArea.background", input);
        UIManager.put("TextArea.foreground", foreground);
        UIManager.put("TextPane.background", input);
        UIManager.put("TextPane.foreground", foreground);
        UIManager.put("List.background", input);
        UIManager.put("List.foreground", foreground);
        UIManager.put("Table.background", input);
        UIManager.put("Table.foreground", foreground);
        UIManager.put("Viewport.background", input);
        UIManager.put("MenuBar.background", surface);
        UIManager.put("MenuBar.foreground", foreground);
        UIManager.put("Menu.background", surface);
        UIManager.put("Menu.foreground", foreground);
        UIManager.put("MenuItem.background", surface);
        UIManager.put("MenuItem.foreground", foreground);
        UIManager.put("Button.background", surface);
        UIManager.put("Button.foreground", foreground);
        UIManager.put("Button.select", isDark() ? new Color(75, 95, 120) : new Color(210, 225, 245));
        UIManager.put("Button.focus", border);
        UIManager.put("CheckBox.background", panel);
        UIManager.put("CheckBox.foreground", foreground);
        UIManager.put("RadioButton.background", panel);
        UIManager.put("RadioButton.foreground", foreground);
        UIManager.put("Spinner.background", input);
        UIManager.put("Spinner.foreground", foreground);
        UIManager.put("ComboBox.background", input);
        UIManager.put("ComboBox.foreground", foreground);
        UIManager.put("List.selectionBackground", getSelectionBackground());
        UIManager.put("List.selectionForeground", foreground);
        UIManager.put("Table.selectionBackground", getSelectionBackground());
        UIManager.put("Table.selectionForeground", foreground);
        UIManager.put("ScrollBar.background", surface);
        UIManager.put("ScrollBar.foreground", foreground);
        UIManager.put("OptionPane.background", panel);
        UIManager.put("OptionPane.messageForeground", foreground);
        UIManager.put("Separator.foreground", border);
    }

    private void notifyListeners() {
        for (WeakReference<ThemeListener> reference : listeners) {
            ThemeListener listener = reference.get();
            if (listener == null) {
                listeners.remove(reference);
            } else {
                listener.themeChanged(this);
            }
        }
    }

    private void load() {
        setDefaultLogColors();
        if (!Files.isRegularFile(SETTINGS_FILE)) {
            return;
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(SETTINGS_FILE)) {
            properties.load(input);
            theme = Theme.valueOf(properties.getProperty("theme", Theme.LIGHT.name()).toUpperCase());
            for (EnhancedLogPanel.LogLevel level : EnhancedLogPanel.LogLevel.values()) {
                String value = properties.getProperty("log." + level.name().toLowerCase());
                if (value != null) {
                    Color parsed = Color.decode(value);
                    logColors.put(level, parsed);
                }
            }
        } catch (Exception ignored) {
            theme = Theme.LIGHT;
            setDefaultLogColors();
        }
    }

    public static Map<EnhancedLogPanel.LogLevel, Color> defaultLogColors() {
        return new EnumMap<>(DEFAULT_LOG_COLORS);
    }

    private static EnumMap<EnhancedLogPanel.LogLevel, Color> createDefaultLogColors() {
        EnumMap<EnhancedLogPanel.LogLevel, Color> colors = new EnumMap<>(EnhancedLogPanel.LogLevel.class);
        colors.put(EnhancedLogPanel.LogLevel.INFO, new Color(52, 152, 219));
        colors.put(EnhancedLogPanel.LogLevel.WARN, new Color(243, 156, 18));
        colors.put(EnhancedLogPanel.LogLevel.ERROR, new Color(231, 76, 60));
        colors.put(EnhancedLogPanel.LogLevel.DEBUG, new Color(149, 165, 166));
        colors.put(EnhancedLogPanel.LogLevel.CHAT, new Color(46, 204, 113));
        return colors;
    }

    private void setDefaultLogColors() {
        logColors.clear();
        logColors.putAll(DEFAULT_LOG_COLORS);
    }

    private void save() {
        Properties properties = new Properties();
        properties.setProperty("theme", theme.name().toLowerCase());
        for (Map.Entry<EnhancedLogPanel.LogLevel, Color> entry : logColors.entrySet()) {
            properties.setProperty("log." + entry.getKey().name().toLowerCase(),
                    String.format("#%02x%02x%02x", entry.getValue().getRed(), entry.getValue().getGreen(), entry.getValue().getBlue()));
        }

        try {
            Files.createDirectories(SETTINGS_FILE.getParent());
            try (OutputStream output = Files.newOutputStream(SETTINGS_FILE,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                properties.store(output, "DedicatedPower GUI appearance settings");
            }
        } catch (IOException ignored) {
            // Appearance changes remain active for this session even if persistence fails.
        }
    }

    /**
     * Applies explicit colors to already-created Swing controls. UIManager values
     * only affect newly-created controls for some Look & Feels, so this pass is
     * required when the user switches theme while the GUI is open.
     */
    public void applyComponentTheme(Component component) {
        Color panel = getPanelBackground();
        Color surface = getSurfaceBackground();
        Color input = getInputBackground();
        Color foreground = getForeground();
        Color border = getBorderColor();

        if (component instanceof AbstractButton button) {
            if (!Boolean.TRUE.equals(button.getClientProperty("dedicatedpower.palettePreview"))) {
                button.setBackground(surface);
            }
            button.setForeground(foreground);
        } else if (component instanceof JSpinner spinner) {
            spinner.setBackground(input);
            spinner.setForeground(foreground);
            setBackgroundRecursively(spinner, input, foreground);
        } else if (component instanceof JTextField || component instanceof JTextArea || component instanceof JList<?> || component instanceof JTable) {
            component.setBackground(input);
            component.setForeground(foreground);
        } else if (component instanceof JScrollBar scrollBar) {
            scrollBar.setBackground(surface);
            scrollBar.setForeground(foreground);
        } else if (component instanceof JPopupMenu) {
            component.setBackground(surface);
            component.setForeground(foreground);
        } else if (component instanceof JComponent swingComponent) {
            swingComponent.setBackground(panel);
            swingComponent.setForeground(foreground);
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyComponentTheme(child);
            }
        }
    }

    public static void setBackgroundRecursively(Component component, Color background, Color foreground) {
        if (component instanceof JComponent swingComponent) {
            swingComponent.setBackground(background);
            swingComponent.setForeground(foreground);
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                setBackgroundRecursively(child, background, foreground);
            }
        }
    }
}
