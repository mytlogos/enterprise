package enterprise.gui.contextMenu;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class ContextMenuManager {
    private static final ContextMenuManager INSTANCE = new ContextMenuManager();
    private final ContextMenu globalMainMenu = new ContextMenu();
    private final Collection<MenuItem> tempItems = new ArrayList<>();

    private ContextMenuManager() {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
        initMainMenu();
    }

    private void initMainMenu() {
        globalMainMenu.setOnShowing(event -> {
            globalMainMenu.getItems().add(0, new SeparatorMenuItem());
            globalMainMenu.getItems().addAll(0, tempItems);
        });
        globalMainMenu.setOnHiding(event -> {
            globalMainMenu.getItems().removeAll(tempItems);
            tempItems.clear();
            ObservableList<MenuItem> items = globalMainMenu.getItems();
            for (int i = 0, itemsSize = items.size(); i < itemsSize; i++) {
                MenuItem item = items.get(i);
                if (item instanceof SeparatorMenuItem) {
                    globalMainMenu.getItems().remove(i);
                } else {
                    break;
                }
            }
        });
    }

    public static ContextMenuManager getInstance() {
        return INSTANCE;
    }

    public ContextMenu addPermItems(Collection<MenuItem> items) {
        globalMainMenu.getItems().addAll(items);
        return globalMainMenu;
    }

    public ContextMenu addTempItems(Collection<MenuItem> items) {
        prepTempItems(items);
        return globalMainMenu;
    }

    private void prepTempItems(Collection<MenuItem> items) {
        tempItems.addAll(items);
    }

    public ContextMenu getTempMenu(Collection<MenuItem> items) {
        ContextMenu contextMenu = new ContextMenu();

        contextMenu.getItems().addAll(globalMainMenu.getItems());
        contextMenu.getItems().add(0, new SeparatorMenuItem());
        contextMenu.getItems().addAll(0, items);

        return contextMenu;
    }

    public ContextMenu getTempMenu() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(globalMainMenu.getItems());
        return contextMenu;
    }
}
