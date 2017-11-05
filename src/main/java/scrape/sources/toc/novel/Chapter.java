package scrape.sources.toc.novel;

import scrape.sources.toc.Leaf;
import scrape.sources.toc.intface.Portion;
import scrape.sources.toc.intface.SubPortion;

import java.util.List;

/**
 *
 */
public class Chapter extends Leaf implements Portion {

    public Chapter(String title) {
        this(title, false, 0);
    }

    public Chapter(String title, boolean isExtra, double index) {
        this(title, isExtra, index, 0);
    }

    public Chapter(String title, boolean isExtra, double totIndex, double partIndex) {
        super(title, "Chapter", isExtra, totIndex, partIndex);
    }

    @Override
    public void add(SubPortion subPortion) {
        super.addChild(subPortion);
    }

    @Override
    public void addAll(List<? extends SubPortion> subPortions) {
        super.addChildren(subPortions);
    }

    @Override
    public List<SubChapter> getChildren() {
        return (List<SubChapter>) super.getChildren();
    }

    @Override
    public void remove(SubPortion subPortion) {
        super.removeChild(subPortion);
    }
}
