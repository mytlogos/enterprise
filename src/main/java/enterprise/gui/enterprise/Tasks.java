package enterprise.gui.enterprise;

import javafx.application.Platform;
import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 *
 */
public class Tasks {
    private static Tasks instance = new Tasks();
    private Queue<UpdateAbleRunnable> taskQueue = new ArrayDeque<>();
    private Map<Runnable, UpdateAbleRunnable> map = new HashMap<>();
    private StringProperty message = new SimpleStringProperty();
    private DoubleProperty workDone = new SimpleDoubleProperty();

    private Tasks() {
        if (instance != null) {
            throw new IllegalStateException("already instantiated");
        }
    }

    public static Tasks get() {
        return instance;
    }

    public ReadOnlyStringProperty messageProperty() {
        return message;
    }

    public ReadOnlyDoubleProperty workDoneProperty() {
        return workDone;
    }

    public boolean isEmpty() {
        return taskQueue.isEmpty();
    }

    public void update(Runnable runnable, double percentage) {
        UpdateAbleRunnable updateAbleRunnable = map.get(runnable);
        if (updateAbleRunnable == null) {
            accept(runnable, "");
        }
        map.get(runnable).workDone.set(percentage);
    }

    public void accept(Runnable runnable, String message) {
        UpdateAbleRunnable updateAbleRunnable = new UpdateAbleRunnable(message);
        map.put(runnable, updateAbleRunnable);
        this.taskQueue.add(updateAbleRunnable);

        bind();
    }

    private void bind() {
        Platform.runLater(() -> {
            UpdateAbleRunnable first = taskQueue.peek();

            message.unbind();
            workDone.unbind();

            if (first != null) {
                message.bind(first.message);
                workDone.bind(first.workDone);
            } else {
                message.set("Fertig um " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                workDone.set(1);
            }
        });
    }

    public void finish(Runnable runnable) {
        UpdateAbleRunnable updateAbleRunnable = map.remove(runnable);
        this.taskQueue.remove(updateAbleRunnable);

        bind();
    }

    private static class UpdateAbleRunnable {
        private DoubleProperty workDone = new SimpleDoubleProperty(-1);
        private StringProperty message = new SimpleStringProperty();

        private UpdateAbleRunnable(String message) {

            if (message == null) {
                message = "";
            }
            this.message.set(message);
        }

        @Override
        public String toString() {
            return "UpdateAbleRunnable{" +
                    "workDone=" + workDone.get() +
                    ", message=" + message.get() +
                    '}';
        }
    }

}
