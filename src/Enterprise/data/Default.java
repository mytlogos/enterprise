package Enterprise.data;

import Enterprise.misc.Log;
import scrape.sources.Source;

import java.net.URISyntaxException;
import java.util.logging.Level;

import static scrape.sources.Source.SourceType.START;

/**
 * Utility class for providing several default values.
 */
public class Default {
    public static final int VALUE = 0;
    public static final String STRING = "";
    public static final String KEYWORDS = "";
    public static final String IMAGE = "img\\NoImg.png";
    public static final String LIST = "Alles";
    public static Source SOURCE;

    static {
        try {
            SOURCE = Source.create("https://www.google.de/", START);
        } catch (URISyntaxException e) {
            Log.packageLogger(Default.class).log(Level.SEVERE, "faulty url", e);
        }
    }
}
