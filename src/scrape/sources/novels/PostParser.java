package scrape.sources.novels;

import Enterprise.gui.general.PostList;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.Post;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * This class parses {@link Elements} to {@link Post}.
 */
class PostParser {

    List<Post> toPosts(Elements postElements) {
        List<Post> posts = new PostList();
        for (Element postElement : postElements) {
            Post post = new Post();

            setPostTitle(post, postElement);
            setPostContent(post, postElement);
            setPostFooter(post,postElement);
            setPostTime(post, postElement);

            posts.add(post);
        }
        return posts;
    }

    private void setPostTitle(Post post, Element postElement) {
        post.setTitle(postElement.getElementsByClass("entry-title").text());
    }

    private void setPostContent(Post post, Element postElement) {
        Elements elements = postElement.getElementsByClass("entry-content");

        if (elements.isEmpty()) {
            elements = postElement.getElementsByClass("entry-summary");
        }
        for (Element element : elements) {
            Elements elements1 = element.getElementsByTag("p");
            for (Element element1 : elements1) {
                post.getContent().add(element1.text());
            }
        }
    }

    private void setPostFooter(Post post, Element postElement) {
        Elements elements1 = postElement.getElementsByClass("entry-footer");
        if (elements1.isEmpty()) {
            elements1 = postElement.getElementsByTag("footer");
        }
        if (elements1.isEmpty()) {
            elements1 = postElement.getElementsByClass("entry-meta");
        }
        if (elements1.isEmpty()) {
            elements1 = postElement.getElementsByClass("postmeta");
        }
        post.setFooter(elements1.text());
    }

    private void setPostTime(Post post, Element element) {
        LocalDateTime dateTime = getTime(element);
        if (dateTime != null) {
            post.setTimeStamp(getTime(element));
        }
    }

    private LocalDateTime getTime(Element elements) {
        LocalDateTime localDateTime;

        Elements updatedTimes = getTimeByUpdatedClass(elements);
        Elements publishedTimes = getTimeByPublishedClass(elements);

        Elements attributeTimes = new Elements();

        if (publishedTimes.isEmpty() && updatedTimes.isEmpty()) {
            System.out.println("ist leer");
            attributeTimes = getTimeByAttribute(elements);
        }

        if (attributeTimes.isEmpty()) {

            if (!(publishedTimes.isEmpty() && updatedTimes.isEmpty())) {

                LocalDateTime localUpdated = getLocalDateTime(updatedTimes);
                LocalDateTime localPublished = getLocalDateTime(publishedTimes);

                if (localUpdated == null || localPublished.isBefore(localUpdated)) {
                    if (localUpdated == null || localUpdated.isAfter(LocalDateTime.now())) {
                        localDateTime = localPublished;
                    } else {
                        localDateTime = localUpdated;
                    }
                } else {
                    localDateTime = localUpdated;
                }
            } else if (publishedTimes.isEmpty()) {
                localDateTime = getLocalDateTime(updatedTimes);
            } else {
                localDateTime = getLocalDateTime(publishedTimes);
            }
        } else {
            localDateTime = getLocalDateTime(attributeTimes);
        }

        if (localDateTime.isAfter(LocalDateTime.now())) {
            localDateTime = localDateTime.minusHours(8);
        }

        System.out.println(localDateTime);
        return localDateTime;
    }

    private final String[] updateClasses = new String[]{
            "updated",
            "entry-date updated",
    };
    private final String[] publishedClasses = new String[]{
            "entry-date published updated",
            "entry-date published",
            "onDate date published",
            "entry-date",
            "timeago",
            "time published updated",
            "entry-meta"
    };


    private Elements getTimeByUpdatedClass(Element element) {
        Elements timeElements = new Elements();

        for (String timeClass : updateClasses) {
            if (timeElements.isEmpty()) {
                timeElements.addAll(element.getElementsByClass(timeClass));
            } else {
                break;
            }
        }
        return timeElements;
    }

    private Elements getTimeByPublishedClass(Element element) {
        Elements timeElements = new Elements();

        for (String timeClass : publishedClasses) {
            if (timeElements.isEmpty()) {
                timeElements.addAll(element.getElementsByClass(timeClass));
            } else {
                break;
            }
        }
        return timeElements;
    }

    private Elements getTimeByAttribute(Element elements) {
        Elements timeElements = new Elements();

        timeElements.addAll(elements.getElementsByAttribute("data-timestamp"));

        return timeElements;
    }

    private LocalDateTime getLocalDateTime(Elements elements) {

        LocalDateTime localDateTime;

        String time = elements.attr("datetime");

        if (time.isEmpty()) {
            time = elements.attr("data-timestamp");
        }

        if (time.isEmpty()) {
            time = elements.text();
        }

        localDateTime = parseTime(time);

        return localDateTime;
    }

    private LocalDateTime parseTime(String time) {
        ZonedDateTime zonedDateTime;
        LocalDateTime dateTime = null;
        String finalTime = time;
        if (!time.isEmpty()) {
            if (time.contains("UTC ")) {

                time = time.replace("UTC ", "GMT");

                DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("MMMM d, yyyy 'at' hh:mm a '('O')'")
                        .toFormatter(Locale.US);

                zonedDateTime = ZonedDateTime.parse(time, fmt);
                dateTime = LocalDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.systemDefault());

            } else if (Arrays.stream(DayOfWeek.values())
                    .anyMatch(dayOfWeek -> Pattern.compile(Pattern.quote(dayOfWeek.toString()), Pattern.CASE_INSENSITIVE)
                            .matcher(finalTime).find())) {

                LocalDate date = LocalDate.parse(time, DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.US));
                dateTime = date.atTime(LocalTime.now());

            } else if (Arrays.stream(Month.values())
                    .anyMatch(month -> Pattern.compile(Pattern.quote(month.toString()), Pattern.CASE_INSENSITIVE)
                            .matcher(finalTime).find())) {
                LocalDate date = LocalDate.parse(time, DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US));
                dateTime = date.atTime(LocalTime.now());

            } else if (time.contains(".0000000")) {
                time = time.concat("+00");
                ZonedDateTime gmtDateTime = ZonedDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX"));
                dateTime = LocalDateTime.ofInstant(gmtDateTime.toInstant(), ZoneId.systemDefault());

            } else if (time.contains("T")) {
                zonedDateTime = ZonedDateTime.parse(time);
                dateTime = LocalDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.systemDefault());
            } else {
                time = time.concat("+00");
                ZonedDateTime gmtDateTime = ZonedDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mmX"));
                dateTime = LocalDateTime.ofInstant(gmtDateTime.toInstant(), ZoneId.systemDefault());
            }
        }
        return dateTime;
    }
}
