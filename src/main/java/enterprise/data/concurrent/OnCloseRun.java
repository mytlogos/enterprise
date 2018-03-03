package enterprise.data.concurrent;

import Configs.SettingsRun;
import enterprise.data.Default;
import enterprise.data.EntryCarrier;
import enterprise.data.dataAccess.DataAccessManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runnable which executes several Operations, before the program terminates
 */
public class OnCloseRun implements Runnable {

    private final Logger logger = Default.LOGGER;

    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    @Override
    public void run() {
        Thread.currentThread().setName("OnCloseOperations");

        //starts the On-Close Operations
        DataAccessManager.manager.stopUpdater();
        pool.submit(DataAccessManager.manager::addNew);
        pool.submit(() -> DataAccessManager.manager.delete(new ArrayList<>(EntryCarrier.getInstance().getDeleted())));
        pool.submit(new SettingsRun(SettingsRun.Action.SAVE));

        //executioner should not accept any new tasks anymore
        pool.shutdown();

        //holds this thread alive, awaiting the termination of the executioner
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            //close all resources of all currently active data-access-layer
            DataAccessManager.manager.close();
        } catch (IOException e) {
            Default.LOGGER.log(Level.SEVERE, "exception occurred while closing data access layer", e);
        }

        //logs the success of the operation, regardless if there was data to be added
        System.out.println("Programm wird beendet.");
        logger.log(Level.INFO, "program terminated normally");
        System.exit(0);
    }
}
