package scrape.sources;

import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 */
public class SourceAccessor {
    private static Map<String, Sleeper> map = Collections.synchronizedMap(new HashMap<>());

    public static Document getDocument(String uri) throws IOException {
        return getConnection(uri).get();
    }

    public static Connection getConnection(String uri) {
        checkHost(uri);
        return Jsoup.connect(uri);
    }

    private static void checkHost(String uri) {
        try {
            URI link = new URI(uri);
            String host = link.getHost();

            if (host == null) {
                throw new IllegalArgumentException("host shall never be null: " + uri);
            }
            map.computeIfAbsent(host, Sleeper::new).waitOn();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static org.jdom2.Document getXml(String link) throws IOException, JDOMException {
        Map<String, String> headers = new HashMap<>();

        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
        headers.put("Accept-Language", "de,en-US;q=0.7,en;q=0.3");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Connection", "keep-alive");
        headers.put("Pragma", "no-cache");
        headers.put("Cache-Control", "no-cache");

        final Connection connect = getConnection(link);

        for (String s : headers.keySet()) {
            connect.header(s, headers.get(s));
        }
        final String body = connect.ignoreContentType(true).execute().body();

        //new BufferedReader(new StringReader(body)).readLine() vs body.split("\n")[0]
        if (body.isEmpty() || !new BufferedReader(new StringReader(body)).readLine().contains("xml")) {
            return null;
        }

        return new SAXBuilder().
                build(new StringReader(body));
    }

    public static String getJson(String link) throws IOException {
        Map<String, String> headers = new HashMap<>();

        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
        headers.put("Accept-Language", "de,en-US;q=0.7,en;q=0.3");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Connection", "keep-alive");
        headers.put("Pragma", "no-cache");
        headers.put("Cache-Control", "no-cache");

        final Connection connect = getConnection(link);

        for (String s : headers.keySet()) {
            connect.header(s, headers.get(s));
        }
        return connect.ignoreContentType(true).execute().body();
    }

    public static Connection.Response getResponse(String uri) throws IOException {
        return getConnection(uri).ignoreContentType(true).maxBodySize(0).execute();
    }

    private static class Sleeper {

        private final String host;
        private final Random random;

        Sleeper(String host) {
            this.host = host;
            random = new Random();
        }

        @Override
        public int hashCode() {
            return host.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Sleeper sleeper = (Sleeper) o;

            return host.equals(sleeper.host);
        }

        @Override
        public String toString() {
            return "Sleeper{" +
                    "host='" + host + '\'' +
                    '}';
        }

        private synchronized void waitOn() {
            try {
                int addend = random.nextInt(15);
                Thread.sleep(10 + addend);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
