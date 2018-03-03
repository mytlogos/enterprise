package enterprise.data.dataAccess;

import enterprise.data.intface.CreationEntry;
import enterprise.data.intface.DataEntry;

import java.io.Closeable;
import java.util.Collection;

/**
 *
 */
public interface DataAccessLayer extends Closeable {
    void add(Collection<DataEntry> entries);

    void update(Collection<DataEntry> entries);

    Collection<CreationEntry> getCreationEntries();

    void delete(Collection<DataEntry> entries);

    void startUpdater();

    void stopUpdater();

    <E extends DataEntry> Collection<E> getAll(Class<E> eClass);
}
