package enterprise.modules;

import enterprise.data.EntryCarrier;
import enterprise.data.intface.CreationEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tools.SetList;

import java.util.List;

/**
 *
 */
public enum BasicModule implements Module {
    ANIME("Anime", true),
    BOOK("Bücher", false),
    MANGA("Manga", false),
    NOVEL("Novel", true),
    SERIES("Serien", false);

    final ObservableList<CreationEntry> entries = FXCollections.observableArrayList(new SetList<>());
    final List<String> distinctionList = new SetList<>();
    private final boolean sourceable;
    private final String tabName;

    BasicModule(String tabName, boolean sourceable) {
        this.sourceable = sourceable;
        this.tabName = tabName;
    }

    public String showName() {
        return tabName;
    }

    @Override
    public boolean deleteEntry(CreationEntry entry) {
        boolean deleted;
        if (!this.getEntryList().contains(entry)) {
            System.out.println("'Entry' nicht vorhanden!");
            deleted = false;
        } else {
            if (EntryCarrier.getInstance().addDeleted(entry)) {
                deleted = getEntryList().remove(entry);
            } else {
                deleted = false;
                System.out.println("Konnte nicht gelöscht werden!");
            }
        }
        return deleted;
    }

    @Override
    public ObservableList<? extends CreationEntry> getEntries() {
        return getEntryList();
    }

    @Override
    public boolean addEntry(CreationEntry entry) {
        if (entry != null) {
            getDistinctionList().add(entry.getUser().getListName());
            return getEntryList().add(entry);
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public List<String> getListNames() {
        return getDistinctionList();
    }

    public boolean isSourceable() {
        return sourceable;
    }

    /**
     * Returns a List of ListNames, which the CreationEntries are divided in.
     *
     * @return a list of ListNames, never null
     */
    List<String> getDistinctionList() {
        return distinctionList;
    }

    /**
     * Returns the inner EntryList of this {@code BasicModule}
     *
     * @return a observable List of {@link CreationEntry}
     */
    ObservableList<CreationEntry> getEntryList() {
        return entries;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
