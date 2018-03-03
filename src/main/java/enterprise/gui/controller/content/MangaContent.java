package enterprise.gui.controller.content;

import enterprise.gui.controller.SourceableContent;
import enterprise.gui.general.BasicMode;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.scene.control.TableView;

/**
 * The {@link BasicMode#CONTENT} Controller of the {@code manga.fxml} file.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class MangaContent extends SourceableContent {

    @Override
    public Module getModule() {
        return BasicModule.MANGA;
    }

    @Override
    public void initialize() {
        super.initialize();

        //readies the graphical interface components
        setIndexColumn();
        //adjusts the size of the columns to fill the size of the table
        entryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setGui();
        setRowListener();
        lockEditBtn();
        lockAddBtn();
    }

    @Override
    protected void setGui() {
        super.setGui();
        titleField.setPromptText("Titel");
        creatorField.setPromptText("Autor");
    }
}
