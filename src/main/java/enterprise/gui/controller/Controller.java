package enterprise.gui.controller;

import enterprise.ControlComm;
import enterprise.gui.general.Mode;
import enterprise.modules.Module;

/**
 * Basic interface of all Controller.
 * A Controller is the accompanied logic for the graphical user interface
 * defined in the fxml file.
 */
public interface Controller {

    default void initialize() {
        //sets instance in ControlComm to this
        ControlComm.get().setController(this, getModule(), getMode());
    }

    Module getModule();

    Mode getMode();
}
