package Enterprise.data.concurrent;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.database.CreationEntryTable;
import Enterprise.data.intface.CreationEntry;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Scheduled Service for periodically updating and adding new Entries to the underlying database.
 */
public class UpdateService extends ScheduledService<Boolean> {

    private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

    {
        try {
            //creates a FileHandler for this Package and adds it to this logger
            FileHandler fileHandler = new FileHandler("log\\" + this.getClass().getPackage().getName() + ".log",true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

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

            List<CreationEntry> updateEntries = OpEntryCarrier.getInstance().getUpdateEntries();
            List<CreationEntry> newEntries = OpEntryCarrier.getInstance().getNewEntries();


            final int maxWork = newEntries.size() + updateEntries.size();
            int currentProgress = 1;

            @Override
            protected Boolean call() throws Exception {
                Thread.currentThread().setName("Scheduled UpdateThread");


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
            private void updateDataFromDB(List<? extends CreationEntry> entries){

                try {
                    CreationEntryTable table = new CreationEntryTable();
                    for (CreationEntry entry : entries) {
                        table.updateEntry(entry);

                        updateProgress(currentProgress, maxWork);
                        currentProgress++;
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "CreationEntryTable could not be instantiated", e);
                    e.printStackTrace();
                }
            }

            /**
             * Adds new Entries to the underlying database
             *
             * @param entries list of {@link CreationEntry}s
             */
            private void addDataToDB(List<? extends CreationEntry> entries) {
                try {
                    CreationEntryTable animeTable = new CreationEntryTable();
                        for (CreationEntry entry : entries) {
                                animeTable.insert(entry);
                                updateProgress(currentProgress, maxWork);
                                currentProgress++;
                                System.out.println(currentProgress);
                        }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "CreationEntryTable could not be instantiated", e);
                    e.printStackTrace();
                }
            }
        };
    }
}
