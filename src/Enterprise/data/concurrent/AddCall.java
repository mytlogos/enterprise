package Enterprise.data.concurrent;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.database.CreationEntryTable;
import Enterprise.data.intface.CreationEntry;
import Enterprise.misc.Log;

import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class responsible for adding new Entries to the DataBase.
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
        try {
            CreationEntryTable table = new CreationEntryTable();
            if (table.tableExists()) {
                for (CreationEntry entry : OpEntryCarrier.getInstance().getNewEntries()) {
                    if (entry.isNewEntry()) {
                        added = table.insert(entry);
                    }
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
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Adding Operation failed", e);
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Addcall tried: " + counter + " times");
        return added;
    }
}
