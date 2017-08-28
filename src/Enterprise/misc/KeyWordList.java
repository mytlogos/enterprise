package Enterprise.misc;

import java.util.ArrayList;

/**
 * Awkward workaround, that no {@code KeyWordList} is equal in
 * the face of {@code Comparable}, especially empty Lists.
 */
public class KeyWordList extends ArrayList<String> implements Comparable<KeyWordList> {

    private static int counter = 1;
    private final int count;

    public KeyWordList() {
        count = counter;
        counter++;
    }

    @Override
    public int compareTo(KeyWordList o) {
        int compare = -1;
        if (o != null) {
            if (this.size() == o.size()) {
                if (this.isEmpty() && o.isEmpty()) {
                    compare = this.count - o.count;
                }
            }
        }
        return compare;
    }
}
