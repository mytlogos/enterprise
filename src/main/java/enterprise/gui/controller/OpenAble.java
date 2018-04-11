package enterprise.gui.controller;

import enterprise.data.intface.CreationEntry;

/**
 *
 */
public interface OpenAble extends Controller {
    /**
     * Opens a new Window with the content specified by the corresponding
     * fxml File.
     */
    void open(CreationEntry entry);
}
