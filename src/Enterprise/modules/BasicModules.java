package Enterprise.modules;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.CreationEntry;
import Enterprise.misc.SetList;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public enum BasicModules implements Module {
    ANIME("Anime") {
        List<CreationEntry> entries = new SetList<>();
        List<String> distinctionList = new SetList<>();

        @Override
        List<CreationEntry> getEntryList() {
            return entries;
        }

        @Override
        List<String> getDistinctionList() {
            return distinctionList;
        }

    },
    BOOK("Bücher") {
        List<CreationEntry> entries = new SetList<>();
        List<String> distinctionList = new ArrayList<>();


        @Override
        List<CreationEntry> getEntryList() {
            return entries;
        }

        @Override
        List<String> getDistinctionList() {
            return distinctionList;
        }
    },
    MANGA("Manga") {
        List<CreationEntry> entries = new SetList<>();
        List<String> distinctionList = new ArrayList<>();


        @Override
        List<CreationEntry> getEntryList() {
            return entries;
        }

        @Override
        List<String> getDistinctionList() {
            return distinctionList;
        }
    },
    NOVEL("Novel") {
        List<CreationEntry> entries = new SetList<>();
        List<String> distinctionList = new ArrayList<>();


        @Override
        List<CreationEntry> getEntryList() {
            return entries;
        }

        @Override
        List<String> getDistinctionList() {
            return distinctionList;
        }
    },
    SERIES("Serien") {
        List<CreationEntry> entries = new SetList<>();
        List<String> distinctionList = new ArrayList<>();


        @Override
        List<CreationEntry> getEntryList() {
            return entries;
        }

        @Override
        List<String> getDistinctionList() {
            return distinctionList;
        }
    };

    private final String tabName;

    BasicModules(String tabName) {
        this.tabName = tabName;
    }

    @Override
    public List<CreationEntry> getEntries() {
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
    abstract List<CreationEntry> getEntryList();

    /**
     * @return
     */
    abstract List<String> getDistinctionList();
}
