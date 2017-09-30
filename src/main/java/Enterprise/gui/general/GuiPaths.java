package Enterprise.gui.general;

import Enterprise.data.intface.CreationEntry;
import Enterprise.modules.Module;

import java.util.List;

/**
 * Utility Class for providing the several Paths to the fxml Files.
 */
public class GuiPaths {

    private static String constructFXMLPath(Module module) {
        String guiPath = "/Enterprise/target/classes/Enterprise/gui/";
        return guiPath.concat(module.toString()).concat("/fxml/");
    }

    @Deprecated
    private static String firstToCap(String string) {
        String firstChar;
        String remnantChars;
        String remnant = string.trim();
        String newString = "";
        int start = 0;
        int index;
        do {
            index = remnant.indexOf(" ");
            firstChar = remnant.substring(start, start + 1).toUpperCase();

            remnantChars = remnant.substring(start + 1, index);
            remnant = remnant.substring(index).trim();


            newString = newString.concat(" ").concat(firstChar.concat(remnantChars));
        } while (index != -1);

        return newString;
    }

    /**
     * Capitalizes the first letter of the given String
     *
     * @param string {@code String} to modify
     * @return string with capitalized first letter
     */
    private static String capFirst(String string) {
        return string.substring(0, 1).toUpperCase().concat(string.substring(1));
    }

    /**
     * Constructs the Path to the fxml file for the
     * specified {@code Module} and {@code Mode}.
     *
     * @param module {@code Module} of the file
     * @param mode   {@code Mode} of the file
     * @return path - a relative path to the current working environment
     */
    public static String getPath(Module module, Mode mode) {
        String directory = getFxmlDirectory();
        String filePath = directory + mode.toString().toLowerCase();
        if (mode != BasicModes.CONTENT && module.isSourceable()) {
            filePath = filePath + "Sourceable";
        }
        return filePath + ".fxml";
    }

    private static String getFxmlDirectory() {
        return "fxml/";
    }
    /*public static String getPath(Module module, Mode mode) {
        String fxmlEnding = ".fxml";
        String path;

        if (mode == BasicModes.CONTENT) {
            path = constructFXMLPath(module)
                    .concat(mode.getString())
                    .concat(module.toString())
                    .concat(fxmlEnding);
        } else {
            String moduleString = capFirst(module.toString());
            path = constructFXMLPath(module)
                    .concat(mode.getString())
                    .concat(moduleString)
                    .concat(fxmlEnding);
        }
        return path;
    }*/

    public static String getMainPath() {
        String extension = ".fxml";
        String path;

        Main module = Main.ENTERPRISE;

        String moduleString = module.toString().toLowerCase();
        path = getFxmlDirectory()
                .concat(moduleString)
                .concat(extension);

        return path;
    }

    public enum Main implements Module {
        ENTERPRISE("Enterprise") {
            @Override
            public boolean deleteEntry(CreationEntry entry) {
                throw new IllegalAccessError();
            }

            @Override
            public List<CreationEntry> getEntries() {
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
                return false;
            }
        };

        final String name;

        Main(String enterprise) {
            name = enterprise;
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