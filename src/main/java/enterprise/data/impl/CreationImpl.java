package enterprise.data.impl;

import enterprise.data.CreationRelation;
import enterprise.data.intface.Creation;
import enterprise.data.intface.Creator;
import gorgon.external.GorgonEntry;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implementation of {@code Creation}.
 *
 * @see Creation
 */
public class CreationImpl extends AbstractDataEntry implements Creation {

    private final StringProperty title = new SimpleStringProperty();

    private final StringProperty series = new SimpleStringProperty();

    private final StringProperty dateLastPortion = new SimpleStringProperty();

    private final IntegerProperty numPortion = new SimpleIntegerProperty();

    private final StringProperty coverPath = new SimpleStringProperty();

    private final StringProperty workStatus = new SimpleStringProperty();

    private final StringProperty tocLocation = new SimpleStringProperty();

    private CreationRelation relation;
    private Creator creator;

    CreationImpl() {

    }

    @Override
    public Creator getCreator() {
        return creator;
    }

    @Override
    public void setCreator(Creator creator) {
        if (creator != null) {
            this.creator = creator;
            creator.addWork(this);
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public String getWorkStatus() {
        return workStatus.get();
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus.set(workStatus);
    }

    @Override
    public StringProperty workStatusProperty() {
        return workStatus;
    }

    @Override
    public String getSeries() {
        return series.get();
    }

    public void setSeries(String series) {
        this.series.set(series);
    }

    @Override
    public StringProperty seriesProperty() {
        return series;
    }

    @Override
    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    @Override
    public StringProperty titleProperty() {
        return title;
    }

    @Override
    public String getDateLastPortion() {
        return dateLastPortion.get();
    }

    public void setDateLastPortion(String dateLastPortion) {
        this.dateLastPortion.set(dateLastPortion);
    }

    @Override
    public StringProperty dateLastPortionProperty() {
        return dateLastPortion;
    }

    @Override
    public int getNumPortion() {
        return numPortion.get();
    }

    public void setNumPortion(int numPortion) {
        this.numPortion.set(numPortion);
    }

    @Override
    public IntegerProperty numPortionProperty() {
        return numPortion;
    }

    @Override
    public String getCoverPath() {
        return coverPath.get();
    }

    @Override
    public void setCoverPath(String coverPath) {

        try {
            new URI(coverPath);
            this.coverPath.set(coverPath);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("illegal argument: " + coverPath, e);
        }
    }

    @Override
    public StringProperty coverPathProperty() {
        return coverPath;
    }

    @Override
    public String getTocLocation() {
        return tocLocation.get();
    }

    public void setTocLocation(String tocLocation) {
        this.tocLocation.set(tocLocation);
    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + getSeries().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreationImpl)) return false;

        CreationImpl that = (CreationImpl) o;

        return getTitle().equals(that.getTitle()) && getSeries().equals(that.getSeries());
    }

    @Override
    public String toString() {
        return title.get();
    }

    @Override
    public int compareTo(GorgonEntry gorgonEntry) {
        if (gorgonEntry == null) return -1;
        if (gorgonEntry == this) return 0;
        if (!(gorgonEntry instanceof Creation)) return -1;

        Creation o = (Creation) gorgonEntry;
        int compare = getTitle().compareTo(o.getTitle());

        if (compare == 0) {
            compare = getSeries().compareTo(o.getSeries());
        }
        return compare;
    }

}
