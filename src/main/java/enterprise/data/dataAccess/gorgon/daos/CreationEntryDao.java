package enterprise.data.dataAccess.gorgon.daos;

import enterprise.data.impl.CreationEntryImpl;
import enterprise.data.intface.Creation;
import enterprise.data.intface.CreationEntry;
import enterprise.data.intface.Creator;
import enterprise.data.intface.User;
import enterprise.modules.BasicModule;
import gorgon.external.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static gorgon.external.Modifier.NOT_NULL;

/**
 *
 */
public class CreationEntryDao extends DataTable<CreationEntry> {
    private final Relation<CreationEntry, String> module = Relate.build(Ratio.ONE_TO_ONE, "MODULE", Type.TEXT, creationEntry -> creationEntry.getModule().toString(), NOT_NULL);
    private final Relation<CreationEntry, Creation> creation = Relate.build(Ratio.ONE_TO_ONE, Creation.class, Type.ID, CreationEntry::getCreation, NOT_NULL);
    private final Relation<CreationEntry, User> user = Relate.build(Ratio.ONE_TO_ONE, User.class, Type.ID, CreationEntry::getUser, NOT_NULL);
    private final Relation<CreationEntry, Creator> creator = Relate.build(Ratio.ONE_TO_ONE, Creator.class, Type.ID, CreationEntry::getCreator, NOT_NULL);


    CreationEntryDao() {
        super("CREATIONENTRYTABLE");
    }

    CreationEntryDao(String tableName) {
        super(tableName);
    }

    @Override
    public List<Relation<CreationEntry, ?>> getOneToOne() {
        List<Relation<CreationEntry, ?>> relations = new ArrayList<>();
        relations.add(module);
        relations.add(creation);
        relations.add(user);
        relations.add(creator);

        return relations;
    }

    @Override
    public List<Relation<CreationEntry, ?>> getOneToMany() {
        return Collections.emptyList();
    }

    @Override
    public CreationEntry getData(Result<CreationEntry> result) throws PersistenceException {
        final String module = result.get(this.module);
        final Creation creation = result.get(this.creation);
        final Creator creator = result.get(this.creator);
        final User user = result.get(this.user);

        final BasicModule basicModule = BasicModule.valueOf(module);

        return new CreationEntryImpl(user, creation, creator, basicModule);
    }
}
