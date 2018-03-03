package enterprise.gui.controller.content;

import enterprise.gui.controller.SourceableContent;
import enterprise.gui.general.BasicMode;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.scene.control.TableView;

/**
 * This class is the Controller of {@code anime.fxml} in mode {@link BasicMode#CONTENT}
 * and {@link BasicModule#ANIME}
 */
public class AnimeContent extends SourceableContent {

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

    @Override
    public Module getModule() {
        return BasicModule.ANIME;
    }

}
