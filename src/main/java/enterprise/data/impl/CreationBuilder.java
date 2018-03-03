package enterprise.data.impl;

import enterprise.data.Default;

/**
 * The {@code CreationBuilder} for this {@code CreationImpl}.
 */
public class CreationBuilder {
    private final String title;
    private String series = Default.STRING;
    private String dateLastPortion = Default.STRING;
    private int numPortion = Default.VALUE;
    private String coverPath = Default.IMAGE;
    private String workStatus = Default.STRING;
    private String tocLocation = null;

    public CreationBuilder(String title) {
        this.title = title;
    }

    public CreationBuilder setSeries(String series) {
        this.series = series;
        return this;
    }

    public CreationBuilder setCoverPath(String coverPath) {
        this.coverPath = coverPath;
        return this;
    }

    public CreationBuilder setNumPortion(int numPortion) {
        this.numPortion = numPortion;
        return this;
    }

    public CreationBuilder setDateLastPortion(String dateLastPortion) {
        this.dateLastPortion = dateLastPortion;
        return this;
    }

    public CreationBuilder setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
        return this;
    }

    public CreationImpl build() {
        validateState();

        CreationImpl creation = new CreationImpl();
        creation.setCoverPath(coverPath);
        creation.setDateLastPortion(dateLastPortion);
        creation.setNumPortion(numPortion);
        creation.setSeries(series);
        creation.setTitle(title);
        creation.setTocLocation(tocLocation);
        creation.setWorkStatus(workStatus);
        return creation;
    }

    /**
     * Validates the State of this {@code CreationBuilder}.
     *
     * @throws IllegalArgumentException if an argument is null or invalid
     */
    private void validateState() {
        String message = "";
        if (title == null) {
            message = message + "title is null, ";
        }
        if (series == null) {
            message = message + "series is null, ";
        }
        if (dateLastPortion == null) {
            message = message + "dateLastPortion is null, ";
        }
        if (numPortion < 0) {
            message = message + "numPortion is invalid: " + numPortion + ", ";
        }
        if (coverPath == null) {
            message = message + "coverPath is null, ";
        }
        if (workStatus == null) {
            message = message + "workStatus is null, ";
        }
        if (!message.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }


    public CreationBuilder setTocLocation(String tocLocation) {
        this.tocLocation = tocLocation;
        return this;
    }
}
