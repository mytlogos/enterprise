package scrape;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 */
public class ElementHierarchy {


    public static boolean isBefore(Element node, Element isBefore) {
        Elements nodeParents = node.parents();

        if (ofSamePath(node, isBefore)) {
            return false;
        }

        Element closestCommonParent = null;
        Element closestIsBeforeElement = isBefore;

        for (Element element : isBefore.parents()) {
            if (nodeParents.contains(element)) {
                closestCommonParent = element;
                break;
            }
            closestIsBeforeElement = element;
        }
        if (closestCommonParent == null) {
            throw new IllegalArgumentException("have no common ancestor");
        }

        Element closestNodeElement = node;

        for (Element element : nodeParents) {
            if (element == closestCommonParent) {
                break;
            }
            closestNodeElement = element;
        }

        return closestNodeElement.elementSiblingIndex() > closestIsBeforeElement.elementSiblingIndex();
    }

    public static boolean ofSamePath(Element element, Element other) {
        return element.parents().contains(other) || other.parents().contains(element);
    }

    public static boolean isAfter(Element node, Element isAfter) {
        Elements nodeParents = node.parents();

        if (ofSamePath(node, isAfter)) {
            return false;
        }

        Element closestCommonParent = null;
        Element closestIsBeforeElement = isAfter;

        for (Element element : isAfter.parents()) {
            if (nodeParents.contains(element)) {
                closestCommonParent = element;
                break;
            }
            closestIsBeforeElement = element;
        }
        if (closestCommonParent == null) {
            throw new IllegalArgumentException("have no common ancestor");
        }

        Element closestNodeElement = node;

        for (Element element : nodeParents) {
            if (element == closestCommonParent) {
                break;
            }
            closestNodeElement = element;
        }

        return closestNodeElement.elementSiblingIndex() < closestIsBeforeElement.elementSiblingIndex();
    }

    public static boolean isChild(Element parent, Element child) {
        return child.parents().contains(parent);
    }
}
