package enterprise.data.dataAccess.gorgon.daos;

import enterprise.data.impl.SourceableEntryImpl;
import enterprise.data.intface.*;
import enterprise.modules.BasicModule;
import gorgon.external.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static gorgon.external.Modifier.NOT_NULL;

/**
 *
 */
public class SourceableEntryDao extends DataTable<SourceableEntry> {
    private final Relation<SourceableEntry, String> module = Relate.build(Ratio.ONE_TO_ONE, "MODULE", Type.TEXT, entry -> entry.getModule().toString(), NOT_NULL);
    private final Relation<SourceableEntry, Creation> creation = Relate.build(Ratio.ONE_TO_ONE, Creation.class, Type.ID, SourceableEntry::getCreation, NOT_NULL);
    private final Relation<SourceableEntry, User> user = Relate.build(Ratio.ONE_TO_ONE, User.class, Type.ID, SourceableEntry::getUser, NOT_NULL);
    private final Relation<SourceableEntry, Creator> creator = Relate.build(Ratio.ONE_TO_ONE, Creator.class, Type.ID, SourceableEntry::getCreator, NOT_NULL);
    private final Relation<SourceableEntry, Sourceable> sourceable = Relate.build(Ratio.ONE_TO_ONE, Sourceable.class, Type.ID, SourceableEntry::getSourceable, NOT_NULL);

    protected SourceableEntryDao() {
        super("SOURCEABLE_ENTRY_TABLE");
    }

    @Override
    public List<Relation<SourceableEntry, ?>> getOneToOne() {
        List<Relation<SourceableEntry, ?>> relations = new ArrayList<>();
        relations.add(module);
        relations.add(creation);
        relations.add(user);
        relations.add(creator);
        relations.add(sourceable);
        return relations;
    }

    @Override
    public List<Relation<SourceableEntry, ?>> getOneToMany() {
        return Collections.emptyList();
    }

    @Override
    public SourceableEntry getData(Result<SourceableEntry> result) throws PersistenceException {
        final Creation creation = result.get(this.creation);
        final Creator creator = result.get(this.creator);
        final User user = result.get(this.user);
        final Sourceable sourceable = result.get(this.sourceable);

        final String module = result.get(this.module);
        final BasicModule basicModule = BasicModule.valueOf(module);

        return new SourceableEntryImpl(user, creation, creator, sourceable, basicModule);
    }

}
