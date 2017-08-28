package Enterprise.test;

import javafx.concurrent.Task;

/**
 * Created by Dominik on 22.07.2017.
 * Part of OgameBot.
 */
public class IntegerTask  extends Task<Long> {

    static long counter = 0;
    @Override
    protected Long call() throws Exception {
        System.out.println(counter);
        System.out.println(Thread.currentThread());
        for (int x = 0; x<10; x++) {
            counter += 1;
            updateValue(counter);
            System.out.println(counter);
        }
        System.out.println("Schleife vorbei");
        return counter++;
    }
}
