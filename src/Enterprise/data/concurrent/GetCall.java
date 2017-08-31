package Enterprise.data.concurrent;

import Enterprise.data.database.CreationEntryTable;
import Enterprise.data.intface.CreationEntry;
import Enterprise.misc.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class responsible for getting data from the underlying database
 */
public class GetCall implements Callable<List<? extends CreationEntry>> {
    private Logger logger = Log.packageLogger(this);

    @Override
    public List<? extends CreationEntry> call() throws Exception {
        Thread.currentThread().setName("GetData");

        List<? extends CreationEntry> creationLists = new ArrayList<>();

        CreationEntryTable entryTable;
        try {
            entryTable = new CreationEntryTable();

            //checks if table exists
            if (entryTable.tableExists()) {
                creationLists = entryTable.getEntries();
            } else {
                entryTable.createTable();
                creationLists = entryTable.getEntries();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Service could not get Data from Database", e);
            e.printStackTrace();
        }

        return creationLists;
    }
}
