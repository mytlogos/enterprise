package Enterprise.test;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.CreatorContributor;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.Epub3Writer;
import nl.siegmann.epublib.epub.EpubWriter;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 */
public class stubdao {
    private static String safe = "";

    public static void main(String[] args) throws SQLException, IOException {
        File file = new File("test.html");
        String html = Jsoup.parse(stubdao.class.getClassLoader().getResourceAsStream("sousetsuka-testchapter8.html"), "UTF-8", "sousetsuka.com").outerHtml();
        FileUtils.writeStringToFile(file, html, "UTF-8");
//        convertToEpub();
    }

    private static void convertToEpub() throws IOException {
        Process process = Runtime.getRuntime().exec("ebook-convert.exe D:\\Programmieren\\Java\\Projects\\Enterprise\\src\\main\\resources\\test.html D:\\Programmieren\\Java\\Projects\\Enterprise\\ebooks\\testEbook.epub --no-default-epub-cover");
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = null;

        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }
    }

    private static void writeSth() throws IOException {
        // Create new Book
        Book book = new Book();

// Set the title
        book.getMetadata().addTitle("Epublib test book 1");

// Add an Author
        book.getMetadata().addCreator(new CreatorContributor("Joe", "Tester"));

// Add Chapter 1
        book.addSection("Introduction", new Resource(new ByteArrayInputStream(safe.getBytes()), "sousetsuka-testchapter8.html"));

// Create EpubWriter
        EpubWriter epubWriter = new Epub3Writer();

// Write the Book as Epub
        epubWriter.write(book, new FileOutputStream("test1_book1.epub"));
    }

    private static Connection getCon() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:enterprise.db");
    }
}
