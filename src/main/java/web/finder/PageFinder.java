package web.finder;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;
import scrape.pages.PageConfig;
import web.PreProcessor;
import web.scorer.NavigationScorer;
import web.scorer.Scorer;

import java.util.*;

/**
 *
 */
public class PageFinder extends AbstractFinder {
    private static String negativeSelector = "footer,[class~=footer],[id~=footer],:contains(copyright)";
    private final PageConfig pageConfig;

    public PageFinder(Document document, PageConfig pageConfig) {
        super(document);
        this.pageConfig = pageConfig;
    }

    private static String toString(Element element) {
        return element.tagName() + element.attributes();
    }

    @Override
    public String find() {
        String selector = new NavigationFinder(start.clone()).find();
        System.out.println(selector);
        return null;
    }

    private static class NavigationFinder extends AbstractFinder {
        private static Scorer scorer = new NavigationScorer();

        NavigationFinder(Document document) {
            super(document);
        }

        NavigationFinder(Element body) {
            super(body);
        }

        public NavigationFinder(Element body, PreProcessor preProcessor) {
            super(body, preProcessor);
        }

        NavigationFinder(PreProcessor preProcessor, Document document) {
            super(preProcessor, document);
        }

        private class NavigationVisitor implements NodeVisitor {
            private Map<Element, ScoreElement> scoreMap = new HashMap<>();

            @Override
            public void head(Node node, int depth) {
                if (node instanceof Element) {
                    Element element = (Element) node;

                    if (!isStructureBlock(element)) {
                        scoreMap.put(element, new ScoreElement(element, Integer.MIN_VALUE));
                        return;
                    }

                    int score = getScore(element, depth);
                    scoreMap.put(element, new ScoreElement(element, score));
                }
            }

            private int getScore(Element element, int depth) {
                int score = 0;

                int linksSize = element.select("a[href]").size();

                if (linksSize <= 1) {
                    return Integer.MIN_VALUE;
                }

                if (linksSize >= 20) {
                    int linksScore = linksSize / 20;
                    score += Math.max(linksScore, 20);
                } else {
                    score += 5;
                }

                long blockElementsSize = element.getAllElements().stream().filter(Element::isBlock).count();
                double complexity = ((double) linksSize) / ((double) blockElementsSize);
                score += Math.max((complexity * 5), 20);

                int relSiblingIndex = 1;

                Elements siblingElements = element.siblingElements();

                for (int i = 0, siblingElementsSize = siblingElements.size(); i < siblingElementsSize; i++) {
                    Element sibling = siblingElements.get(i);
                    if (sibling == element) {
                        relSiblingIndex = i;
                        break;
                    } else if (scoreMap.containsKey(sibling)) {
                        relSiblingIndex = element.elementSiblingIndex() - sibling.elementSiblingIndex();
                        break;
                    }
                }

                double depthIndexScore = (((double) relSiblingIndex) / depth) * 10;
                score -= Math.min(depthIndexScore, 20);
                String txt = element.text().trim();
                String[] words = txt.split("[\\s.!?]");

                long wordCount = Arrays.stream(words).filter(s -> !s.isEmpty()).count();

                long linkWordRatio = wordCount / linksSize;
                System.out.println(PageFinder.toString(element) + " ratio " + linkWordRatio);
                score -= Math.min(linkWordRatio, 20);
                return score;
            }

            @Override
            public void tail(Node node, int depth) {
                if (node instanceof Element) {
                    Element element = (Element) node;

                    boolean similar = true;
                    int parentScore = scoreMap.get(element).score;

                    if (parentScore == Integer.MIN_VALUE) {
                        return;
                    }

                    for (Element child : element.children()) {
                        int score = scoreMap.get(child).score;
                        if (((double) score) / parentScore < 0.75) {
                            similar = false;
                            break;
                        }
                    }
                    if (similar) {
                        for (Element child : element.children()) {
                            ScoreElement scoreElement = scoreMap.get(child);

                            if (scoreElement.score < parentScore) {
                                if (parentScore < 0) {
                                    scoreElement.score = Integer.MIN_VALUE;
                                } else {
                                    scoreElement.score = 0;
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public String find() {
            NavigationVisitor visitor = new NavigationVisitor();
            start.traverse(visitor);

            List<ScoreElement> scoreElements = new ArrayList<>(visitor.scoreMap.values());

            Comparator<ScoreElement> comparator = Comparator.comparingInt(value -> value.score);
            scoreElements.sort(comparator.reversed());
            scoreElements.removeIf((entry -> Integer.MIN_VALUE == entry.score));

            scoreElements.forEach(System.out::println);
            return scoreElements.isEmpty() ? null : scoreElements.get(0).element.cssSelector();
        }
    }

    private static class ScoreElement {
        private Element element;
        private int score;

        private ScoreElement(Element element, int score) {
            this.element = element;
            this.score = score;
        }

        @Override
        public String toString() {
            return "ScoreElement{" +
                    "element=" + PageFinder.toString(element) +
                    ", scoreHead=" + score +
                    '}';
        }
    }
}
