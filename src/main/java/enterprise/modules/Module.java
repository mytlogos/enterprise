package enterprise.modules;

import enterprise.data.intface.CreationEntry;
import javafx.collections.ObservableList;

import java.util.List;

/**
 *
 */
public interface Module {
    String tabName();

    boolean deleteEntry(CreationEntry entry);

    ObservableList<? extends CreationEntry> getEntries();

    boolean addEntry(CreationEntry entry);

    List<String> getListNames();

    boolean isSourceable();
}
