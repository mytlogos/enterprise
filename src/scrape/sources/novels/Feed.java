package scrape.sources.novels;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import scrape.sources.Source;

import java.io.IOException;

/**
 *
 */
public class Feed {


    public static boolean hasFeed(Source source) {
        boolean hasFeed;
        String baseUrl;
        try {
            baseUrl = getDocument(source.getUrl()).baseUri();
        } catch (IOException e) {
            return false;
        }

        hasFeed = checkForFeed(baseUrl, Types.ATOM.getAppends(), source);

        if (!hasFeed) {
            hasFeed = checkForFeed(baseUrl, Types.RSS.getAppends(), source);
        }

        return hasFeed;
    }

    // TODO: 05.09.2017 save feed in source
    private static boolean checkForFeed(String baseUrl, String[] appends, Source source) {
        boolean hasFeed = false;
        for (String s : appends) {
            try {
                if (getDocument(baseUrl + s).hasText()) {
                    hasFeed = true;
                    break;
                }
            } catch (IOException ignored) {
            }
        }
        return hasFeed;
    }

    private static Document getDocument(String uri) throws IOException {
        return Jsoup.connect(uri).get();
    }

    private enum Types {
        RSS {
            @Override
            String[] getAppends() {
                return RSS;
            }

            private final String[] RSS = new String[]{
                    "/feed",
                    "rss/chapters/all",
                    "/rss",
                    "rss.php"
            };
        },
        ATOM {
            @Override
            String[] getAppends() {
                return ATOM;
            }

            private final String[] ATOM = new String[]{"/feeds/posts/default"};
        };

        abstract String[] getAppends();
    }


}
