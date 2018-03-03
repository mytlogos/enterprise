package enterprise.data.dataAccess.gorgon.daos;

import enterprise.data.impl.UserImpl;
import enterprise.data.intface.User;
import gorgon.external.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class UserDao extends DataTable<User> {
    private final Relation<User, String> ownStatus = Relate.build(Ratio.ONE_TO_ONE, "OWNSTATUS", Type.TEXT, User::getOwnStatus);
    private final Relation<User, String> listName = Relate.build(Ratio.ONE_TO_ONE, "LIST", Type.TEXT, User::getListName);
    private final Relation<User, String> keyWords = Relate.build(Ratio.ONE_TO_ONE, "KEYWORDS", Type.TEXT, User::getKeyWords);
    private final Relation<User, String> comment = Relate.build(Ratio.ONE_TO_ONE, "COMMENT", Type.TEXT, User::getComment);
    private final Relation<User, Integer> rating = Relate.build(Ratio.ONE_TO_ONE, "RATING", Type.INTEGER, User::getRating);
    private final Relation<User, Integer> processedPortion = Relate.build(Ratio.ONE_TO_ONE, "PROCESSED", Type.INTEGER, User::getProcessedPortion);

    protected UserDao() {
        super("USERTABLE");
    }

    @Override
    public List<Relation<User, ?>> getOneToOne() {
        List<Relation<User, ?>> relations = new ArrayList<>();
        relations.add(ownStatus);
        relations.add(listName);
        relations.add(keyWords);
        relations.add(comment);
        relations.add(rating);
        relations.add(processedPortion);
        return relations;
    }

    @Override
    public List<Relation<User, ?>> getOneToMany() {
        return Collections.emptyList();
    }

    @Override
    public User getData(Result<User> result) throws PersistenceException {
        final String status = result.get(ownStatus);
        final String listName = result.get(this.listName);
        final String keyWords = result.get(this.keyWords);
        final String comment = result.get(this.comment);
        final Integer rating = result.get(this.rating);
        final Integer processed = result.get(processedPortion);

        return new UserImpl(status, comment, rating, listName, processed, keyWords);
    }
}
