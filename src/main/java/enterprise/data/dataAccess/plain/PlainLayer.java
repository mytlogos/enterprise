package enterprise.data.dataAccess.plain;

import enterprise.data.dataAccess.DataAccessLayerImpl;
import enterprise.data.intface.DataEntry;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class PlainLayer extends DataAccessLayerImpl {

    public PlainLayer(String name, String location) {
        super(name, location);
    }

    @Override
    public void add(Collection<DataEntry> entries) {

    }

    @Override
    public void update(Collection<DataEntry> entries) {

    }

    @Override
    public void delete(Collection<DataEntry> entries) {

    }

    @Override
    public <E extends DataEntry> Collection<E> getAll(Class<E> eClass) {
        return new ArrayList<>();
    }
}
