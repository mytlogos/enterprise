package Enterprise.data.concurrent;

import Enterprise.data.database.CreationEntryTable;
import Enterprise.data.intface.CreationEntry;
import Enterprise.misc.Log;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * Service class responsible for getting data from the underlying database
 */
public class GetCall implements Callable<List<? extends CreationEntry>> {
    private Logger logger = Log.packageLogger(this);

    @Override
    public List<? extends CreationEntry> call() throws Exception {
        Thread.currentThread().setName("GetData");

        List<? extends CreationEntry> creationLists;

        CreationEntryTable entryTable;
        entryTable = CreationEntryTable.getInstance();

        //checks if table exists
        if (entryTable.tableExists()) {
            creationLists = entryTable.getEntries();
        } else {
            entryTable.createTable();
            creationLists = entryTable.getEntries();
        }

        return creationLists;
    }
}
