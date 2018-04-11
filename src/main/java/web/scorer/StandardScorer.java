package web.scorer;

import org.jsoup.nodes.Element;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public abstract class StandardScorer implements Scorer {
    private Map<String, StringBuilder> positiveAttributeValues = new HashMap<>();
    private Map<String, StringBuilder> negativeAttributeValues = new HashMap<>();

    private Map<String, StringBuilder> posParentBoniAttributeValues = new HashMap<>();
    private Map<String, StringBuilder> negParentBoniAttributeValues = new HashMap<>();

    private List<String> positiveTags = new ArrayList<>();
    private List<String> negativeTags = new ArrayList<>();
    private List<String> fastFailTags = new ArrayList<>();

    private StringBuilder positiveTxtPattern = new StringBuilder();
    private StringBuilder negativeTxtPattern = new StringBuilder();


    StandardScorer() {
        String[] strings = {
                "hidden", "print", "comment", "discuss", "share", "reply",
                "login", "sign", "banner", "community", "cover-wrap", "disqus", "extra",
                "foot", "legends", "related", "remark", "replies", "rss",
                "shoutbox", "skyscraper", "social", "sponsor", "supplemental",
                "ad-break", "agegate", "popup", "yom-remote", "contact"
        };

        addAttributeValue(false, "class", strings);
        addAttributeValue(false, "id", strings);
    }

    @Override
    public void addAttributeValue(boolean positive, String attr, String... values) {
        Map<String, StringBuilder> attrValueMap = positive ? positiveAttributeValues : negativeAttributeValues;

        appendAttributeValue(attr, attrValueMap, values);
    }

    @Override
    public void addParentBonusAttributeValue(boolean positive, String attr, String... values) {
        Map<String, StringBuilder> attrValueMap = positive ? posParentBoniAttributeValues : negParentBoniAttributeValues;

        appendAttributeValue(attr, attrValueMap, values);
    }

    @Override
    public void addText(boolean positive, String... texts) {
        StringBuilder neg = positive ? negativeTxtPattern : positiveTxtPattern;
        StringBuilder pos = positive ? positiveTxtPattern : negativeTxtPattern;

        for (String txt : texts) {
            int index = neg.indexOf(txt);

            if (index != -1) {
                neg.delete(index, txt.length());
            }
            if (pos.indexOf(txt) == -1) {
                if (pos.length() != 0) {
                    pos.append("|");
                }
                pos.append(txt);
            }
        }
    }

    @Override
    public void addTags(boolean positive, String... tags) {
        Collection<String> remove = positive ? negativeTags : positiveTags;
        Collection<String> add = positive ? positiveTags : negativeTags;

        for (String tag : tags) {
            remove.remove(tag);

            if (!add.contains(tag)) {
                add.add(tag);
            }
        }
    }

    @Override
    public boolean scoreHead(ElementWrapper elementWrapper, ScorerValue value) {
        Element element = elementWrapper.getElement();

        if (fastFailTags.contains(element.tagName())) {
            value.setElementWeight(-5);
            return true;
        }

        int elementWeight = 0;
        elementWeight += scoreAttr(element, positiveAttributeValues);
        elementWeight -= scoreAttr(element, negativeAttributeValues);
        elementWeight += scoreTag(element.tagName());
        elementWeight += scoreTxt(element.text());

        int parentBonus = scoreAttr(element, posParentBoniAttributeValues);
        parentBonus -= scoreAttr(element, negParentBoniAttributeValues);
        elementWeight -= (parentBonus * 2);

        value.addParentBoni(parentBonus);
        value.setElementWeight(elementWeight);

        return false;
    }

    @Override
    public void scoreTail(ElementWrapper elementWrapper, ScorerValue value) {
        if (elementWrapper.childNodeSize() != 1) {
            for (ElementWrapper child : elementWrapper.getChildren()) {
                ScorerValue score = child.getScorerValue(this);
                value.addChildrenWeight(score);
            }
        }
    }

    @Override
    public void addFastFailTag(String... tags) {
        fastFailTags.addAll(Arrays.asList(tags));
    }

    private int scoreAttr(Element element, Map<String, StringBuilder> map) {
        int score = 0;
        for (Map.Entry<String, StringBuilder> entry : map.entrySet()) {
            String value = element.attr(entry.getKey());
            if (value.isEmpty()) {
                continue;
            }
            if (Pattern.compile(entry.getValue().toString(), Pattern.CASE_INSENSITIVE).matcher(value).find()) {
                score++;
            }
        }
        return score;
    }

    private int scoreTag(String tagName) {
        return positiveTags.contains(tagName) ? 5 : negativeTags.contains(tagName) ? -5 : 0;
    }

    private double scoreTxt(String text) {
        Set<String> foundPos = new HashSet<>();
        Set<String> foundNeg = new HashSet<>();

        if (positiveTxtPattern.length() != 0) {
            Matcher posMatcher = Pattern.compile(positiveTxtPattern.toString(), Pattern.CASE_INSENSITIVE).matcher(text);
            while (posMatcher.find()) {
                foundPos.add(posMatcher.group());
            }
        }
        if (negativeTxtPattern.length() != 0) {
            Matcher negMatcher = Pattern.compile(negativeTxtPattern.toString(), Pattern.CASE_INSENSITIVE).matcher(text);

            while (negMatcher.find()) {
                foundNeg.add(negMatcher.group());
            }
        }
        final int pos = foundPos.size() > 5 ? 5 : foundPos.size();
        final int neg = foundNeg.size() > 5 ? 5 : foundNeg.size();
        return pos - neg;
    }

    private void appendAttributeValue(String attr, Map<String, StringBuilder> attrValueMap, String[] values) {
        StringBuilder builder = attrValueMap.get(attr);

        if (builder == null) {
            builder = new StringBuilder(values[0]);

            for (int i = 1; i < values.length; i++) {
                builder.append("|").append(values[i]);
            }
            attrValueMap.put(attr, builder);
        } else {
            for (String value : values) {
                builder.append("|").append(value);
            }
        }
    }

    StandardScorer(Scorer scorer) {
        if (scorer instanceof StandardScorer) {
            StandardScorer standardScorer = ((StandardScorer) scorer);

            positiveTxtPattern.append(standardScorer.positiveTxtPattern);
            negativeTxtPattern.append(standardScorer.negativeTxtPattern);

            positiveTags.addAll(standardScorer.positiveTags);
            negativeTags.addAll(standardScorer.negativeTags);

            positiveAttributeValues.putAll(standardScorer.positiveAttributeValues);
            negativeAttributeValues.putAll(standardScorer.negativeAttributeValues);
        }
    }

    @Override
    public int hashCode() {
        int result = positiveAttributeValues.hashCode();
        result = 31 * result + negativeAttributeValues.hashCode();
        result = 31 * result + positiveTags.hashCode();
        result = 31 * result + negativeTags.hashCode();
        result = 31 * result + positiveTxtPattern.hashCode();
        result = 31 * result + negativeTxtPattern.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StandardScorer that = (StandardScorer) o;

        if (!positiveAttributeValues.equals(that.positiveAttributeValues)) return false;
        if (!negativeAttributeValues.equals(that.negativeAttributeValues)) return false;
        if (!positiveTags.equals(that.positiveTags)) return false;
        if (!negativeTags.equals(that.negativeTags)) return false;
        if (!positiveTxtPattern.toString().equals(that.positiveTxtPattern.toString())) return false;
        return negativeTxtPattern.toString().equals(that.negativeTxtPattern.toString());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "positiveAttributeValues=" + positiveAttributeValues +
                ", negativeAttributeValues=" + negativeAttributeValues +
                ", positiveTags=" + positiveTags +
                ", negativeTags=" + negativeTags +
                ", positiveTxtPattern=" + positiveTxtPattern +
                ", negativeTxtPattern=" + negativeTxtPattern +
                '}';
    }
}
