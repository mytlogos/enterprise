package enterprise.data.dataAccess.gorgon.daos;

import enterprise.data.impl.CreatorImpl;
import enterprise.data.intface.Creator;
import gorgon.external.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class CreatorDao extends DataTable<Creator> {
    private final Relation<Creator, String> name = Relate.build(Ratio.ONE_TO_ONE, "AUTHOR", Type.TEXT, Creator::getName, Modifier.NOT_NULL);
    private final Relation<Creator, String> sortName = Relate.build(Ratio.ONE_TO_ONE, "AUTHORSORT", Type.TEXT, Creator::getSortName, Modifier.NOT_NULL);
    private final Relation<Creator, String> status = Relate.build(Ratio.ONE_TO_ONE, "AUTHORSTATUS", Type.TEXT, Creator::getStatus, Modifier.NOT_NULL);

    protected CreatorDao() {
        super("CREATORTABLE");
    }

    @Override
    public List<Relation<Creator, ?>> getOneToOne() {
        List<Relation<Creator, ?>> relations = new ArrayList<>();
        relations.add(name);
        relations.add(sortName);
        relations.add(status);

        return relations;
    }

    @Override
    public List<Relation<Creator, ?>> getOneToMany() {
        return Collections.emptyList();
    }

    @Override
    public Creator getData(Result<Creator> result) throws PersistenceException {
        String name = result.get(this.name);
        String sortName = result.get(this.sortName);
        String status = result.get(this.status);

        return new CreatorImpl.CreatorBuilder(name).setSortName(sortName).setStatus(status).build();
    }
}
