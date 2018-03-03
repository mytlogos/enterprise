package enterprise.data.concurrent;

import enterprise.data.EntryCarrier;
import enterprise.data.dataAccess.DataAccessLayer;
import enterprise.data.intface.*;
import enterprise.gui.enterprise.Tasks;
import enterprise.modules.BasicModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class Updater implements Runnable {
    private static Updater updater = new Updater();
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
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
        if (scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        //check if already running
        if (!getUpdater().running) {
            scheduledFuture = scheduler.scheduleAtFixedRate(getUpdater(), 0, 1, TimeUnit.MINUTES);
        }
    }

    private static Updater getUpdater() {
        return updater;
    }

    public static void runOnce() {
        if (scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        scheduler.execute(getUpdater());
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
        Tasks.get().accept(this, "Aktualisiere Daten...");
        running = true;

        if (nextLayer != null) {
            currentLayer = nextLayer;
            nextLayer = null;
        }

        List<DataEntry> deleted = EntryCarrier.getInstance().getDeleted();
        List<DataEntry> newEntries = new ArrayList<>(EntryCarrier.getInstance().getNewEntries());

        List<DataEntry> list = Arrays.
                stream(BasicModule.values()).
                flatMap(basicModule -> basicModule.getEntries().stream()).
                collect(Collectors.toList());

        list.removeAll(deleted);
        list.removeAll(newEntries);

        currentLayer.delete(deleted);
        currentLayer.update(list);
        currentLayer.add(newEntries);

        running = false;
        Tasks.get().finish(this);
    }

    private Stream<DataEntry> map(CreationEntry entry) {
        User user = entry.getUser();
        Creation creation = entry.getCreation();
        Creator creator = entry.getCreator();

        List<DataEntry> entries = new ArrayList<>();
        entries.add(user);
        entries.add(creation);
        entries.add(creator);
        return entries.stream();
    }
}
