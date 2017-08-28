package Enterprise.modules;

import Enterprise.data.intface.SourceableEntry;
import Enterprise.misc.SetList;

import java.util.Collection;
import java.util.List;

/**
 * Container class for {@link Module#NOVEL}.
 */
public class Novel extends EnterpriseSegments<SourceableEntry> {
    private List<String> tlGroup = new SetList<>();

    private static final Novel ourInstance = new Novel();

    public static Novel getInstance() {
        return ourInstance;
    }

    private Novel() {
        if (ourInstance != null) {
            throw new IllegalStateException();
        }
    }

    public boolean addTlGroup(String tlGroup) {
        return this.tlGroup.add(tlGroup);
    }

    public boolean addTlGroup(Collection<String> tlGroup) {
        return this.tlGroup.addAll(tlGroup);
    }

    public List<String> getTlGroup() {
        return tlGroup;
    }
}
