package enterprise.gui.general;

import enterprise.ControlComm;
import enterprise.data.intface.CreationEntry;
import enterprise.gui.controller.Content;
import enterprise.gui.enterprise.EnterpriseController;
import enterprise.gui.general.Columns.Column;
import enterprise.modules.Module;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.CheckMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class generates {@link CheckMenuItem}s with several default values.
 * The created {@code CheckMenuItems} are intended to be used for the {@code viewMenu}
 * of the {@link EnterpriseController} in the
 * 'hide/show XYColumns' area.
 */
public class ItemFactory {

    /**
     * Creates a {@code List} of all {@link CheckMenuItem}s corresponding to the columns
     * inherent to the {@link Content} instance of the parameter.
     *
     * @param module {@code Module} to specify the text of the items
     * @return list of {@code CheckMenuItems}
     */
    @Deprecated
    public List<CheckMenuItem> getCheckMenuItems(Module module) {
        Content controller = (Content) ControlComm.get().getController(module, BasicMode.CONTENT);

        List<Column<? extends CreationEntry, ?>> columns = controller.getColumnManager().getColumns();
        List<CheckMenuItem> items = new ArrayList<>();
        columns.forEach(column -> items.add(getCheckMenuItem(controller, column)));
        return items;
    }

    // TODO: 03.11.2017 check with column if raw type can be changed
    public CheckMenuItem getCheckMenuItem(Content controller, Column column) {
        return checkMenuItemFactory(
                column.getName(),
                column.getDefaultSelect(),
                (observable, oldValue, newValue) -> menuItemCheckChange(controller, column, newValue));
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

    private void menuItemCheckChange(Content controller, Column column, Boolean newValue) {
        if (newValue) {
            controller.getColumnManager().showColumn(column);
        } else {
            controller.getColumnManager().hideColumn(column);
        }
    }
}
