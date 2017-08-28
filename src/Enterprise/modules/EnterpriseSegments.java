package Enterprise.modules;

import Enterprise.data.OpEntryCarrier;
import Enterprise.misc.SetList;
import Enterprise.data.intface.CreationEntry;

import java.util.Collection;
import java.util.List;

/**
 * Created by Dominik on 27.07.2017.
 * Part of OgameBot.
 * // TODO: 24.08.2017 do the javadoc and revision
 */
public abstract class  EnterpriseSegments<E extends CreationEntry> {
    private List<E> entries = new SetList<>();

    public boolean deleteEntry(E entry) {
        boolean deleted;
        if (!entries.contains(entry)) {
            System.out.println("'Entry' nicht vorhanden!");
            deleted = false;
        } else {
            if (OpEntryCarrier.getInstance().addDeleted(entry)) {
                entry.setDead();
                deleted = entries.remove(entry);
            } else {
                deleted = false;
                System.out.println("Konnte nicht gelöscht werden!");
            }
        }
        return deleted;
    }

    public List<E> getEntries() {
        return entries;
    }

    public void setEntries(Collection<E> entries) {
        this.entries = new SetList<>();
        this.entries.addAll(entries);
    }

    public boolean addEntry(E entry) {
        return entries.add(entry);
    }

    public boolean addEntries(Collection<E> entryCollection) {
        return entries.addAll(entryCollection);
    }

}
