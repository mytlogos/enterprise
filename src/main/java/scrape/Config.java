package scrape;

import enterprise.data.impl.AbstractDataEntry;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 *
 */
public abstract class Config extends AbstractDataEntry {
    private boolean init;
    private LocalDateTime lastInit;

    public final boolean isInit() {
        if (lastInit != null && lastInit.isBefore(expiration().apply(LocalDateTime.now()))) {
            return init = false;
        }
        return init;
    }

    protected Function<LocalDateTime, LocalDateTime> expiration() {
        return localDateTime -> localDateTime.minusDays(7);
    }

    public void setInit() {
        lastInit = LocalDateTime.now();
        init = true;
    }

    public void setLastInit(LocalDateTime lastInit) {
        if (lastInit.isBefore(LocalDateTime.now().minusDays(7))) {
            init = false;
        }
        this.lastInit = lastInit;
    }

    public abstract String getKey();

    public LocalDateTime lastInit() {
        return lastInit;
    }

}
