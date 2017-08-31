package Enterprise.misc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 */
public class Log {

    private static int counter = 0;

    public static Logger classLogger(Class<?> logClass) {
        Logger logger = Logger.getLogger(logClass.getSimpleName());

        addFileHandler(logger, logClass.getSimpleName());

        return logger;
    }

    public static Logger classLogger(Object logInstance) {
        Logger logger = Logger.getLogger(logInstance.getClass().getSimpleName());

        addFileHandler(logger, logInstance.getClass().getSimpleName());

        return logger;
    }

    public static Logger packageLogger(Class<?> logClass) {
        Logger logger = Logger.getLogger(logClass.getPackage().getName());

        addFileHandler(logger, logClass.getPackage().getName());

        return logger;
    }

    public static Logger packageLogger(Object logInstance) {
        Logger logger = Logger.getLogger(logInstance.getClass().getPackage().getName());

        addFileHandler(logger, logInstance.getClass().getPackage().getName());

        return logger;
    }

    private static void addFileHandler(Logger logger, String fileName) {
        String directory = "log\\";
        try {
            if (checkDirectory(directory)) {
                //creates a FileHandler for this Package and adds it to this logger
                FileHandler fileHandler = new FileHandler(directory + fileName + ".log", true);

                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);
            } else {
                System.out.println("could not create or find directory: " + directory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkDirectory(String directory) throws IOException {
        boolean exists = false;
        if (counter > 10) {
            return false;
        }
        Path directoryPath = Paths.get(directory);
        if (!Files.exists(directoryPath) && Files.notExists(directoryPath)) {

            Files.createDirectory(directoryPath);
            counter++;
            checkDirectory(directory);

        } else if (Files.exists(directoryPath)) {
            exists = true;
        } else {
            System.out.println("programm hat nicht die nötigen rechte um den pfad abzufragen");
        }
        return exists;
    }
}
