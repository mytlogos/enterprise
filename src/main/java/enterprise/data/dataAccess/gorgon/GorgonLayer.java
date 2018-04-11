package enterprise.data.dataAccess.gorgon;

import enterprise.data.Default;
import enterprise.data.dataAccess.DataAccessLayerImpl;
import enterprise.data.intface.DataEntry;
import gorgon.external.Gorgon;
import gorgon.external.PersistenceException;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 *
 */
public class GorgonLayer extends DataAccessLayerImpl {

    private Gorgon gorgon;

    public GorgonLayer(String name, String location) {
        super(name, location);
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
    public void delete(Collection<DataEntry> entries) {
        try {
            gorgon.delete(entries);
        } catch (PersistenceException e) {
            Default.LOGGER.log(Level.SEVERE, "deleting entries may have failed", e);
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
        super.close();
        //todo close gorgon
    }
}
