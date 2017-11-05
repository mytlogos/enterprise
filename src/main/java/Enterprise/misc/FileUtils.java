package Enterprise.misc;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.notExists;

/**
 *
 */
public abstract class FileUtils {
    /**
     * Counter for {@link #checkDirectory(String)}.
     */
    private static int counter = 0;

    /**
     * // TODO: 31.08.2017 do the doc
     * Counts the number of times it tried to check the directory.
     * Aborts if it tried more than 10 times.
     *
     * @param directory
     * @return
     */
    public static boolean checkDirectory(String directory) {
        boolean exists = false;
        if (counter > 5) {
            return false;
        }
        Path directoryPath = Paths.get(directory);
        if (!exists(directoryPath) && notExists(directoryPath)) {

            createDirectory(directoryPath);
            counter++;
            return checkDirectory(directory);

        } else if (exists(directoryPath)) {
            exists = true;
        } else {
            System.out.println("program does not the necessary rights to query the directory");
        }
        return exists;
    }

    private static void createDirectory(Path directoryPath) {
        try {
            java.nio.file.Files.createDirectory(directoryPath);
        } catch (IOException e) {
            Path userDir = Paths.get(workDirectory());
            Path parent = directoryPath.getParent();

            if (parent != null && !parent.equals(userDir)) {
                createDirectory(parent);
            }
        }
    }

    public static String workDirectory() {
        return System.getProperty("user.dir");
    }
}
