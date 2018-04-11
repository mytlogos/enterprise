package web.finder;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import web.scorer.ElementWrapper;
import web.scorer.Scorer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Visitor which visits each {@link ElementWrapper}
 * in a tree.
 * <p>
 * Scores the uniqueness of the {@link ElementWrapper#element} and let
 * all set {@link Scorer} set a Score Value in each ElementWrapper in
 * a head and tail visit each.
 * <p>
 * Sets basic Scores, e.g. link density, word count etc, which are then scored
 * according to the given {@link Scorer}.
 * <p>
 */
class ScoreVisitor implements Visitor<ElementWrapper> {
    private final List<Scorer> scorers;
    private Map<String, List<Element>> classNamesMap;


    ScoreVisitor(List<Scorer> scorers, Map<String, List<Element>> classNamesMap) {
        this.scorers = scorers;
        this.classNamesMap = classNamesMap;
    }

    @Override
    public void head(ElementWrapper elementWrapper, int depth) {
        setHeadCharacteristics(elementWrapper);
        this.scorers.forEach(elementWrapper::setHeadScore);
    }

    @Override
    public void tail(ElementWrapper elementWrapper, int depth) {
        setTailCharacteristics(elementWrapper);
        this.scorers.forEach(elementWrapper::setTailScore);
    }

    private void setTailCharacteristics(ElementWrapper elementWrapper) {
        int index = elementWrapper.getSiblingIndex();

        int depth = elementWrapper.parents().size();
        double depthIndexScore;

        if (depth == 0) {
            depthIndexScore = 0;
        } else {
            double addend = index - depth;
            depthIndexScore = addend * addend;
        }
        elementWrapper.setDepthIndexScore(Math.log10(depthIndexScore));
    }

    private void setHeadCharacteristics(ElementWrapper elementWrapper) {
        Element element = elementWrapper.getElement();

        String txt = element.text().trim();
        long wordCount = txt.split("[\\W+]").length;

        List<TextNode> textNodes = new ArrayList<>();
        final int overLimitNodes = collectTextNodes(element, textNodes);

        double elementsSize = element.getAllElements().size();

        //number of links
        int linksSize = element.select("a[href]").size();

        //the root of this search tree
        final ElementWrapper root = elementWrapper.getRoot();

        //percentage of links of the parameter
        double linkPercentage = root == elementWrapper ? 1 : ((double) linksSize) / root.linkSize();

        //density of links vs elementSize
        double linkElementDensity = linksSize / elementsSize;

        //density of links vs words
        double linkWordDensity = linksSize / ((double) wordCount);

        //density of links vs textNode
        double linkTextNodeDensity = linksSize / ((double) textNodes.size());

        //words per textNode
        double wordDensity = wordCount / ((double) textNodes.size());

        //words percentage vs the root of this search tree
        double wordPercentage = root == elementWrapper ? 1 : wordCount / ((double) root.getWords());

        double uniqueness = getUniqueness(elementWrapper.getElement());

        elementWrapper.setTextNodeSize(textNodes.size());
        elementWrapper.setOverLimitTextNodes(overLimitNodes);
        elementWrapper.setLinkTextDensity(linkTextNodeDensity);
        elementWrapper.setLinkPercentage(linkPercentage);
        elementWrapper.setUniqueness(uniqueness);
        elementWrapper.setLinkSize(linksSize);
        elementWrapper.setWordPercentage(wordPercentage);
        elementWrapper.setWordDensity(wordDensity);
        elementWrapper.setLinkElementDensity(linkElementDensity);
        elementWrapper.setLinkWordDensity(linkWordDensity);
        elementWrapper.setWords((int) wordCount);
    }

    private int collectTextNodes(Element element, List<TextNode> textNodes) {
        List<TextNode> text = new ArrayList<>(element.textNodes());

        int overLimitNode = 0;

        for (Iterator<TextNode> iterator = text.iterator(); iterator.hasNext(); ) {
            TextNode textNode = iterator.next();

            if (textNode.text().length() > 150) {
                overLimitNode++;
            } else if (textNode.isBlank()) {
                iterator.remove();
            }
        }
        textNodes.addAll(text);


        for (Element child : element.children()) {
            final int limitNodes = collectTextNodes(child, textNodes);
            overLimitNode += limitNodes;
        }
        return overLimitNode;
    }

    private double getUniqueness(Element element) {
        String className = element.className();
        List<Element> list = classNamesMap.get(className);

        if (list == null) {
            throw new IllegalStateException("unvisited node detected " + element.cssSelector());
        }

        List<String> similar = getSimilarElements(className);
        double uniqueness = 0;

        String tagName = element.tagName();
        String id = element.id();

        for (String similarClassName : similar) {
            List<Element> elements = classNamesMap.get(similarClassName);
            uniqueness = similarity(uniqueness, 1, tagName, id, elements, element);
        }

        if (className.isEmpty() && id.isEmpty()) {
            uniqueness = similarity(uniqueness, 0.5, tagName, id, list, element);
        } else {
            uniqueness = similarity(uniqueness, 2, tagName, id, list, element);
        }

        return uniqueness;
    }

    private List<String> getSimilarElements(String className) {
        if (className.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> names = Arrays.asList(className.split("\\s"));

        Pattern pattern = buildPattern(names);
        int similarityThreshold = names.size() - (names.size() / 3);

        List<String> similar = new ArrayList<>();

        for (String name : classNamesMap.keySet()) {
            if (name.equals(className)) {
                continue;
            }

            Matcher matcher = pattern.matcher(name);

            double classSimilarity = 0;

            while (matcher.find()) {
                String group = matcher.group();

                if (group.charAt(group.length() - 1) == '-') {
                    classSimilarity = 0.25;
                } else {
                    classSimilarity++;
                }
            }

            if (classSimilarity >= similarityThreshold) {
                similar.add(name);
            }
        }
        return similar;
    }

    private double similarity(double uniqueness, double multiplier, String tagName, String id, List<Element> elements, Element element) {
        for (Element similarElement : elements) {
            String similarId = similarElement.id();

            if (id.contains(similarId) || similarId.contains(id)) {
                uniqueness += (0.25 * multiplier);
            } else {
                uniqueness -= (0.25 * multiplier);
            }

            if (similarElement.className().isEmpty()) {
                continue;
            }

            if (tagName.equals(similarElement.tagName())) {
                uniqueness += (0.25 * multiplier);
            } else {
                uniqueness += (0.125 * multiplier);
            }
            uniqueness += distance(element, similarElement);
        }
        return uniqueness;
    }

    private Pattern buildPattern(List<String> names) {
        StringBuilder patternBuilder = new StringBuilder();
        for (Iterator<String> iterator = names.iterator(); iterator.hasNext(); ) {
            String name = iterator.next();

            if (name.isEmpty()) {
                continue;
            }
            int indexOf;

            if ((indexOf = name.indexOf("-")) != -1) {
                patternBuilder.append(name, 0, indexOf + 1).append("|");
            }
            patternBuilder.append(name);

            if (iterator.hasNext()) {
                patternBuilder.append("|");
            }
        }

        return Pattern.compile(patternBuilder.toString());
    }

    /**
     * Calculates the distance between two Elements as a value
     * between 0 and 1;
     *
     * @param first  element to calculate the distance from
     * @param second element to calculate the distance from
     * @return a value between 0 and 1;
     */
    private double distance(Element first, Element second) {
        if (first == second) {
            return 0;
        }

        Elements secondParents = second.parents();
        Element sameParent = null;

        int firstParentDistance = 0;
        int secondParentDistance = 0;

        Element previousFirstParent = null;
        Elements firstParents = first.parents();

        for (ListIterator<Element> iterator = firstParents.listIterator(); iterator.hasNext(); ) {
            Element parent = iterator.next();

            if ((secondParentDistance = secondParents.indexOf(parent)) != -1) {
                sameParent = parent;
                firstParentDistance = iterator.previousIndex();
                previousFirstParent = iterator.hasPrevious() ? iterator.previous() : second;
                break;
            }
        }

        if (sameParent == null) {
            throw new IllegalStateException("arguments have no common parents!");
        }

        Element previousSecondParent;

        if (secondParentDistance == 0) {
            previousSecondParent = second;
        } else {
            previousSecondParent = secondParents.get(secondParentDistance - 1);
        }

        int parentSiblingDistance = Math.abs(previousFirstParent.elementSiblingIndex() - previousSecondParent.elementSiblingIndex());
        return satedDistance(firstParentDistance, secondParentDistance, parentSiblingDistance);
    }

    private double satedDistance(int firstDistance, int secondDistance, int parentSiblingDistance) {
        int saturation = 1;
        double exponentFactor = parentSiblingDistance == 0 ? 0.01 : (firstDistance + secondDistance) / parentSiblingDistance;

        double expPart = Math.exp(exponentFactor);
        double portion = 1 / expPart;
        portion = 1 - portion;
        return portion * saturation;
    }
}
