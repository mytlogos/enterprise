package Enterprise.modules;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.CreationEntry;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public enum BasicModules implements Module {
    ANIME("Anime") {
        List<CreationEntry> entries = new ArrayList<>();

        @Override
        List<CreationEntry> getList() {
            return entries;
        }

    },
    BOOK("Bücher") {
        List<CreationEntry> entries = new ArrayList<>();

        @Override
        List<CreationEntry> getList() {
            return entries;
        }
    },
    MANGA("Manga") {
        List<CreationEntry> entries = new ArrayList<>();

        @Override
        List<CreationEntry> getList() {
            return entries;
        }
    },
    NOVEL("Novel") {
        List<CreationEntry> entries = new ArrayList<>();

        @Override
        List<CreationEntry> getList() {
            return entries;
        }
    },
    SERIES("Serien") {
        List<CreationEntry> entries = new ArrayList<>();

        @Override
        List<CreationEntry> getList() {
            return entries;
        }
    };

    private final String tabName;

    BasicModules(String tabName) {
        this.tabName = tabName;
    }

    @Override
    public List<CreationEntry> getEntries() {
        return getList();
    }

    @Override
    public boolean addEntry(CreationEntry entry) {
        return getList().add(entry);
    }

    abstract List<CreationEntry> getList();

    @Override
    public boolean deleteEntry(CreationEntry entry) {
        boolean deleted;
        if (!this.getList().contains(entry)) {
            System.out.println("'Entry' nicht vorhanden!");
            deleted = false;
        } else {
            if (OpEntryCarrier.getInstance().addDeleted(entry)) {
                entry.setDead();
                deleted = getList().remove(entry);
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
}
