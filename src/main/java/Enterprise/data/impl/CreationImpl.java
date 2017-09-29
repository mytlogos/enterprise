package Enterprise.data.impl;

import Enterprise.data.CreationRelation;
import Enterprise.data.Default;
import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.Creator;
import Enterprise.data.intface.DataEntry;
import Enterprise.misc.DataAccess;
import Enterprise.misc.SQLUpdate;
import javafx.beans.property.*;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implementation of {@code Creation}.
 *
 * @see Creation
 */
@DataAccess(daoClass = "CreationTable")
public class CreationImpl extends AbstractDataEntry implements DataEntry, Creation {

    @SQLUpdate(stateGet = "isTitleChanged", valueGet = "getTitle", columnField = "titleC")
    private StringProperty title = new SimpleStringProperty();

    @SQLUpdate(stateGet = "isSeriesChanged", valueGet = "getSeries", columnField = "seriesC")
    private StringProperty series = new SimpleStringProperty();

    @SQLUpdate(stateGet = "isDateLastPortionChanged", valueGet = "getDateLastPortion", columnField = "dateLastPortionC")
    private StringProperty dateLastPortion = new SimpleStringProperty();

    @SQLUpdate(stateGet = "isNumPortionChanged", valueGet = "getNumPortion", columnField = "numPortionC")
    private IntegerProperty numPortion = new SimpleIntegerProperty();

    @SQLUpdate(stateGet = "isCoverPathChanged", valueGet = "getCoverPath", columnField = "coverPathC")
    private StringProperty coverPath = new SimpleStringProperty();

    @SQLUpdate(stateGet = "isWorkStatusChanged", valueGet = "getWorkStatus", columnField = "workStatusC")
    private StringProperty workStatus = new SimpleStringProperty();

    private CreationRelation relation;
    private Creator creator;

    private BooleanProperty titleChanged = new SimpleBooleanProperty(false);
    private BooleanProperty seriesChanged = new SimpleBooleanProperty(false);

    private BooleanProperty dateLastPortionChanged = new SimpleBooleanProperty(false);
    private BooleanProperty numPortionsChanged = new SimpleBooleanProperty(false);

    private BooleanProperty coverPathChanged = new SimpleBooleanProperty(false);
    private BooleanProperty workStatusChanged = new SimpleBooleanProperty(false);

    /**
     * The private constructor of {@code CreationImpl}.
     */
    private CreationImpl(CreationImplBuilder builder) {
        super(builder.id);
        this.series.set(builder.buildSeries);
        this.title.set(builder.buildTitle);
        this.dateLastPortion.set(builder.buildDateLastPortion);
        this.numPortion.set(builder.buildNumPortion);
        this.workStatus.set(builder.buildWorkStatus);
        this.coverPath.set(builder.buildCoverPath);

        invalidListener();
        bindUpdated();
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

    @Override
    final protected void bindUpdated() {
        updated.bind(titleChanged.or(seriesChanged).or(dateLastPortionChanged).or(numPortionsChanged).or(coverPathChanged).or(workStatusChanged));
        updated.addListener((observable, oldValue, newValue) -> {
            if (newValue && !newEntry) {
                OpEntryCarrier.getInstance().addUpdate(this);
            }
        });
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreationImpl)) return false;

        CreationImpl that = (CreationImpl) o;

        return getTitle().equals(that.getTitle()) && getSeries().equals(that.getSeries());
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
            URI uri = new URI(coverPath);

            this.coverPath.set(coverPath);
        } catch (URISyntaxException e) {
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
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + getSeries().hashCode();
        return result;
    }

    /**
     * The {@code CreationImplBuilder} for this {@code CreationImpl}.
     */
    public static class CreationImplBuilder {
        private final String buildTitle;
        private String buildSeries = Default.STRING;
        private String buildDateLastPortion = Default.STRING;
        private int buildNumPortion = Default.VALUE;
        private String buildCoverPath = Default.IMAGE;
        private String buildWorkStatus = Default.STRING;
        private int id = Default.VALUE;

        public CreationImplBuilder(String title) {
            buildTitle = title;
        }

        public CreationImplBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public CreationImplBuilder setSeries(String series) {
            this.buildSeries = series;
            return this;
        }

        public CreationImplBuilder setCoverPath(String coverPath) {
            this.buildCoverPath = coverPath;
            return this;
        }

        public CreationImplBuilder setNumPortion(int numPortion) {
            this.buildNumPortion = numPortion;
            return this;
        }

        public CreationImplBuilder setDateLastPortion(String dateLastPortion) {
            this.buildDateLastPortion = dateLastPortion;
            return this;
        }

        public CreationImplBuilder setWorkStatus(String workStatus) {
            this.buildWorkStatus = workStatus;
            return this;
        }

        public CreationImpl build() {
            validateState();
            return new CreationImpl(this);
        }

        /**
         * Validates the State of this {@code CreationImplBuilder}.
         *
         * @throws IllegalArgumentException if an argument is null or invalid
         */
        private void validateState() {
            String message = "";
            if (id < 0) {
                message = message + "id is invalid: " + id + ", ";
            }
            if (buildTitle == null) {
                message = message + "title is null, ";
            }
            if (buildSeries == null) {
                message = message + "series is null, ";
            }
            if (buildDateLastPortion == null) {
                message = message + "dateLastPortion is null, ";
            }
            if (buildNumPortion < 0) {
                message = message + "buildNumPortion is invalid: " + buildNumPortion + ", ";
            }
            if (buildCoverPath == null) {
                message = message + "coverPath is null, ";
            }
            if (buildWorkStatus == null) {
                message = message + "workStatus is null, ";
            }
            if (!message.isEmpty()) {
                throw new IllegalArgumentException(message);
            }
        }
    }
}
