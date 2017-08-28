package Enterprise.data.impl;

import Enterprise.data.CreationRelation;
import Enterprise.data.Default;
import Enterprise.data.EnterpriseEntry;
import Enterprise.data.intface.*;
import Enterprise.misc.SQL;
import javafx.beans.property.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

/**
 * Class representing an Entertainment-Object, like a Series, Novel
 * @see Creation
 */
public class SimpleCreation extends EnterpriseEntry implements DataBase, Creation{
    private int creationId;
    private static int idCounter = 1;

    @SQL
    private StringProperty title = new SimpleStringProperty();
    @SQL
    private StringProperty series = new SimpleStringProperty();
    @SQL
    private StringProperty dateLastPortion = new SimpleStringProperty();
    @SQL
    private IntegerProperty numPortion = new SimpleIntegerProperty();

    @SQL
    private StringProperty coverPath = new SimpleStringProperty();
    private CreationRelation relation;

    @SQL
    private StringProperty workStatus = new SimpleStringProperty();
    private Creator creator;

    private BooleanProperty titleChanged = new SimpleBooleanProperty(false);
    private BooleanProperty seriesChanged = new SimpleBooleanProperty(false);

    private BooleanProperty dateLastPortionChanged = new SimpleBooleanProperty(false);
    private BooleanProperty numPortionsChanged = new SimpleBooleanProperty(false);

    private BooleanProperty coverPathChanged = new SimpleBooleanProperty(false);
    private BooleanProperty workStatusChanged = new SimpleBooleanProperty(false);

    /**
     * The no-argument constructor of this {@code SimpleCreation}.
     * <p>All Data is set to default.</p>
     */
    public SimpleCreation() {
        this(Default.VALUE, Default.STRING, Default.STRING, Default.IMAGE,
                Default.VALUE, Default.STRING, Default.STRING);
    }

    /**
     * The constructor of this {@code SimpleCreation}
     *
     * @param series series of this {@code SimpleCreation}
     * @param title title of this {@code SimpleCreation}
     * @param coverPath coverPath of this {@code SimpleCreation}
     * @param numPortion number of Portions of this {@code SimpleCreation}
     * @param dateLastPortion String representation of the date of the last published portion  of this {@code SimpleCreation}
     * @param creatorStat status of the Creator about this {@code SimpleCreation}
     */
    public SimpleCreation(String series, String title, String coverPath, int numPortion,
                          String dateLastPortion, String creatorStat) {
        this(Default.VALUE, series, title, coverPath, numPortion, dateLastPortion,creatorStat);
    }

    /**
     * The constructor of {@code SimpleCreation}
     *
     * @param title title of this {@code SimpleCreation}
     */
    public SimpleCreation(String title) {
        this(Default.VALUE, Default.STRING, title, Default.IMAGE,
                Default.VALUE, Default.STRING,Default.STRING);
    }

    // TODO: 23.08.2017 use builder pattern maybe
    /**
     *  The constructor of {@code SimpleCreation}.
     *
     * @param id id of this {@code SimpleCreation}
     * @param series series of this {@code SimpleCreation}
     * @param title title of this {@code SimpleCreation}
     * @param coverPath coverPath of this {@code SimpleCreation}
     * @param numPortion number of Portions of this {@code SimpleCreation}
     * @param dateLastPortion String representation of the date of the last published portion  of this {@code SimpleCreation}
     * @param workStatus status of the Creator about this {@code SimpleCreation}
     */
    public SimpleCreation(int id, String series, String title, String coverPath,
                          int numPortion, String dateLastPortion, String workStatus) {
        this.series.set(series);
        this.title.set(title);
        this.dateLastPortion.set(dateLastPortion);
        this.numPortion.set(numPortion);
        this.workStatus.set(workStatus);
        this.coverPath.set(coverPath);

        if (id == 0) {
            creationId = idCounter;
            idCounter++;
        } else {
            creationId = id;
            if (idCounter <= id) {
                idCounter = id;
                idCounter++;
            }
        }
        validateState();
        invalidListener();
        bindUpdated();
    }

