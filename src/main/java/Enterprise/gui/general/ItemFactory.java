package Enterprise.gui.general;

import Enterprise.ControlComm;
import Enterprise.data.intface.CreationEntry;
import Enterprise.gui.controller.ContentController;
import Enterprise.gui.general.Columns.Column;
import Enterprise.modules.Module;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.CheckMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class generates {@link CheckMenuItem}s with several default values.
 * The created {@code CheckMenuItems} are intended to be used for the {@code viewMenu}
 * of the {@link Enterprise.gui.enterprise.controller.EnterpriseController} in the
 * 'hide/show XYColumns' area.
 */
public class ItemFactory {

    /**
     * Creates a {@code List} of all {@link CheckMenuItem}s corresponding to the columns
     * inherent to the {@link ContentController} instance of the parameter.
     *
     * @param module {@code Module} to specify the text of the items
     * @return list of {@code CheckMenuItems}
     */
    public List<CheckMenuItem> getCheckMenuItems(Module module) {
        ContentController controller = (ContentController) ControlComm.getInstance().getController(module, BasicModes.CONTENT);

        List<Column<? extends CreationEntry, ?>> columns = controller.getColumnManager().getColumns();
        List<CheckMenuItem> items = new ArrayList<>();
        columns.forEach(column -> items.add(getCheckMenuItem(controller, column)));
        return items;
    }

    public CheckMenuItem getCheckMenuItem(ContentController controller, Column column) {
        return checkMenuItemFactory(column.getName(), column.getDefaultSelect(),
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.getColumnManager().showColumn(column);
                    } else {
                        controller.getColumnManager().hideColumn(column);
                    }
                });
    }

    /**
     * The Factory Method for creating a {@link CheckMenuItem} with the values provided
     * by the parameter.
     *
     * @param text           {@code String} to set as visible text of the {@code CheckMenuItem}
     * @param defaultSel     the default selection
     * @param changeListener the behaviour on a selection change
     * @return item - a complete {@code CheckMenuItem}
     */
    private CheckMenuItem checkMenuItemFactory(String text, boolean defaultSel, ChangeListener<Boolean> changeListener) {
        CheckMenuItem item = new CheckMenuItem();
        item.setText(text);
        item.setSelected(defaultSel);

        item.selectedProperty().addListener(changeListener);
        return item;
    }
}
