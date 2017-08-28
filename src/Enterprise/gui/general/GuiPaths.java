package Enterprise.gui.general;

import Enterprise.modules.Module;

/**
 * Utility Class for providing the several Paths to the fxml Files.
 */
public class GuiPaths {

    private static String constructFXMLPath(Module module) {
        String guiPath = "/Enterprise/gui/";
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
            firstChar = remnant.substring(start, start +1).toUpperCase();

            remnantChars = remnant.substring(start +1,index);
            remnant = remnant.substring(index).trim();


            newString = newString.concat(" ").concat(firstChar.concat(remnantChars));
        }while (index != -1);

        return newString;
    }

    /**
     * Capitalizes the first letter of the given String
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
     * @param mode {@code Mode} of the file
     * @return path - a relative path to the current working environment
     */
    public static String getPath(Module module, Mode mode) {
        String fxmlEnding = ".fxml";
        String path;
        if (Module.ENTERPRISE == module) {
            String moduleString = capFirst(module.toString());
            path = constructFXMLPath(module)
                    .concat(moduleString)
                    .concat(fxmlEnding);
        }else if (mode == Mode.CONTENT) {
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
    }
}
