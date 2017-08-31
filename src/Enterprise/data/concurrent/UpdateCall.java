package Enterprise.data.concurrent;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.database.CreationEntryTable;
import Enterprise.data.intface.CreationEntry;
import Enterprise.misc.Log;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * Service class responsible for updating Entries in the underlying DataBase, before the program terminates.
 * Gets called from {@link OnCloseRun}.
 */
public class UpdateCall implements Callable<Boolean> {

    private Logger logger = Log.packageLogger(this);

    @Override
    public Boolean call() throws Exception {
        Thread.currentThread().setName("UpdateEntries");

        Boolean updated;
        //gets the updates Entries
        List<CreationEntry> entries = OpEntryCarrier.getInstance().getUpdateEntries();
        //updates the Entries in the database
        updated = CreationEntryTable.getInstance().updateEntries(entries);


        System.out.println("UpdateCall wurde ausgeführt!");
        return updated;
    }

    @Deprecated
    private Boolean updateTable() throws SQLException {
        Boolean updated;

        List<CreationEntry> entries = OpEntryCarrier.getInstance().getUpdateEntries();
        for (CreationEntry entry : entries) {
            CreationEntryTable.getInstance().updateEntry(entry);
        }
        updated = CreationEntryTable.getInstance().updateEntries(entries);

        return updated;
    }

}