    /**
     * validates the State of this {@code SimpleCreation}
     *
     * @throws IllegalArgumentException if an argument is null or invalid
     */
    private void validateState() {
        String message = "";
        if (creationId < 0) {
            message = message + "id is invalid: " + creationId + ", ";
        }
        if (title.get() == null) {
            message = message + "title is null, ";
        }
        if (series.get() == null) {
            message = message + "series is null, ";
        }
        if (dateLastPortion.get() == null) {
            message = message + "dateLastPortion is null, ";
        }
        if (numPortion.get() < 0) {
            message = message + "numPortion is invalid: " + numPortion.get() + ", ";
        }
        if (coverPath.get() == null) {
            message = message + "coverPath is null, ";
        }
        if (workStatus.get() == null) {
            message = message + "workStatus is null, ";
        }
        if (!message.isEmpty()) {
            IllegalArgumentException exception = new IllegalArgumentException(message);
            logger.log(Level.WARNING, "object creation failed", exception);
            throw exception;
        }
    }

    /**
     * adds InvalidListeners to the data-Properties, which set the corresponding flags to true, if a change happened
     */
    private void invalidListener() {
        title.addListener(observable -> titleChanged.set(true));
        series.addListener(observable -> seriesChanged.set(true));
        dateLastPortion.addListener(observable -> dateLastPortionChanged.set(true));
        numPortion.addListener(observable -> numPortionsChanged.set(true));
        coverPath.addListener(observable -> coverPathChanged.set(true));
        workStatus.addListener(observable -> workStatusChanged.set(true));
    }

    final protected void bindUpdated() {
        updated.bind(titleChanged.or(seriesChanged).or(dateLastPortionChanged).or(numPortionsChanged).or(coverPathChanged).or(workStatusChanged));
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
    public int getId() {
        return creationId;
    }

    @Override
    public void setId(int id, Table table) {
        if (!(table instanceof DataTable)) {
            throw new IllegalAccessError();
        }
        if (id < 1) {
            throw new IllegalArgumentException("should not be smaller than 1: " + id);
        }
        this.creationId = id;
    }

    @Override
    public void setUpdated() {
        titleChanged.set(false);
        seriesChanged.set(false);
        numPortionsChanged.set(false);
        dateLastPortionChanged.set(false);
        coverPathChanged.set(false);
        workStatusChanged.set(false);
    }

    @Override
    public boolean isUpdated() {
        return updated.get();
    }

    @Override
    public BooleanProperty updatedProperty() {
        return updated;
    }

    @Override
    public boolean isWorkStatusChanged() {
        return workStatusChanged.get();
    }

    @Override
    public boolean isTitleChanged() {
        return titleChanged.get();
    }

    @Override
    public boolean isSeriesChanged() {
        return seriesChanged.get();
    }

    @Override
    public boolean isDateLastPortionChanged() {
        return dateLastPortionChanged.get();
    }

    @Override
    public boolean isNumPortionChanged() {
        return numPortionsChanged.get();
    }

    @Override
    public boolean isCoverPathChanged() {
        return coverPathChanged.get();
    }

    @Override
    public String getWorkStatus() {
        return workStatus.get();
    }

    @Override
    public StringProperty workStatusProperty() {
        return workStatus;
    }

    @Override
    public String getSeries() {
        return series.get();
    }

    @Override
    public StringProperty seriesProperty() {
        return series;
    }

    @Override
    public String getTitle() {
        return title.get();
    }

    @Override
    public StringProperty titleProperty() {
        return title;
    }

    @Override
    public String getDateLastPortion() {
        return dateLastPortion.get();
    }

    @Override
    public StringProperty dateLastPortionProperty() {
        return dateLastPortion;
    }

    @Override
    public int getNumPortion() {
        return numPortion.get();
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
            File file = new File(coverPath);
            URL uri = new URL(file.toString());

            this.coverPath.set(coverPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Not a valid Path: " + coverPath);
        }
    }

    @Override
    public StringProperty coverPathProperty() {
        return coverPath;
    }

    @Override
    public String toString() {
        return title.get();
    }

    @Override
    public int compareTo(Creation simpleCreation) {
        int compare;
        compare = title.get().compareTo(simpleCreation.getTitle());
        if (compare == 0) {
            compare = series.get().compareTo(simpleCreation.getSeries());
        }
        return compare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleCreation)) return false;

        SimpleCreation that = (SimpleCreation) o;

        return getTitle().equals(that.getTitle()) && getSeries().equals(that.getSeries());
    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + getSeries().hashCode();
        return result;
    }
}
