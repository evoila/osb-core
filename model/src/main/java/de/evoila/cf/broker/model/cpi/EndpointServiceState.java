/**
 *
 */
package de.evoila.cf.broker.model.cpi;

import java.util.Date;
import java.util.Objects;

/**
 * @author Johannes Hiemer.
 *
 */
public class EndpointServiceState {

    private String identifier;

    private Date date;

    private AvailabilityState state;

    private String information;

    public EndpointServiceState(String identifier, AvailabilityState state) {
        this(identifier, state, null);
    }

    public EndpointServiceState(String identifier, AvailabilityState state, String information) {
        this.identifier = identifier;
        this.date = new Date();
        this.state = state;
        this.information = information;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public AvailabilityState getState() {
        return state;
    }

    public void setState(AvailabilityState state) {
        this.state = state;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndpointServiceState that = (EndpointServiceState) o;
        return identifier.equals(that.identifier) &&
                date.equals(that.date) &&
                state == that.state &&
                Objects.equals(information, that.information);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, date, state, information);
    }
}
