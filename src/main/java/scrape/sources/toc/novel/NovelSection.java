package scrape.sources.toc.novel;

import scrape.sources.toc.CreationContent;
import scrape.sources.toc.CreationRoot;
import scrape.sources.toc.Portionable;
import scrape.sources.toc.intface.Portion;
import scrape.sources.toc.intface.Section;

import java.util.List;

/**
 *
 */
public class NovelSection extends CreationContent implements Section, Portionable {
    private final String sectionType;

    public NovelSection(String title, String type) {
        this(title, type, false, 0);
    }

    public NovelSection(String title, String sectionType, boolean isExtra, double index) {
        super(title, "Section", isExtra, index);
        this.sectionType = sectionType;
    }

    @Override
    public void add(Portion portion) {
        super.addChild(portion);
    }

    @Override
    public void addAll(List<? extends Portion> portions) {
        super.addChildren(portions);
    }

    @Override
    public List<Chapter> getChildren() {
        return (List<Chapter>) super.getChildren();
    }

    @Override
    public CreationRoot getParent() {
        return (CreationRoot) super.getParent();
    }

    public String getSectionType() {
        return sectionType;
    }

    @Override
    public String toString() {
        return super.toString() + ", sectionType=" + getSectionType();
    }
}
