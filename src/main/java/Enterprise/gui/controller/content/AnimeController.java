package Enterprise.gui.controller.content;

import Enterprise.ControlComm;
import Enterprise.gui.controller.SourceableContentCont;
import Enterprise.gui.general.BasicModes;
import Enterprise.modules.BasicModules;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the Controller of {@code anime.fxml} in mode {@link BasicModes#CONTENT}
 * and {@link BasicModules#ANIME}
 */
public class AnimeController extends SourceableContentCont<BasicModules> implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControlComm.getInstance().setController(this, module, mode);

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
    protected void setModule() {
        module = BasicModules.ANIME;
    }


    @Override
    @Deprecated
    public void open() {
        throw new IllegalAccessError();
    }
   /* //methods for theoretical auto fit of the columns to their content

    private static Method columnToFitMethod;

    static {
        try {
            columnToFitMethod = TableViewSkin.class.getDeclaredMethod("resizeColumnToFitContent", TableColumn.class, int.class);
            columnToFitMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    //copied Method for auto-Fitting the Widths of the Columns to their Content

    private void autoFitTable(TableView<?> tableView) {
        tableView.getItems().addListener((ListChangeListener<Object>) c -> {
            for (javafx.event.EventTarget column : tableView.getColumns()) {
                try {
                    columnToFitMethod.invoke(tableView.getSkin(), column, -1);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
}
