package web;

import scrape.pages.PageConfig;
import scrape.sources.posts.PostConfig;

/**
 *
 */
public class ScrapeConfigs {
    private PageConfig pageConfigs;
    private PostConfig postConfig;
    private ContentConfig contentConfig;

    public PageConfig getPageConfigs() {
        return pageConfigs;
    }

    public void setPageConfigs(PageConfig pageConfigs) {
        this.pageConfigs = pageConfigs;
    }

    public PostConfig getPostConfig() {
        return postConfig;
    }

    public void setPostConfig(PostConfig postConfig) {
        this.postConfig = postConfig;
    }

    public ContentConfig getContentConfig() {
        return contentConfig;
    }

    public void setContentConfig(ContentConfig contentConfig) {
        this.contentConfig = contentConfig;
    }
}
