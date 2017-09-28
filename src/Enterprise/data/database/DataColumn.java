package Enterprise.data.database;

/**
 *
 */
public class DataColumn {
    private int index = -1;
    private String name;
    private String type;
    private String[] modifiers = new String[0];

    public DataColumn(String name, String type, String... modifiers) {
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
    }

    public DataColumn(String name, Type type, Modifiers... modifiers) {
        this.name = name;
        this.type = type.toString();

        this.modifiers = new String[modifiers.length];
        for (int i = 0; i < modifiers.length; i++) {
            this.modifiers[i] = modifiers[i].toString();
        }
    }

    public String getName() {
        return name;
    }

    String getType() {
        return type;
    }

    String[] getModifiers() {
        return modifiers;
    }

    int getIndex() {
        return index;
    }

    void setIndex(int index) {
        if (index < 1) {
            throw new IllegalArgumentException();
        }
        this.index = index;
    }

    public enum Type {
        INTEGER("INTEGER") {

        },
        TEXT("TEXT"),
        BOOLEAN("NUMERIC"),;

        private final String type;

        Type(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public enum Modifiers {
        PRIMARY_KEY("PRIMARY KEY"),
        UNIQUE("UNIQUE"),
        NOT_NULL("NOT NULL");

        private final String modifier;

        Modifiers(String modifier) {
            this.modifier = modifier;
        }

        @Override
        public String toString() {
            return modifier;
        }
    }

}
