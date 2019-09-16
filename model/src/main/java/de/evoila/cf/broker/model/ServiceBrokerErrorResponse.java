package de.evoila.cf.broker.model;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceBrokerErrorResponse that = (ServiceBrokerErrorResponse) o;

        if (error != null ? !error.equals(that.error) : that.error != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;
    }

    @Override
    public int hashCode() {
        int result = error != null ? error.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
