package scrape.sources;

import scrape.sources.novels.ElementsGetter;

/**
 *
 */
public class PostConfigs {
    private boolean init = false;
    private boolean isArchive = false;

    private ElementsGetter archive = null;
    private ElementsGetter body = null;
    private ElementsGetter posts = null;
    private ElementsGetter feed = null;

    private ElementsGetter time = null;
    private ElementsGetter title = null;
    private ElementsGetter postBody = null;
    private ElementsGetter footer = null;

    public boolean isInit() {
        return init;
    }

    private void init() {
        init = true;
    }

    public ElementsGetter getPostBody() {
        return postBody;
    }

    public void setPostBody(ElementsGetter postBody) {
        init();
        this.postBody = postBody;
    }

    public ElementsGetter getTime() {
        return time;
    }

    public void setTime(ElementsGetter time) {
        init();
        this.time = time;
    }

    public ElementsGetter getTitle() {
        return title;
    }

    public void setTitle(ElementsGetter title) {
        init();
        this.title = title;
    }

    public ElementsGetter getBody() {
        return body;
    }

    public void setBody(ElementsGetter body) {
        init();
        this.body = body;
    }

    public ElementsGetter getFooter() {
        return footer;
    }

    public void setFooter(ElementsGetter footer) {
        init();
        this.footer = footer;
    }

    public boolean isArchive() {
        return isArchive;
    }

    public ElementsGetter getArchive() {
        return archive;
    }

    public void setArchive(ElementsGetter archive) {
        init();
        isArchive = archive != null;
        this.archive = archive;
    }

    public ElementsGetter getPosts() {
        return posts;
    }

    public void setPosts(ElementsGetter posts) {
        init();
        this.posts = posts;
    }

    public ElementsGetter getFeed() {
        return feed;
    }

    public void setFeed(ElementsGetter feed) {
        init();
        this.feed = feed;
    }
}
