package web.finder;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;
import web.scorer.ElementWrapper;
import web.scorer.Scorer;
import web.scorer.ScorerValue;

import java.util.*;

/**
 *
 */
public class ScorerUser {
    private static final List<String> structureBlockTags = Arrays.asList(
            "title", "section", "nav", "aside", "hgroup", "header", "footer",
            "pre", "div", "figure", "ul", "ol", "li", "body",
            "table", "caption", "thead", "tfoot", "tbody", "colgroup", "col", "tr", "th",
            "td", "details", "plaintext", "template", "article", "main"
    );
    private Element start;
    private List<Scorer> scorers = new ArrayList<>();
    private ElementWrapper root;
    private Map<String, List<Element>> classNamesMap = new TreeMap<>();
    private Map<String, TreeMap<Double, List<ElementWrapper>>> scoredMaps = new HashMap<>();
    private boolean maxScorerVisitor;

    public ScorerUser(Element start) {
        this.start = start;
        setMaxScorerVisitor(true);
    }

    public void setMaxScorerVisitor(boolean use) {
        maxScorerVisitor = use;
    }

    public void execute() {
        if (root == null) {
            classNamesMap.clear();
            buildTree();
        }
        traverse(new ScoreVisitor(scorers, classNamesMap), root);
        fillScoredMaps();
        scorers.clear();
    }

    private void buildTree() {
        BuildVisitor buildVisitor = new BuildVisitor(classNamesMap);
        if (start instanceof Document) {
            ((Document) start).body().traverse(buildVisitor);
        } else {
            start.traverse(buildVisitor);
        }
        buildVisitor.removeLeftOver();
    }

    /**
     * Copied from {@link org.jsoup.select.NodeTraversor#traverse(NodeVisitor, Node)}.
     *
     * @param visitor visitor to visit each ElementWrapper
     * @param root    starting point
     */
    private void traverse(Visitor<ElementWrapper> visitor, ElementWrapper root) {
        ElementWrapper node = root;
        int depth = 0;

        while (node != null) {
            visitor.head(node, depth);
            if (node.childNodeSize() > 0) {
                node = node.childNode(0);
                depth++;
            } else {
                while (node.nextSibling() == null && depth > 0) {
                    visitor.tail(node, depth);
                    node = node.parent();
                    depth--;
                }
                visitor.tail(node, depth);
                if (node == root)
                    break;
                node = node.nextSibling();
            }
        }
    }

    private void fillScoredMaps() {
        scoredMaps.clear();
        Visitor<ElementWrapper> visitor = maxScorerVisitor ? maxScorerVisitor() : allScorerVisitor();
        traverse(visitor, root);
    }

    private Visitor<ElementWrapper> maxScorerVisitor() {
        return new Visitor<ElementWrapper>() {
            @Override
            public void head(ElementWrapper elementWrapper, int depth) {
                ScorerValue value = elementWrapper.getMaxScore();

                if (value != null) {
                    double score = value.getScore();
                    scoredMaps.
                            computeIfAbsent(value.getScorerKey(), k -> new TreeMap<>()).
                            computeIfAbsent(score, k -> new ArrayList<>()).
                            add(elementWrapper);
                }

            }

            @Override
            public void tail(ElementWrapper elementWrapper, int depth) {

            }
        };
    }

    private Visitor<ElementWrapper> allScorerVisitor() {
        return new Visitor<ElementWrapper>() {
            @Override
            public void head(ElementWrapper elementWrapper, int depth) {
                for (Scorer scorer : scorers) {
                    ScorerValue value = elementWrapper.getScorerValue(scorer);

                    if (value != null) {
                        double score = value.getScore();
                        scoredMaps.
                                computeIfAbsent(scorer.getScoreKey(), k -> new TreeMap<>()).
                                computeIfAbsent(score, k -> new ArrayList<>()).
                                add(elementWrapper);
                    }
                }

            }

            @Override
            public void tail(ElementWrapper elementWrapper, int depth) {

            }
        };
    }

    public void execute(Element start) {

    }

    public List<ElementWrapper> wrappers() {
        List<ElementWrapper> wrappers = new ArrayList<>();
        traverse(new Visitor<ElementWrapper>() {
            @Override
            public void head(ElementWrapper wrapper, int depth) {
                wrappers.add(wrapper);
            }

            @Override
            public void tail(ElementWrapper wrapper, int depth) {

            }
        }, root);
        return wrappers;
    }

    public ScorerUser addScorers(Scorer... scorers) {
        this.scorers.addAll(Arrays.asList(scorers));
        return this;
    }

    public ElementWrapper getRoot() {
        return root;
    }

    public Map<String, TreeMap<Double, List<ElementWrapper>>> getScoredMaps() {
        return scoredMaps;
    }

