package Enterprise.data.concurrent;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.database.CreationEntryTable;
import Enterprise.data.intface.CreationEntry;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Service class responsible for updating Entries in the underlying DataBase, before the program terminates.
 * Gets called from {@link OnCloseRun}.
 */
public class UpdateCall implements Callable<Boolean> {

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
        Thread.currentThread().setName("UpdateEntries");

        Boolean updated = false;
        //gets the updates Entries
        List<CreationEntry> entries = OpEntryCarrier.getInstance().getUpdateEntries();
        //updates the Entries in the database
        updated = CreationEntryTable.getInstance().updateEntries(entries);


        System.out.println("UpdateCall wurde ausgeführt!");
        return updated;
    }

    @Deprecated
    private Boolean updateTable() throws SQLException {
        Boolean updated = false;
        //List<? extends CreationEntry> animeEntries = anime.getInstance().getUpdateEntries();
        //List<? extends CreationEntry> bookEntries = Book.getInstance().getUpdateEntries();
        //List<? extends CreationEntry> mangaEntries = manga.getInstance().getUpdateEntries();
        //List<? extends CreationEntry> novelEntries = Novel.getInstance().getUpdateEntries();
        //List<? extends CreationEntry> seriesEntries = Series.getInstance().getUpdateEntries();


        //entries.addAll(animeEntries);
        //entries.addAll(bookEntries);
        //entries.addAll(novelEntries);
        //entries.addAll(mangaEntries);
        //entries.addAll(seriesEntries);


        List<CreationEntry> entries = OpEntryCarrier.getInstance().getUpdateEntries();
        updated = CreationEntryTable.getInstance().updateEntries(entries);

        return updated;
    }

}
