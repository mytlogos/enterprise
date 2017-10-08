package Enterprise.data.concurrent;

import Configs.SettingsRun;
import Enterprise.misc.Log;
import scrape.concurrent.PostCall;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import static scrape.concurrent.PostCall.Action.ADD_ENTRIES;
import static scrape.concurrent.PostCall.Action.DELETE_ENTRIES;

/**
 * Runnable which executes several Operations, before the program terminates
 */
public class OnCloseRun implements Runnable {

    private Logger logger = Log.packageLogger(this);

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
        PostCall addPosts = new PostCall(ADD_ENTRIES);
        PostCall deletePosts = new PostCall(DELETE_ENTRIES);
        SettingsRun saveSettings = new SettingsRun(SettingsRun.Action.SAVE);


        //starts the On-Close Operations
        Future<Boolean> updateFuture = pool.submit(update);
        Future<Boolean> addFuture = pool.submit(add);
        Future<Boolean> deleteFuture = pool.submit(delete);
        Future<Boolean> deletePostsFuture = pool.submit(addPosts);
        Future<Boolean> addPostsFuture = pool.submit(deletePosts);
        pool.submit(saveSettings);

        //executioner should not accept any new tasks anymore
        pool.shutdown();

        boolean deleted = getFuture(deleteFuture);
        boolean updated = getFuture(updateFuture);
        boolean added = getFuture(addFuture);
        boolean postsAdded = getFuture(addPostsFuture);
        boolean postsDeleted = getFuture(deletePostsFuture);

        //holds this thread alive, awaiting the termination of the executioner
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //logs the success of the operation, regardless if there was data to be added
        logger.log(Level.INFO, "Updated: " + updated + ", Added: " + added + ", Deleted: " + deleted);
        logger.log(Level.INFO, "Any Posts deleted: " + postsDeleted + ", Any Posts Added: " + postsAdded);

        System.out.println("Programm wird beendet.");
        logger.log(Level.INFO, "program terminated normally");
        System.exit(0);
    }

    private Boolean getFuture(Future<Boolean> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Problem in executing the on-Close-operations", e);
            return false;
        }
    }
}
