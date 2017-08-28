package Enterprise.data.concurrent;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Runnable which executes several Operations, before the program terminates
 */
public class OnCloseRun implements Runnable {

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

    private ExecutorService pool;

    /**
     * The constructor of this {@code OnCloseRun}
     */
    public OnCloseRun() {
        //number of operations this Thread should start
        int numberOfOperations = 3;
        pool = Executors.newFixedThreadPool(numberOfOperations);
}

    @Override
    public void run() {
        Thread.currentThread().setName("OnCloseOperations");

        UpdateCall update = new UpdateCall();
        AddCall add = new AddCall();
        DeleteCall delete = new DeleteCall();

        //starts the On-Close Operations
        Future<Boolean> updateFuture = pool.submit(update);
        Future<Boolean> addFuture = pool.submit(add);
        Future<Boolean> deleteFuture = pool.submit(delete);

        //executioner should not accept any new tasks anymore
        pool.shutdown();

        boolean updated = false;
        boolean added = false;
        boolean deleted = false;

        try {
            deleted = deleteFuture.get();
            updated = updateFuture.get();
            added = addFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Problem in executing the on-Close-operations", e);
            e.printStackTrace();
        }

        //holds this thread alive, awaiting the termination of the executioner
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //logs the success of the operation, regardless if there was data to be added
        logger.log(Level.INFO, "Updated: " + updated + ", Added: " + added + " Deleted: " + deleted);

        System.out.println("Programm wird beendet.");

        logger.log(Level.INFO, "program terminated normally");
        System.exit(0);
    }
}
