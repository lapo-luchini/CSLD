package cz.larpovadatabaze.calendar;

import org.apache.wicket.util.lang.Args;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

/**
 * Boxing for Location.
 */
@Embeddable
public class Location {
    @Basic // TODO: See whether there is another way to specify access through properties
    private Double latitude;
    private Double longitude;

    protected Location(){}

    public Location(Double latitude, Double longitude) {
        Args.notNull(latitude, "Latitude must be set.");
        Args.notNull(longitude, "Longitude must be set.");

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
