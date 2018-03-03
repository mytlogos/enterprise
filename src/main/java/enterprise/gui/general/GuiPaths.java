package enterprise.gui.general;

import enterprise.data.intface.CreationEntry;
import enterprise.modules.Module;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Utility Class for providing the several Paths to the fxml Files.
 */
public class GuiPaths {

    private static String fxmlDir = "/fxml/";

    /**
     * Constructs the Path to the fxml file for the
     * specified {@code Module} and {@code Mode}.
     *
     * @param module {@code Module} of the file
     * @param mode   {@code Mode} of the file
     * @return path - a relative path to the current working environment
     */
    public static String getPath(Module module, Mode mode) {
        String filePath = fxmlDir + mode.toString().toLowerCase();

        if (mode != BasicMode.CONTENT && module.isSourceable()) {
            filePath = filePath + "Sourceable";
        }

        return filePath + ".fxml";
    }

    public static String getMainPath() {
        String extension = ".fxml";
        String path;

        Main module = Main.ENTERPRISE;

        String moduleString = module.toString().toLowerCase();
        path = fxmlDir
                .concat(moduleString)
                .concat(extension);

        return path;
    }

    public enum Main implements Module {
        ENTERPRISE() {
            @Override
            public boolean deleteEntry(CreationEntry entry) {
                throw new IllegalAccessError();
            }

            @Override
            public ObservableList<? extends CreationEntry> getEntries() {
                throw new IllegalAccessError();
            }

            @Override
            public boolean addEntry(CreationEntry entry) {
                throw new IllegalAccessError();
            }

            @Override
            public List<String> getListNames() {
                throw new IllegalAccessError();
            }

            @Override
            public boolean isSourceable() {
                throw new IllegalAccessError();
            }
        };

        final String name;

        Main() {
            name = "enterprise";
        }

        public String getName() {
            return name;
        }

        @Override
        public String tabName() {
            return "";
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
}