    private void print() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, TreeMap<Double, List<ElementWrapper>>> entry : scoredMaps.entrySet()) {

            TreeMap<Double, List<ElementWrapper>> map = entry.getValue();

            NavigableSet<Double> doubles = map.descendingKeySet();

            StringBuilder mapBuilder = new StringBuilder();

            for (Double score : doubles) {
                List<ElementWrapper> elementWrappers = map.get(score);

                for (ElementWrapper elementWrapper : elementWrappers) {
                    if (elementWrapper.getUniqueness() < 10) {
                        append(elementWrapper, score, mapBuilder, entry.getKey());
                    }
                }
            }
            mapBuilder.insert(0, "\n");
            mapBuilder.insert(0, String.format("%10s | %100s | %10s | %10s | %10s | %10s | %10s | %10s | %10s | %10s | %10s | %10s", "Worth", "Element Identifier", "Weight", "Words", "LinkPer", "LinkED", "LinkWD", "LinkTND", "WordD", "DepthS", "WordP", "Unique") + "\n");


            mapBuilder.insert(0, "\n\n");
            for (int i = 0; i < 100; i++) {
                mapBuilder.insert(0, "*");
            }
            mapBuilder.insert(0, "\n").insert(0, entry.getKey()).insert(0, "\n");

            stringBuilder.append(mapBuilder);
        }

        System.out.println(stringBuilder);
    }

    private void append(ElementWrapper elementWrapper, Double score, StringBuilder stringBuilder, String key) {
        Element element = elementWrapper.getElement();
        String row = String.format("%10f | %100s | %10f | %10d | %10f | %10f | %10f | %10f | %10f | %10f | %10f | %10f",
                score,
                element.tagName() + element.attributes(),
                elementWrapper.getScorerValue(key).getElementWeight(),
                elementWrapper.getWords(),
                elementWrapper.getLinkPercentage(),
                elementWrapper.getLinkElementDensity(),
                elementWrapper.getLinkWordDensity(),
                elementWrapper.getLinkTextNodeDensity(),
                elementWrapper.getWordDensity(),
                elementWrapper.getDepthIndexScore(),
                elementWrapper.getWordPercentage(),
                elementWrapper.getUniqueness());

        stringBuilder.append(row).append("\n");
    }

    private boolean isStructureBlock(Element element) {
        return structureBlockTags.contains(element.tagName());
    }

    /**
     * Visitor, which visits each {@link Node} of the Jsoup tree of the given root.
     * <p>
     * This Visitor skips all Nodes which are not instance of Element or
     * a block Element per {@link #isStructureBlock(Element)}, because they
     * are not 'interesting' enough to be scored.
     * <p>
     * Elements which are not a {@code StructureBlock} will have
     * their children appended to their parent in the {@link #tail(Node, int)} method.
     */
    private class BuildVisitor implements NodeVisitor {
        private ElementWrapper prevVisited;
        private List<ElementWrapper> removeElementWrapper = new ArrayList<>();
        private Map<String, List<Element>> classNamesMap;

        private BuildVisitor(Map<String, List<Element>> classNamesMap) {
            this.classNamesMap = classNamesMap;
        }

        @Override
        public void head(Node node, int depth) {
            if (node instanceof Element) {
                Element element = (Element) node;

                ElementWrapper elementWrapper = new ElementWrapper(element);

                if (root == null) {
                    root = elementWrapper;
                } else if (prevVisited.getElement() == element.parent()) {
                    prevVisited.appendChild(elementWrapper);
                } else if (prevVisited.getElement().siblingElements().contains(element)) {
                    prevVisited.parent().appendChild(elementWrapper);
                }
                classNamesMap.computeIfAbsent(element.className(), k -> new ArrayList<>()).add(element);
                prevVisited = elementWrapper;
            }
        }

        @Override
        public void tail(Node node, int depth) {
            if (node instanceof Element) {
                Element element = (Element) node;

                Element prevVisitedElement = prevVisited.getElement();

                ElementWrapper current = null;

                if (element == prevVisitedElement) {
                    current = prevVisited;
                } else if (prevVisitedElement.parent() == element) {
                    current = prevVisited.parent();
                } else if (prevVisitedElement.siblingElements().contains(element)) {

                    for (ElementWrapper sibling : prevVisited.siblings()) {
                        if (sibling.getElement() == element) {
                            current = sibling;
                        }
                    }
                }

                if (current == null) {
                    throw new NullPointerException("missing previous visited node");
                }

                if (!isStructureBlock(element)) {
                    ElementWrapper blockParent = current.parent();

                    if (blockParent == null) {
                        throw new IllegalStateException("invalid tree, missing block element root (see 'isStructureBlock')");
                    }
                    blockParent.appendChildren(current.getChildren());
                    removeElementWrapper.add(current);
                }
                prevVisited = current;
            }
        }

        private void removeLeftOver() {
            for (ElementWrapper elementWrapper : removeElementWrapper) {
                elementWrapper.parent().removeChild(elementWrapper);
            }
        }
    }
}
