package scrape.sources.novel.toc.structure.novel;

import scrape.sources.novel.toc.structure.CreationContent;
import scrape.sources.novel.toc.structure.TableOfContent;
import scrape.sources.novel.toc.structure.intface.Portion;
import scrape.sources.novel.toc.structure.intface.Portionable;
import scrape.sources.novel.toc.structure.intface.Section;

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
    public TableOfContent getParent() {
        return (TableOfContent) super.getParent();
    }

    @Override
    public String toString() {
        return super.toString() + ", sectionType=" + getSectionType();
    }

    private String getSectionType() {
        return sectionType;
    }
}
