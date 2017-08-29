package Enterprise.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by Dominik on 08.08.2017.
 * Part of OgameBot.
 */
public class tester {
    static Connection con;
    static Logger logger = Logger.getLogger(tester.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("log\\handler.log",true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static void main(String... aArgs) throws IOException, URISyntaxException, SQLException {
/*
        String ROOT = "";
        FileVisitor<Path> fileProcessor = new ProcessFile();
        Files.walkFileTree(Paths.get(ROOT), fileProcessor);
*/


        System.out.println(System.getProperties());
        String lineseparator = System.lineSeparator();
        System.out.println("Line separator" + lineseparator + "Other line");
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("os.version"));
        System.out.println(System.getProperty("os.arch"));
        System.out.println(System.getProperty("user.name"));
        System.out.println(System.getProperty("user.home"));
        System.out.println(System.getProperty("user.dir"));
    }

    private static final class ProcessFile extends SimpleFileVisitor<Path> {
        @Override public FileVisitResult visitFile(
                Path aFile, BasicFileAttributes aAttrs
        ) throws IOException {
            System.out.println("Processing file:" + aFile);
            return FileVisitResult.CONTINUE;
        }

        @Override  public FileVisitResult preVisitDirectory(
                Path aDir, BasicFileAttributes aAttrs
        ) throws IOException {
            System.out.println("Processing directory:" + aDir);
            return FileVisitResult.CONTINUE;
        }
    }

    static Connection getCon() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:enterprise.db");
    }

    static void doSth() {
        LocalDateTime first = LocalDateTime.now();
        for (int x = 0; x < 100000; x++) {
            String string = "Method " + new Object() {}.getClass().getEnclosingMethod().getName();
        }
        LocalDateTime second = LocalDateTime.now();
        for (int x = 0; x < 100000; x++) {
            StackTraceElement[] elements = new Throwable().getStackTrace();
            String string = "Method " + elements[0];
        }
        LocalDateTime third = LocalDateTime.now();
        LocalDateTime temp = LocalDateTime.now();
        for (int i = 0; i < 100000; i++) {
            LocalDateTime loop = LocalDateTime.now();
            System.out.println(loop.getNano() - temp.getNano());
            temp = loop;
        }
        System.out.println("Erste Schleife: " + (second.getNano() - first.getNano()));
        System.out.println("Zweite Schleife: " + (third.getNano() - second.getNano()));
    }

}
