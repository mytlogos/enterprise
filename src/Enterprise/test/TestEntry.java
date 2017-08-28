package Enterprise.test;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Dominik on 09.07.2017.
 * Part of OgameBot.
 */
public class TestEntry {
    private StringProperty name;
    private StringProperty buch;
    private IntegerProperty zahl;

    private IntegerBean integerBean = new IntegerBean();
    private StringBean stringBean = new StringBean();

    public TestEntry(String name, String buch, Integer zahl) {
        //this.name = new SimpleStringProperty(this, "name", name);
        this.buch = new SimpleStringProperty(this, "buch", buch);
        //this.zahl = new SimpleIntegerProperty(this, "zahl", zahl);
        this.integerBean.integerProperty().set(zahl);
        this.stringBean.stringNameProperty().set(name);
    }

    public IntegerBean getIntegerBean() {
        return integerBean;
    }

    public StringBean getStringBean() {
        return stringBean;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setBuch(String buch) {
        this.buch.set(buch);
    }

    public void setZahl(int zahl) {
        this.zahl.set(zahl);
    }

    public String getName() {

        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getBuch() {
        return buch.get();
    }

    public StringProperty buchProperty() {
        return buch;
    }

    public int getZahl() {
        return zahl.get();
    }

    public IntegerProperty zahlProperty() {
        return zahl;
    }
}
