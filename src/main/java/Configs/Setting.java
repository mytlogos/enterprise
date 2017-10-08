package Configs;

import Enterprise.modules.Module;

import java.util.Set;

/**
 *
 */
public interface Setting {
    void loadSetting(String key, String value);

    String getNodeName();

    boolean isModule();

    Module getModule();

    String getSetting(String key);

    Set<String> getKeys();

    String getDefault(String key);
}
