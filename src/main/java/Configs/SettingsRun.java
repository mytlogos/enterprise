package Configs;

/**
 *
 */
public class SettingsRun implements Runnable {
    private final Action action;

    public SettingsRun(Action action) {
        this.action = action;
    }

    @Override
    public void run() {
        action.doAction();
    }

    public enum Action {
        LOAD {
            @Override
            void doAction() {
//                SettingsManager.get().saveSettings();
            }
        },
        SAVE {
            @Override
            void doAction() {
                SettingsManager.getInstance().saveSettings();
            }
        };

        abstract void doAction();
    }
}
