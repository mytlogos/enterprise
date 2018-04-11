package enterprise.data.concurrent;

import enterprise.data.EntryCarrier;
import enterprise.data.dataAccess.DataAccessLayer;
import enterprise.data.intface.DataEntry;
import enterprise.gui.enterprise.Tasks;
import enterprise.misc.SafeRunnable;
import enterprise.modules.BasicModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 */
public class Updater implements Runnable {
    private static Updater updater = new Updater();
    private static ScheduledExecutorService scheduler;
    private static ScheduledFuture<?> scheduledFuture;
    private DataAccessLayer currentLayer;
    private DataAccessLayer nextLayer;
    private volatile boolean running;

    private Updater() {
        if (updater != null) {
            throw new IllegalStateException("Already instantiated");
        }
    }

    public static void start() {
        checkScheduler();

        //check if already running
        if (!getUpdater().running) {
            scheduledFuture = scheduler.scheduleAtFixedRate(new SafeRunnable(getUpdater()), 0, 10, TimeUnit.SECONDS);
        }
    }

    private static void checkScheduler() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
    }

    private static Updater getUpdater() {
        return updater;
    }

    public static void runOnce() {
        checkScheduler();
        scheduler.execute(new SafeRunnable(getUpdater()));
    }

    public static void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
        }

        //is this even necessary?
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }
    }

    public static void setLayer(DataAccessLayer layer) {
        if (getUpdater().running) {
            getUpdater().nextLayer = layer;
        } else {
            getUpdater().currentLayer = layer;
        }
    }

    @Override
    public void run() {
        String previousName = Thread.currentThread().getName();
        Thread.currentThread().setName("DataUpdate-Thread");
        Tasks.get().accept(this, "Aktualisiere Daten...");
        System.out.println("updater running");
        running = true;

        if (nextLayer != null) {
            currentLayer = nextLayer;
            nextLayer = null;
        }

        if (currentLayer == null) {
            throw new IllegalStateException("No DataAccessLayer Available!");
        }

        List<DataEntry> deleted = new ArrayList<>(EntryCarrier.getInstance().getDeleted());
        List<DataEntry> newEntries = new ArrayList<>(EntryCarrier.getInstance().getNewEntries());

        EntryCarrier.getInstance().removeNewEntries(newEntries);
        EntryCarrier.getInstance().removeDeleted(deleted);

        List<DataEntry> list = Arrays.
                stream(BasicModule.values()).
                flatMap(basicModule -> basicModule.getEntries().stream()).
                collect(Collectors.toList());

        list.removeAll(deleted);
        list.removeAll(newEntries);

        currentLayer.delete(deleted);
        currentLayer.add(newEntries);
        currentLayer.update(list);

        running = false;
        System.out.println("updater finished");
        Tasks.get().finish(this);
        Thread.currentThread().setName(previousName);
    }

}
