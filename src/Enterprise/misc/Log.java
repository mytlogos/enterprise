package Enterprise.misc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Utility class to provide a {@link Logger} with
 * several default methods.
 * Default setting is that it saves the logs in the file, while appending,
 * not creating a new file every time.
 * Creates the directory "log" in the working directory, if it does not exist.
 *
 */
public class Log {

    /**
     * Counter for {@link #checkDirectory(String)}.
     */
    private static int counter = 0;

    /**
     * // TODO: 31.08.2017 do the doc
     *
     * @param logClass
     * @return
     */
    public static Logger classLogger(Class<?> logClass) {
        return getLogger(logClass.getSimpleName());
    }

    /**
     * // TODO: 31.08.2017 do the doc
     *
     * @param logInstance
     * @return
     */
    public static Logger classLogger(Object logInstance) {
        return getLogger(logInstance.getClass().getSimpleName());
    }

    /**
     * // TODO: 31.08.2017 do the doc
     *
     * @param logClass
     * @return
     */
    public static Logger packageLogger(Class<?> logClass) {
        return getLogger(logClass.getPackage().getName());
    }

    /**
     * // TODO: 31.08.2017 do the doc
     *
     * @param logInstance
     * @return
     */
    public static Logger packageLogger(Object logInstance) {
        return getLogger(logInstance.getClass().getPackage().getName());
    }

    /**
     * // TODO: 31.08.2017 do the doc
     *
     * @param name
     * @return
     */
    private static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        addFileHandler(logger, name);
        return logger;
    }

    /**
     * // TODO: 31.08.2017 do the doc
     * @param logger
     * @param fileName
     */
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
            System.out.println("logs will not be saved in a file");
        }
    }

    /**
     *
     * // TODO: 31.08.2017 do the doc
     * Counts the number of times it tried to check the directory.
     *  Aborts if it tried more than 10 times.
     *
     * @param directory
     * @return
     * @throws IOException
     */
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
            System.out.println("program does not the necessary rights to query the directory");
        }
        return exists;
    }
}
