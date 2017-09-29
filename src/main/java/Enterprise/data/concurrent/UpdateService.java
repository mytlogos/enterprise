package Enterprise.data.concurrent;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.database.CreationEntryTable;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.modules.BasicModules;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Scheduled Service for periodically updating and adding new Entries to the underlying database.
 */
public class UpdateService extends ScheduledService<Boolean> {

    /**
     * The constructor of {@code UpdateService}.
     * Default period is set to 1 minute.
     */
    public UpdateService() {
        setPeriod(Duration.minutes(1));
    }


    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {

            List<CreationEntry> updateEntries = new ArrayList<>();
            List<CreationEntry> newEntries = new ArrayList<>();

            final int maxWork = newEntries.size() + updateEntries.size();
            int currentProgress = 1;

            @Override
            protected Boolean call() throws Exception {
                Thread.currentThread().setName("Scheduled UpdateThread");

                updateEntries = OpEntryCarrier.getInstance().getUpdateEntries();
                newEntries = OpEntryCarrier.getInstance().getNewEntries();
                BasicModules.ANIME.getEntries().forEach(entry -> {
                    System.out.println("Entry: " + entry.isUpdated());
                    System.out.println("Sourceable: " + ((SourceableEntry) entry).getSourceable().isUpdated());
                    System.out.println("SourceList: " + ((SourceableEntry) entry).getSourceable().getSourceList().listChangedProperty().get());
                    ((SourceableEntry) entry).getSourceable().getSourceList().forEach(source -> {
                        System.out.println("Source: " + source + " is " + source.isUpdated());
                        System.out.println("Source: " + source + " is  new?: " + source.isNewEntry());
                        System.out.println("Configs is: " + source.getConfigs().isUpdated());
                    });
                    System.out.println();
                });

                updateDataFromDB(updateEntries);
                addDataToDB(newEntries);

                //clears both lists, so this will not be executed twice
                OpEntryCarrier.getInstance().clearNewEntries();
                OpEntryCarrier.getInstance().clearUpdateEntries();

                // TODO: 21.08.2017 do sth better
                //if no exception were thrown, then it should return true
                return true;
            }

            /**
             * Updates the Entries in the Database, with the data in the entries
             *
             * @param entries entries to be updated in the database
             */
            private void updateDataFromDB(List<? extends CreationEntry> entries) {
                CreationEntryTable table = CreationEntryTable.getInstance();
                for (CreationEntry entry : entries) {
                    table.updateEntry(entry);

                    updateProgress(currentProgress, maxWork);
                    currentProgress++;
                }
            }

            /**
             * Adds new Entries to the underlying database
             *
             * @param entries list of {@link CreationEntry}s
             */
            private void addDataToDB(List<? extends CreationEntry> entries) {
                CreationEntryTable animeTable = CreationEntryTable.getInstance();

                for (CreationEntry entry : entries) {
                    animeTable.insert(entry);
                    updateProgress(currentProgress, maxWork);
                    currentProgress++;
                    System.out.println(currentProgress);
                }
            }
        };
    }
}
