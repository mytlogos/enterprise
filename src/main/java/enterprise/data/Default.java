package enterprise.data;

import enterprise.EnterpriseStart;
import scrape.sources.Source;
import tools.Log;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static scrape.sources.SourceType.START;

/**
 * Utility class for providing several default values.
 */
public class Default {
    public static final int VALUE = 0;
    public static final String STRING = "";
    public static final String KEYWORDS = "";
    public static final String IMAGE = "img/NoImg.png";
    public static final String LIST = "Alles";
    public static final Source SOURCE;
    public static final String workDir = System.getProperty("user.dir");
    public static final Logger LOGGER = Log.packageLogger(EnterpriseStart.class);

    static {
        Source temp;
        try {
            temp = Source.create("https://www.google.de/", START);
        } catch (URISyntaxException e) {
            temp = null;
            Default.LOGGER.log(Level.SEVERE, "faulty url", e);
        }
        SOURCE = temp;
    }
}
