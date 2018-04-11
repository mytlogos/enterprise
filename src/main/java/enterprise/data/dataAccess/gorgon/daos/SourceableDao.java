package enterprise.data.dataAccess.gorgon.daos;

import enterprise.data.impl.SourceableImpl;
import enterprise.data.intface.Sourceable;
import gorgon.external.*;
import javafx.collections.FXCollections;
import scrape.sources.Source;
import tools.SetList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class SourceableDao extends DataTable<Sourceable> {
    private final Relation<Sourceable, String> translator = Relate.build(Ratio.ONE_TO_ONE, "TRANSLATOR", Type.TEXT, Sourceable::getTranslator, Modifier.NOT_NULL);
    private final Relation<Sourceable, Collection<Source>> sources = Relate.build(Ratio.ONE_TO_MANY, Source.class, Type.ID, Sourceable::getSources, Modifier.NOT_NULL);

    protected SourceableDao() {
        super("SOURCEABLETABLE");
    }

    @Override
    public List<Relation<Sourceable, ?>> getOneToOne() {
        List<Relation<Sourceable, ?>> relations = new ArrayList<>();
        relations.add(translator);
        return relations;
    }

    @Override
    public List<Relation<Sourceable, ?>> getOneToMany() {
        List<Relation<Sourceable, ?>> relations = new ArrayList<>();
        relations.add(sources);
        return relations;
    }

    @Override
    public Sourceable getData(Result<Sourceable> result) throws PersistenceException {
        final String translator = result.get(this.translator);
        final Collection<Source> sources = result.get(this.sources);
        return SourceableImpl.get(FXCollections.observableArrayList(new SetList<>(sources)), translator);
    }
}
