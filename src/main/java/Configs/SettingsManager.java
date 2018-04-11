package Configs;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * // TODO: 01.10.2017 does not load or save user changed settings, only default
 */
public class SettingsManager {
    private static final SettingsManager settings = new SettingsManager();

    private final Preferences projectPreferences = Preferences.userRoot().node("enterprise");
    private final List<SetAble> registered = new ArrayList<>();

    private SettingsManager() {
        if (settings != null) {
            throw new IllegalStateException();
        }
    }

    public static SettingsManager getInstance() {
        return settings;
    }

    public void register(SetAble setting) {
        registered.add(setting);
        loadSetting(setting);
    }

    private void loadSetting(SetAble setting) {
        Preferences preferences = getPreferences(setting);

        for (String s : setting.getKeys()) {
            String value = preferences.get(s, setting.getDefault(s));
            setting.loadSetting(s, value);
        }
    }

    private Preferences getPreferences(SetAble setting) {
        Preferences preferences = projectPreferences;

        if (setting.isModule()) {
            preferences = preferences.node(setting.getModule().toString());
        }

        String extraNode = setting.getNodeName();

        if (extraNode != null && !extraNode.isEmpty()) {
            preferences = preferences.node(extraNode);
        }
        return preferences;
    }

    public void saveSettings() {
        for (SetAble setting : registered) {
            Preferences preferences = getPreferences(setting);

            for (String s : setting.getKeys()) {
                preferences.put(s, setting.getSetting(s));
            }
        }
    }

}
