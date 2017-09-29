package Enterprise.modules;

import Enterprise.data.intface.CreationEntry;

import java.util.List;

/**
 *
 */
public interface Module {
    String tabName();

    boolean deleteEntry(CreationEntry entry);

    List<? extends CreationEntry> getEntries();

    boolean addEntry(CreationEntry entry);

    List<String> getListNames();

    boolean isSourceable();
}
