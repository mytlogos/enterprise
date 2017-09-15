package scrape.sources.novels.strategies;

import org.jsoup.nodes.Element;
import scrape.sources.novels.ParseTime;

import java.time.LocalDateTime;

/**
 *
 */
public class PostFormat {

    public static Element timeElement(String time) {
        if (time == null || time.isEmpty()) {
            return null;
        }

        Element timeElement = new Element("time");

        LocalDateTime dateTime = ParseTime.parseTime(time);
        timeElement.attr("datetime", dateTime.toString());
        return timeElement;
    }
}
