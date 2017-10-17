package Configs;

import Enterprise.misc.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * // TODO: 01.10.2017 does not load or save user changed settings, only default
 */
public class SettingsManager {
    private static SettingsManager settings = new SettingsManager();
    private Preferences projectPreferences = Preferences.userRoot().node("enterprise");
    private List<Setting> registered = new ArrayList<>();

    private SettingsManager() {
        if (settings != null) {
            throw new IllegalStateException();
        }
    }

    public static SettingsManager getInstance() {
        return settings;
    }

    public void register(Setting setting) {
        registered.add(setting);
        loadSetting(setting);
    }

    public void saveSettings() {
        for (Setting setting : registered) {
            Preferences preferences = getPreferences(setting);

            for (String s : setting.getKeys()) {
                preferences.put(s, setting.getSetting(s));
            }
        }
    }

    private void loadSetting(Setting setting) {
        Preferences preferences = getPreferences(setting);

        for (String s : setting.getKeys()) {
            String value = preferences.get(s, setting.getDefault(s));
            setting.loadSetting(s, value);
        }
    }

    private Preferences getPreferences(Setting setting) {
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

    private void save(Preferences preferences, String s, String value) {
    }

    public void saveContentControllerSettings() {
        contentControlSettings(this::save);
    }

    private void contentControlSettings(TriConsumer<Preferences, Setting, String> triConsumer) {
        /*for (BasicModules basicModules : BasicModules.values()) {

            Preferences moduleNode = preferences.node(basicModules.toString());

            ContentController controller = (ContentController) ControlComm.getInstance().getController(basicModules, BasicModes.CONTENT);
            List<Column<? extends CreationEntry,?>> set = controller.getColumnManager().getColumns();

            for (Setting o : set) {
                String columnNode = o.getNodeName();
                triConsumer.consume(moduleNode, o, columnNode);
            }
        }*/
    }

    private void load(Preferences moduleNode, Setting o, String columnNode) {
        for (String s : o.getKeys()) {
            String key = getKey(columnNode, s);
            String value = moduleNode.get(key, o.getDefault(key));
            o.loadSetting(s, value);
        }
    }

    private String getKey(String columnNode, String s) {
        return columnNode.concat(".").concat(s);
    }

    private void save(Preferences moduleNode, Setting o, String columnNode) {
        for (String s : o.getKeys()) {
            String key = getKey(columnNode, s);
            String value = o.getSetting(s);
            if (value != null) {
                save(moduleNode, key, value);
            } else {
//                System.out.println("null value of Key: " + key);
            }
        }
    }
}