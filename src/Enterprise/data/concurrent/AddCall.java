package Enterprise.data.concurrent;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.database.CreationEntryTable;
import Enterprise.misc.Log;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class responsible for adding new Entries to the DataEntry.
 * // FIXME: 02.09.2017 returns false even though sth was added
 */
public class AddCall implements Callable<Boolean> {
    private Logger logger = Log.packageLogger(this);

    @Override
    public Boolean call() throws Exception {
        Thread.currentThread().setName("AddData-Thread");
        return addDataToDB();
    }

    private static int counter = 0;

    /**
     * method separated from {@link #call()} to allow recursive calls
     * <p>tries to add new entries to the underlying database,
     *
     * @return added - true if add operation was successful
     */
    private boolean addDataToDB() {
        boolean added = false;
        CreationEntryTable table = CreationEntryTable.getInstance();
        if (table.tableExists()) {
            added = table.insert(OpEntryCarrier.getInstance().getNewEntries());
            if (added) {
                OpEntryCarrier.getInstance().clearNewEntries();
            }
            logger.log(Level.INFO, "AddCall was successful");
        } else {
            counter++;
            int maxTries = 10;
            if (counter <= maxTries) {
                table.createTable();
                addDataToDB();
            } else {
                added = false;
            }
        }
        logger.log(Level.INFO, "Addcall tried: " + counter + " times");
        return added;
    }
}
