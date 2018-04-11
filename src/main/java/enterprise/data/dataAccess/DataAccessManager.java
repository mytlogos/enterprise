package enterprise.data.dataAccess;

import enterprise.data.Default;
import enterprise.data.EntryCarrier;
import enterprise.data.concurrent.Updater;
import enterprise.data.dataAccess.gorgon.GorgonLayer;
import enterprise.data.dataAccess.hibernate.HibernateLayer;
import enterprise.data.dataAccess.plain.PlainLayer;
import enterprise.data.dataAccess.spring.SpringLayer;
import enterprise.data.intface.CreationEntry;
import enterprise.data.intface.DataEntry;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;

/**
 *
 */
public class DataAccessManager implements Closeable {
    public static final DataAccessManager manager = new DataAccessManager();
    private static final String name = "enterprise";
    private static final String location = Default.workDir;
    private AccessType type;

    public DataAccessManager() {
        setType(AccessType.HIBERNATE);

        if (manager != null) {
            throw new IllegalStateException("already instantiated!");
        }
    }

    public void setType(AccessType type) {
        this.type = type;
        Updater.setLayer(type.getLayer());
    }

    public void update(Collection<DataEntry> entries) {
        checkClosed();
        type.getLayer().update(entries);
    }

    private void checkClosed() {
        if (type.closed) {
            System.out.println("already closed");
            throw new IllegalStateException("Layer already closed!");
        }
    }

    public Collection<CreationEntry> getEntries() {
        checkClosed();
        return type.getLayer().getCreationEntries();

    }

    public <E extends DataEntry> Collection<E> get(Class<E> eClass) {
        checkClosed();
        return type.getLayer().getAll(eClass);
    }

    public void startUpdater() {
        checkClosed();
        type.getLayer().startUpdater();
    }

    public void stopUpdater() {
        type.getLayer().stopUpdater();
    }

    public void delete(Collection<DataEntry> deleted) {
        checkClosed();
        type.getLayer().delete(deleted);
    }

    public void addNew() {
        checkClosed();
        add(new ArrayList<>(EntryCarrier.getInstance().getNewEntries()));
    }

    public void add(Collection<DataEntry> entries) {
        checkClosed();
        type.getLayer().add(entries);
    }

    @Override
    public void close() throws IOException {
        if (!type.closed) {
            type.getLayer().close();
            type.closed = true;
        }
    }

    public enum AccessType {
        SPRING(SpringLayer::new),
        PLAIN_JDBC(PlainLayer::new),
        GORGON(GorgonLayer::new),
        HIBERNATE(HibernateLayer::new),;

        private final BiFunction<String, String, DataAccessLayer> layerFunction;
        private DataAccessLayer layer = null;
        private boolean closed;

        AccessType(BiFunction<String, String, DataAccessLayer> function) {
            this.layerFunction = function;
        }

        public DataAccessLayer getLayer() {
            return layer == null ? layer = layerFunction.apply(DataAccessManager.name, DataAccessManager.location) : layer;
        }
    }
}
