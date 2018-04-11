package scrape.pages;

import gorgon.external.GorgonEntry;
import scrape.Config;

import java.util.Objects;

/**
 *
 */
public class PageConfig extends Config {
    private String navigation;
    private String feed;
    private String content;
    private String sideBarRight;
    private String sideBarLeft;

    public String getSideBarLeft() {
        return sideBarLeft;
    }

    public void setSideBarLeft(String sideBarLeft) {
        this.sideBarLeft = sideBarLeft;
    }

    @Override
    public int hashCode() {
        int result = getNavigation() != null ? getNavigation().hashCode() : 0;
        result = 31 * result + (getContent() != null ? getContent().hashCode() : 0);
        result = 31 * result + (getSideBarRight() != null ? getSideBarRight().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PageConfig that = (PageConfig) o;

        if (!Objects.equals(getNavigation(), that.getNavigation())) return false;
        if (!Objects.equals(getContent(), that.getContent())) return false;
        return Objects.equals(getSideBarRight(), that.getSideBarRight());
    }

    @Override
    public String toString() {
        return "PageConfig{" +
                "navigation=" + navigation +
                ", content=" + content +
                ", sideBarRight=" + sideBarRight +
                ", sideBarLeft=" + sideBarLeft +
                ", feed=" + feed +
                '}';
    }

    public String getNavigation() {
        return navigation;
    }

    public void setNavigation(String navigation) {
        this.navigation = navigation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSideBarRight() {
        return sideBarRight;
    }

    public void setSideBarRight(String sideBarRight) {
        this.sideBarRight = sideBarRight;
    }

    @Override
    public int compareTo(GorgonEntry o) {
        if (o == null) return -1;
        if (o == this) return 0;
        if (!(o instanceof PageConfig)) return -1;

        //todo implement compareTo
        return 0;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    @Override
    public String getKey() {
        return "page";
    }
}
