package Enterprise.test;

import Enterprise.misc.TimeMeasure;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Dominik on 05.08.2017.
 */
public class test {
    public static void main(String[] args) throws SQLException, IOException, URISyntaxException, InterruptedException {
        Processor process = new Processor();
//        getProcess(process, testScraper.getTocs().get(testScraper.getTocs().size() - 1));
        doSequential(testScraper.getTocs(), s -> getProcess(process, s));
    }

    private static void getProcess(Processor process, String s) {
        process.process(s);
    }

    private static void doSequential(List<String> list, Consumer<String> consumer) {
        TimeMeasure measure = TimeMeasure.start();
        list.forEach(s -> {
            try {
                consumer.accept(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        measure.finish();
        System.out.println(measure.getMessage(s -> "Time needed: " + s));
    }
}
