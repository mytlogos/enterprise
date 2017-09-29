package Enterprise.modules;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.CreationEntry;
import Enterprise.misc.SetList;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public enum BasicModules implements Module {
    ANIME("Anime", true) {
        ObservableList<CreationEntry> entries = new ObservableListWrapper<>(new SetList<>());
        List<String> distinctionList = new SetList<>();

        @Override
        ObservableList<CreationEntry> getEntryList() {
            return entries;
        }

        @Override
        List<String> getDistinctionList() {
            return distinctionList;
        }

    },
    BOOK("Bücher", false) {
        ObservableList<CreationEntry> entries = new ObservableListWrapper<>(new SetList<>());
        List<String> distinctionList = new ArrayList<>();


        @Override
        ObservableList<CreationEntry> getEntryList() {
            return entries;
        }

        @Override
        List<String> getDistinctionList() {
            return distinctionList;
        }
    },
    MANGA("Manga", false) {
        ObservableList<CreationEntry> entries = new ObservableListWrapper<>(new SetList<>());
        List<String> distinctionList = new ArrayList<>();


        @Override
        ObservableList<CreationEntry> getEntryList() {
            return entries;
        }

        @Override
        List<String> getDistinctionList() {
            return distinctionList;
        }
    },
    NOVEL("Novel", true) {
        ObservableList<CreationEntry> entries = new ObservableListWrapper<>(new SetList<>());
        List<String> distinctionList = new ArrayList<>();


        @Override
        ObservableList<CreationEntry> getEntryList() {
            return entries;
        }

        @Override
        List<String> getDistinctionList() {
            return distinctionList;
        }
    },
    SERIES("Serien", false) {
        ObservableList<CreationEntry> entries = new ObservableListWrapper<>(new SetList<>());
        List<String> distinctionList = new ArrayList<>();


        @Override
        ObservableList<CreationEntry> getEntryList() {
            return entries;
        }

        @Override
        List<String> getDistinctionList() {
            return distinctionList;
        }
    };

    private final boolean sourceable;
    private final String tabName;

    BasicModules(String tabName, boolean sourceable) {
        this.sourceable = sourceable;
        this.tabName = tabName;
    }

    public boolean isSourceable() {
        return sourceable;
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

    @Override
    public boolean deleteEntry(CreationEntry entry) {
        boolean deleted;
        if (!this.getEntryList().contains(entry)) {
            System.out.println("'Entry' nicht vorhanden!");
            deleted = false;
        } else {
            if (OpEntryCarrier.getInstance().addDeleted(entry)) {
                entry.setDead();
                deleted = getEntryList().remove(entry);
            } else {
                deleted = false;
                System.out.println("Konnte nicht gelöscht werden!");
            }
        }
        return deleted;
    }

    public String tabName() {
        return tabName;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    /**
     * Returns the inner EntryList of this {@code BasicModules}
     *
     * @return
     */
    abstract ObservableList<CreationEntry> getEntryList();

    /**
     * @return
     */
    abstract List<String> getDistinctionList();
}
