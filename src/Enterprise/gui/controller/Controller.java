package Enterprise.gui.controller;

/**
 * Basic interface of all Controller.
 * A Controller is the accompanied logic for the graphical user interface
 * defined in the fxml file.
 */
public interface Controller {
    /**
     * Opens a new Window with the content specified by the corresponding
     * fxml File.
     */
    void open();

    //public abstract void close() throws IOException;
}
