package enterprise.data.dataAccess.gorgon;

import enterprise.data.dataAccess.DataAccessLayer;
import enterprise.data.intface.CreationEntry;
import enterprise.data.intface.DataEntry;
import enterprise.data.intface.SourceableEntry;
import gorgon.external.Gorgon;
import gorgon.external.PersistenceException;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class GorgonLayer implements DataAccessLayer {

    private Gorgon gorgon;

    public GorgonLayer(String name, String location) {
        gorgon = Gorgon.create(name, location);
    }

    @Override
    public void add(Collection<DataEntry> entries) {
        entries.forEach(gorgon::wrapEntry);
        gorgon.addNew();
    }

    @Override
    public void update(Collection<DataEntry> entries) {

    }

    @Override
    public Collection<CreationEntry> getCreationEntries() {
        List<CreationEntry> entries = gorgon.get(CreationEntry.class, true);
        List<SourceableEntry> sourceableEntries = gorgon.get(SourceableEntry.class, true);

        entries.addAll(sourceableEntries);
        return entries;
    }

    @Override
    public void delete(Collection<DataEntry> entries) {
        try {
            gorgon.delete(entries);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startUpdater() {
        //starts the Updater
        gorgon.startUpdater(1, TimeUnit.MINUTES);
    }

    @Override
    public void stopUpdater() {
        gorgon.stopUpdater();
    }

    @Override
    public <E extends DataEntry> Collection<E> getAll(Class<E> eClass) {
        return gorgon.get(eClass, true);
    }

    @Override
    public void close() {

    }
}
