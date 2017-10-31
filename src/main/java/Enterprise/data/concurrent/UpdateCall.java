package Enterprise.data.concurrent;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.database.*;
import Enterprise.data.intface.*;
import Enterprise.data.update.EntryWrapper;
import scrape.sources.Source;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Service class responsible for updating Entries in the underlying DataEntry, before the program terminates.
 * Gets called from {@link OnCloseRun}.
 */
public class UpdateCall implements Callable<Boolean> {

    @Override
    public Boolean call() throws Exception {
        Thread.currentThread().setName("UpdateEntries");

        /*List<Creation> creations = OpEntryCarrier.getInstance().getUpdateCreations();
        List<Creator> creators = OpEntryCarrier.getInstance().getUpdateCreators();
        List<User> users = OpEntryCarrier.getInstance().getUpdateUsers();
        List<Sourceable> sourceables = OpEntryCarrier.getInstance().getUpdateSourceable();
        List<Source> sources = OpEntryCarrier.getInstance().getUpdateSources();*/


        List<CreationEntry> updateEntries = OpEntryCarrier.getInstance().getUpdateEntries();

        Collection<Source> sources = new ArrayList<>();
        Collection<Creator> creators = new ArrayList<>();
        Collection<Creation> creations = new ArrayList<>();
        Collection<User> users = new ArrayList<>();
        Collection<Sourceable> sourceables = new ArrayList<>();

        for (CreationEntry updateEntry : updateEntries) {
            EntryWrapper wrapper = EntryWrapper.getWrapper(updateEntry);
            List<DataEntry> updatedEntries = wrapper.getUpdatedEntries();

            for (DataEntry updatedEntry : updatedEntries) {
                if (updatedEntry instanceof Source) {
                    sources.add((Source) updatedEntry);
                } else if (updatedEntry instanceof Creator) {

                    creators.add((Creator) updatedEntry);
                } else if (updatedEntry instanceof Creation) {

                    creations.add((Creation) updatedEntry);
                } else if (updatedEntry instanceof User) {

                    users.add((User) updatedEntry);
                } else if (updatedEntry instanceof Sourceable) {

                    sourceables.add((Sourceable) updatedEntry);
                }
            }
        }

        boolean sourceUpdated = updateTable(sources, SourceTable.getInstance());
        boolean creatorsUpdated = updateTable(creators, CreatorTable.getInstance());
        boolean creationsUpdated = updateTable(creations, CreationTable.getInstance());
        boolean usersUpdated = updateTable(users, UserTable.getInstance());
        boolean sourceablesUpdated = updateTable(sourceables, SourceableTable.getInstance());
        boolean sourceRelationUpdated = (!sourceables.isEmpty() && EntrySourceTable.getInstance().updateEntries(sourceables));

        System.out.println("UpdateCall wurde ausgeführt!");
        return sourceUpdated ||
                creationsUpdated ||
                creatorsUpdated ||
                usersUpdated ||
                sourceablesUpdated ||
                sourceRelationUpdated
                ;
    }

    private <E extends DataEntry> boolean updateTable(Collection<E> entries, AbstractDataTable<E> dataTable) {
        return !entries.isEmpty() && dataTable.updateEntries(entries);
    }

}
