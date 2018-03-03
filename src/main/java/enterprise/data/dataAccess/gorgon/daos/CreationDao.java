package enterprise.data.dataAccess.gorgon.daos;

import enterprise.data.impl.CreationBuilder;
import enterprise.data.intface.Creation;
import gorgon.external.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class CreationDao extends DataTable<Creation> {
    private final Relation<Creation, String> title = Relate.build(Ratio.ONE_TO_ONE, "TITLE", Type.TEXT, Creation::getTitle, Modifier.NOT_NULL);
    private final Relation<Creation, String> series = Relate.build(Ratio.ONE_TO_ONE, "SERIES", Type.TEXT, Creation::getSeries, Modifier.NOT_NULL);
    private final Relation<Creation, String> datelastportion = Relate.build(Ratio.ONE_TO_ONE, "DATELASTPORTION", Type.TEXT, Creation::getDateLastPortion, Modifier.NOT_NULL);
    private final Relation<Creation, Integer> numportion = Relate.build(Ratio.ONE_TO_ONE, "NUMPORTION", Type.INTEGER, Creation::getNumPortion, Modifier.NOT_NULL);
    private final Relation<Creation, String> coverpath = Relate.build(Ratio.ONE_TO_ONE, "COVERPATH", Type.TEXT, Creation::getCoverPath, Modifier.NOT_NULL);
    private final Relation<Creation, String> workstatus = Relate.build(Ratio.ONE_TO_ONE, "WORKSTATUS", Type.TEXT, Creation::getWorkStatus, Modifier.NOT_NULL);
    private final Relation<Creation, String> toclocation = Relate.build(Ratio.ONE_TO_ONE, "TOCLOCATION", Type.TEXT, Creation::getTocLocation, Modifier.NOT_NULL);

    protected CreationDao() {
        super("CREATIONTABLE");
    }

    @Override
    public List<Relation<Creation, ?>> getOneToOne() {
        List<Relation<Creation, ?>> relations = new ArrayList<>();
        relations.add(title);
        relations.add(series);
        relations.add(datelastportion);
        relations.add(numportion);
        relations.add(coverpath);
        relations.add(workstatus);
        relations.add(toclocation);
        return relations;
    }

    @Override
    public List<Relation<Creation, ?>> getOneToMany() {
        return Collections.emptyList();
    }

    @Override
    public Creation getData(Result<Creation> result) throws PersistenceException {
        final String title = result.get(this.title);
        final String series = result.get(this.series);
        final String date = result.get(this.datelastportion);
        final Integer portions = result.get(this.numportion);
        final String path = result.get(this.coverpath);
        final String status = result.get(this.workstatus);
        final String toc = result.get(this.toclocation);

        return new CreationBuilder(title).
                setSeries(series).
                setCoverPath(path).
                setNumPortion(portions).
                setDateLastPortion(date).
                setWorkStatus(status).
                setTocLocation(toc).
                build();
    }
}
