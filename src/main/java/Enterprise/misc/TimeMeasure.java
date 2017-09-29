package Enterprise.misc;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

/**
 *
 */
public class TimeMeasure {
    private long start = 0;
    private long finish = 0;
    private long elapsed;

    public void start() {
        start = System.nanoTime();
    }

    public void finish() {
        finish = System.nanoTime();
    }

    public String getMessage(Function<String, String> function, TimeOutPut outPut) {
        calcElapsed();
        return function.apply(outPut.getString(elapsed));
    }

    public String getMessage(Function<String, String> function) {
        return getMessage(function, TimeOutPut.SEC);
    }

    private void calcElapsed() {
        elapsed = finish - start;
        Duration duration = Duration.of(elapsed, ChronoUnit.NANOS);
        elapsed = duration.toMillis();
    }

    public long getElapsed() {
        calcElapsed();
        return elapsed;
    }

    public enum TimeOutPut {
        FULL_PRECISION {
            @Override
            String getString(long elapsed) {
                return null;
            }
        },
        ONLY_DAY {
            @Override
            String getString(long elapsed) {
                return null;
            }
        },
        ONLY_HOUR {
            @Override
            String getString(long elapsed) {
                return null;
            }
        },
        ONLY_MIN {
            @Override
            String getString(long elapsed) {
                return null;
            }
        },
        ONLY_SEC {
            @Override
            String getString(long elapsed) {
                return null;
            }
        },
        SEC {
            @Override
            String getString(long elapsed) {
                long milis = elapsed % 1000;
                long seconds = elapsed / 1000;
                return seconds + "," + milis;
            }
        };

        abstract String getString(long elapsed);
    }

}

