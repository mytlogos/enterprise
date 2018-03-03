package enterprise.data.dataAccess.spring;

import enterprise.data.dataAccess.DataAccessLayer;
import enterprise.data.intface.CreationEntry;
import enterprise.data.intface.DataEntry;

import java.util.Collection;

/**
 *
 */
public class SpringLayer implements DataAccessLayer {

    private final String name;
    private final String location;

    public SpringLayer(String name, String location) {
        this.name = name;
        this.location = location;
    }

    @Override
    public void add(Collection<DataEntry> entries) {

    }

    @Override
    public void update(Collection<DataEntry> entries) {

    }

    @Override
    public Collection<CreationEntry> getCreationEntries() {
        return null;
    }

    @Override
    public void delete(Collection<DataEntry> entries) {

    }

    @Override
    public void startUpdater() {

    }

    @Override
    public void stopUpdater() {

    }

    @Override
    public <E extends DataEntry> Collection<E> getAll(Class<E> eClass) {
        return null;
    }

    @Override
    public void close() {

    }
}
