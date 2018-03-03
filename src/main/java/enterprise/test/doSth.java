package enterprise.test;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDateTime;

/**
 *
 */
public class doSth {
    public static void main(String[] args) {
        Elements elements = new Elements();
        for (int i = 12; i != 1; i--) {
            for (int j = 1; j < 5; j++) {
                LocalDateTime localDateTime = LocalDateTime.of(2000, i, j, 0, 0);
                Element element = new Element("time");
                element.attr("datetime", localDateTime.toString());
                elements.add(element);
            }
        }
        System.out.println(elements);
    }


}
