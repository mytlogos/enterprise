package Enterprise.data.concurrent;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.database.CreationEntryTable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Service class responsible for deleting old Entries from the Database
 */
public class DeleteCall implements Callable<Boolean> {

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
