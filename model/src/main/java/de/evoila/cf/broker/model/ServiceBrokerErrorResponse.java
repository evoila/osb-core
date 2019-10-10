package de.evoila.cf.broker.model;

import java.util.Objects;

public class ServiceBrokerErrorResponse {

    private String error;
    private String description;

    public ServiceBrokerErrorResponse() {
        this("","");
    }

    public ServiceBrokerErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ServiceBrokerErrorResponse{" +
                "error='" + error + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ServiceBrokerErrorResponse that = (ServiceBrokerErrorResponse) o;
        return Objects.equals(error, that.error) &&
               Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error, description);
    }

}
