package Enterprise.data.concurrent;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.database.CreationEntryTable;
import Enterprise.data.intface.CreationEntry;
import Enterprise.misc.Log;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * Service class responsible for deleting old Entries from the Database
 */
public class DeleteCall implements Callable<Boolean> {

    private Logger logger = Log.packageLogger(this);

    @Override
    public Boolean call() throws Exception {
        Thread.currentThread().setName("DeleteEntries");

        boolean deleted = false;
        //getting the entries to delete from the OpEntryCarrier singleton
        List<CreationEntry> deletedEntries = OpEntryCarrier.getInstance().getDeleted();

        if (!deletedEntries.isEmpty()) {
            deleted = CreationEntryTable.getInstance().delete(deletedEntries);
        }

        return deleted;
    }
}
