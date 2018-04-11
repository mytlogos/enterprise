package enterprise.data.dataAccess;

import enterprise.data.concurrent.Updater;
import enterprise.data.impl.CreationEntryImpl;
import enterprise.data.impl.SourceableEntryImpl;
import enterprise.data.intface.CreationEntry;
import scrape.sources.Source;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public abstract class DataAccessLayerImpl implements DataAccessLayer {
    protected final String name;
    protected final String location;

    protected DataAccessLayerImpl(String name, String location) {
        this.name = name;
        this.location = location;
    }

    @Override
    public Collection<CreationEntry> getCreationEntries() {
        Collection<CreationEntry> entries = new ArrayList<>();

        Collection<CreationEntryImpl> creationEntries = getAll(CreationEntryImpl.class);
        Collection<SourceableEntryImpl> sourceableEntries = getAll(SourceableEntryImpl.class);

        for (SourceableEntryImpl entry : sourceableEntries) {
            Source.cache(entry.getSourceable().getSources());
        }

        entries.addAll(creationEntries);
        entries.addAll(sourceableEntries);

        return entries;
    }

    @Override
    public void startUpdater() {
        Updater.start();
    }

    @Override
    public void stopUpdater() {
        Updater.stop();
    }

    @Override
    public void close() {
        stopUpdater();
        new Cleaner(name, location).cleanUp();
    }
}
