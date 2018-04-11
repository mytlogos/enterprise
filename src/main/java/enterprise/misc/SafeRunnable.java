package enterprise.misc;

import enterprise.data.Default;

import java.util.Objects;
import java.util.logging.Level;

/**
 * I got the Solution to the silent crash of the {@link java.util.concurrent.ScheduledExecutorService}
 * from this ingenious article:
 * <a href="http://code.nomad-labs.com/2011/12/09/mother-fk-the-scheduledexecutorservice/">
 * Mother F**k the ScheduledExecutorService! | Nomad Labs Code</a>
 */
public class SafeRunnable implements Runnable {

    private final Runnable runnable;

    public SafeRunnable(Runnable runnable) {
        Objects.requireNonNull(runnable);
        this.runnable = runnable;
    }


    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Exception e) {
            Default.LOGGER.log(Level.SEVERE, "runnable threw exception", e);
            throw new RuntimeException(e);
        }
    }
}
