package Enterprise.data.concurrent;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.modules.Anime;
import Enterprise.data.database.CreationEntryTable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Service class responsible for adding new Entries to the DataBase.
 */
public class AddCall implements Callable<Boolean> {
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
                    logger.log(Level.INFO, "AddCall was successful");
                }
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

    private boolean addToTable() throws SQLException {
        boolean added = false;
        CreationEntryTable table = new CreationEntryTable();
        if (table.tableExists()) {
            for (CreationEntry entry : OpEntryCarrier.getInstance().getNewEntries()) {
                if (entry.isNewEntry()) {
                    added = table.insert(entry);
                }
            }
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
        return added;
    }

    private boolean addToAnimeTable() throws SQLException {
        boolean added = false;
        CreationEntryTable animeTable = new CreationEntryTable();
        if (animeTable.tableExists()) {
            for (SourceableEntry entry : Anime.getInstance().getEntries()) {
                if (entry.isNewEntry()) {
                    added = animeTable.insert(entry);
                }
            }
        } else {
            counter++;
            int maxTries = 10;
            if (counter <= maxTries) {
                animeTable.createTable();
                addDataToDB();
            } else {
                added = false;
            }
        }
            return added;
    }
}
