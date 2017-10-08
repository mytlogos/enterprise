package Configs;

/**
 *
 */
public class SettingsRun implements Runnable {
    private Action action;

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
//                SettingsManager.getInstance().saveSettings();
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
