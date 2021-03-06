package scrape.scrapeDaos;

import gorgon.external.*;
import scrape.sources.Source;
import scrape.sources.SourceType;
import scrape.sources.posts.PostConfig;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class SourceDao extends DataTable<Source> {
    private final Relation<Source, String> sourceurl = Relate.build(Ratio.ONE_TO_ONE, "SOURCEURL", Type.TEXT, Source::getUrl, Modifier.NOT_NULL);
    private final Relation<Source, SourceType> sourceType = Relate.build(Ratio.ONE_TO_ONE, "SOURCETYPE", SourceType.class, Type.ENUM, Source::getSourceType, Modifier.NOT_NULL);
    private final Relation<Source, PostConfig> postConfigs = Relate.build(Ratio.ONE_TO_ONE, PostConfig.class, Type.ID, source -> source.getConfig(new PostConfig()));


    protected SourceDao() {
        super("SOURCETABLE");
    }

    @Override
    public List<Relation<Source, ?>> getOneToOne() {
        List<Relation<Source, ?>> relations = new ArrayList<>();
        relations.add(sourceurl);
        relations.add(sourceType);
        relations.add(postConfigs);
        return relations;
    }

    @Override
    public List<Relation<Source, ?>> getOneToMany() {
        return Collections.emptyList();
    }

    @Override
    public Source getData(Result<Source> result) throws PersistenceException {
        final String sourceurl = result.get(this.sourceurl);
        final SourceType type = result.get(this.sourceType);
        PostConfig postConfigs = result.get(this.postConfigs);

        if (postConfigs == null) {
            postConfigs = new PostConfig();
        }

        try {
            return Source.create(sourceurl, type, postConfigs);
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
