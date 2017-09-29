package Enterprise.gui.general;

import Enterprise.modules.BasicModules;

/**
 * This enum represents Modes of gui modules of one {@link BasicModules}.
 * One opens the mode {@code ADD} for the controller and opens the {@code Add Window} etc.
 * It is also used for creating the paths for the fxml files.
 *
 * @see GuiPaths
 * @see Enterprise.ControlComm
 */
public enum BasicModes implements Mode {
    /**
     * This Mode is for the Adding Component of the
     * graphical user interface.
     * This Mode is for opening a new Window with the
     * all available fields for adding a new
     * {@link Enterprise.data.intface.CreationEntry}.
     */
    ADD,
    /**
     * This Mode is for the Content Component of the
     * graphical user interface.
     * It is for displaying the available data in
     * a pane, mostly loading the pane into the
     * tabPanes of the {@link Enterprise.gui.enterprise.controller.EnterpriseController}.
     */
    CONTENT,
    /**
     * This Mode is the Edit Component of a
     * specific {@link Enterprise.data.intface.CreationEntry} of the
     * graphical user interface.
     * It is for editing the values of the provided {@code CreationEntry}
     * and saving it in real-Time through the use of {@link javafx.beans.property.Property}.
     */
    EDIT,
    /**
     * This Mode ist the Content Display Component of a specific
     * {@link Enterprise.data.intface.CreationEntry} of the
     * graphical user interface.
     * It is for displaying all relevant values of the provided {@code CreationEntry}.
     */
    SHOW;

    public String getString() {
        if (this == BasicModes.CONTENT) {
            return "";
        }
        return super.toString().toLowerCase();
    }
}
