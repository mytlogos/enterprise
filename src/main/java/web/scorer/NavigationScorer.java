package web.scorer;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class NavigationScorer extends StandardScorer {

    public NavigationScorer() {
        //positive characteristics
        addAttributeValue(true, "class", "head", "nav", "main-nav");
        addAttributeValue(true, "id", "head", "nav", "main-nav");
        addAttributeValue(true, "role", "nav");
        addTags(true, "header", "nav");

        addText(true,
                "home", "project", "teaser",
                "anime", "manga", "novel", "list", "ongoing",
                "completed", "dropped", "finished", "translation",
                "series", "about");

        addParentBonusAttributeValue(true, "class", "menu-item", "sub-menu", "collapsed", "dropdown", "child");
        addParentBonusAttributeValue(true, "href", "project", "novel");
        //negative characteristics
        addFastFailTag("head", "main", "html", "body", "td", "tr", "th", "tbody", "table", "article", "footer");
        addTags(false, "li");

        final String[] negAttrValues = {
                "main", "content", "foot", "side",
                "btn", "page", "post", "article",
                "pager", "entry", "login", "archive",
                "credit", "copyright", "pagination", "pager",
                "subscri", "meta", "cookie", "follow",
                "next", "previous", "category", "likes",
                "facebook", "twitter", "disqus", "discord",
                "colophon", "widget", "secondary", "tertiary",
                "recent", "social", "categor", "powered by",
                "patreon"
        };

        addAttributeValue(false, "class", negAttrValues);
        addAttributeValue(false, "id", negAttrValues);
        addText(false, "copyright", "facebook", "twitter", "discord",
                "disqus", "post", "recent", "patreon",
                "categor"
        );
    }

    public NavigationScorer(Scorer scorer) {
        super(scorer);
    }

    @Override
    public String getScoreKey() {
        return "navigation";
    }

    @Override
    public boolean scoreHead(ElementWrapper elementWrapper, ScorerValue value) {
        final String uri = elementWrapper.getElement().baseUri();
        final String host = getHost(uri);
        addText(true, host);

        if (super.scoreHead(elementWrapper, value)) {
            return true;
        }

        for (Attribute attribute : elementWrapper.getElement().attributes()) {
            String key = attribute.getKey();
            final String attributeValue = attribute.getValue();
            if (key.contains("hidden") || key.contains("expanded") || (attributeValue != null && attributeValue.contains("hidden"))) {
                value.addNonStackBoni(-1);
            }
        }

        int wordScore = getWordScore(elementWrapper);
        int wordDensityScore = getWordDensityScore(elementWrapper);
        int linkPercScore = getLinkPercScore(elementWrapper);
        double linkScore = getLinkScore(elementWrapper);
        int textNodeScore = getTextNodeScore(elementWrapper);
        int linkTextNodeScore = getLinkTextNodeScore(elementWrapper);

        double uniqueness = 3 / elementWrapper.getUniqueness();

        value.addNonStackBoni(linkTextNodeScore);
        value.addNonStackBoni(textNodeScore);
        value.addNonStackBoni(uniqueness);
        value.addNonStackBoni(linkPercScore);
        value.addNonStackBoni(wordScore);
        value.addNonStackBoni(wordDensityScore);
        value.addNonStackBoni(linkScore);

        return false;
    }

    private String getHost(String s) {
        Matcher matcher = Pattern.compile("https?://(\\w{0,3}\\.)?([\\w-]+)\\..+").matcher(s);
        if (matcher.find()) {
            return matcher.group(2);
        } else {
            throw new IllegalStateException("no host found for " + s);
        }
    }

    private int getWordScore(ElementWrapper elementWrapper) {
        int wordScore = 0;
        if (elementWrapper.getWords() <= 1) {
            wordScore = -5;
        } else {
            int rootWords = elementWrapper.getRoot().getWords();

            double percLimit = rootWords > 500 ? rootWords > 1000 ? 0.1 : 0.2 : 0.4;

            final double wordPercentage = elementWrapper.getWordPercentage();
            if (wordPercentage < percLimit) {
                wordScore++;
            } else if (wordPercentage > 0.9) {
                wordScore = -15;
            } else if (wordPercentage > 0.98) {
                wordScore = -30;
            } else {
                wordScore = -5;
            }
        }
        return wordScore;
    }

    private int getWordDensityScore(ElementWrapper elementWrapper) {
        return elementWrapper.getWordDensity() < 10 ? 1 : -2;
    }

    private int getLinkPercScore(ElementWrapper elementWrapper) {
        double linkPercentage = elementWrapper.getLinkPercentage();
        //if less then 0.5 % of links, then punish, if less then 50% reward, else punish
        return linkPercentage < 0.005 ? -10 : linkPercentage < 0.5 ? 1 : linkPercentage < 0.9 ? -5 : -20;
    }

    private double getLinkScore(ElementWrapper elementWrapper) {
        int linkSize = elementWrapper.linkSize();

        double linkScore;

        if (linkSize <= 1) {
            linkScore = -10;
        } else if (linkSize < 5) {
            linkScore = 1;
        } else if (linkSize < 50) {
            linkScore = evaluateLinkHost(elementWrapper.getElement(), 4);
        } else {
            linkScore = evaluateLinkHost(elementWrapper.getElement(), 6);
        }
        return linkScore;
    }

    private int getTextNodeScore(ElementWrapper elementWrapper) {
        final int overLimitNodes = elementWrapper.getOverLimitNodes();
        final int textNodeSize = elementWrapper.getTextNodeSize();

        //if over ten percent of textNodes are over the set limit, subtract 5, else add 1
        final double overLimitPerc = overLimitNodes / ((double) textNodeSize);
        return overLimitPerc > 0.5 ? -10 : overLimitPerc > 0.1 ? -5 : 1;
    }

    private int getLinkTextNodeScore(ElementWrapper wrapper) {
        final double linkTextNodeDensity = wrapper.getLinkTextNodeDensity();
        return linkTextNodeDensity < 0.5 ? -2 : 1;
    }

    private int evaluateLinkHost(Element element, int bonus) {
        double count = 0;
        final Elements elements = element.select("a[href]");

        for (Element linkElement : elements) {
            final String url = linkElement.absUrl("href");

            if (validLink(url) && !getHost(url).equals(getHost(element.baseUri()))) {
                count++;
            }
        }
        double perc = count / elements.size();

        if (perc > 0.5) {
            return -5;
        }

        return (int) (bonus * (1 - perc));
    }

    private boolean validLink(String link) {
        return Pattern.compile("https?://(\\w{0,3}\\.)?([\\w-]+)\\..+").matcher(link).find();
    }

    @Override
    public void scoreTail(ElementWrapper elementWrapper, ScorerValue value) {
        super.scoreTail(elementWrapper, value);
        //todo not sure if i really want to use this
/*
        if (elementWrapper.getElement().children().size() == 1 && elementWrapper.childNodeSize() == 1) {
            final ElementWrapper onlyChild = elementWrapper.childNode(0);

            //remove if child has same text as parent but lower score
            if (onlyChild.getElement().text().equals(elementWrapper.getElement().text())
                    && onlyChild.getScorerValue(this).getScore() < value.getScore()) {

                System.out.println("removed " + onlyChild.getElementName());
                elementWrapper.removeChild(onlyChild);
                elementWrapper.appendChildren(onlyChild.getChildren());
            }
        }*/
    }
}
