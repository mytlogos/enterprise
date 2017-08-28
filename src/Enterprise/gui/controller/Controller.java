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

    /**
     * Sets the focus on the Pane, which handled
     * the MouseClicked event.
     */
    void paneFocus();

    /**
     * Gets the corresponding subclass instance
     * of {@link Enterprise.modules.EnterpriseSegments}
     * and sets a reference to the instance to a field.
     */
    void setModuleEntry();
}
