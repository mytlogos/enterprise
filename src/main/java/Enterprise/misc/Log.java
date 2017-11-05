package Enterprise.misc;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static Enterprise.misc.FileUtils.checkDirectory;

/**
 * Utility class to provide a {@link Logger} with
 * several default methods.
 * Default setting is that it saves the logs in the file, while appending,
 * not creating a new file every time.
 * Creates the directory "log" in the working directory, if it does not exist.
 */
public class Log {

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
     *
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
}
