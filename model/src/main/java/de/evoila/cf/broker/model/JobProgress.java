/**
 * 
 */
package de.evoila.cf.broker.model;

import java.util.Date;
import java.util.Objects;

/**
 * @author Johannes Hiemer, Marco Di Martino
 */
public class JobProgress implements BaseEntity<String> {

	public static final String SUCCESS = "succeeded";

	public static final String FAILED = "failed";

	public static final String IN_PROGRESS = "in progress";
	
	public static final String UNKNOWN = "unknown";

	public static final String PROVISION = "provision";

	public static final String UPDATE = "update";

	public static final String DELETE = "delete";

	public static final String BIND = "bind";

	public static final String UNBIND = "unbind";

	private String id;

	private String state;
	
	private Date date;
	
	private String description;

	/**
	 * This field describes the kind of operation this object describes, not the "operation" field for the response to the platform!
	 * Use id to return to the platform as "operation" field in the json.
	 *
	 */
	private String operation;

	private String referenceId;

	public JobProgress() {
	}

	public JobProgress(String id, String referenceId, String progress, String description) {
		this(id, referenceId, progress, description, null);
	}

	public JobProgress(String id, String referenceId, String progress, String description, String operation) {
	    this.id = id;
		this.referenceId = referenceId;
		this.state = progress;
		this.date = new Date();
		this.description = description;
		this.operation = operation;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the {@linkplain #operation} that describes the kind of operation.
	 * See {@linkplain #PROVISION}, {@linkplain #UPDATE}, {@linkplain #DELETE}, {@linkplain #BIND} and {@linkplain #UNBIND} for values to expect.
	 * @return description string for the current kind of operation
	 */
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public boolean isInProgress() {
		return IN_PROGRESS.equals(state);
	}

	public boolean isFailed() {
		return FAILED.equals(state);
	}

	public boolean isSucceeded() {
		return SUCCESS.equals(state);
	}

	public boolean isProvisioning() {
		return PROVISION.equals(operation);
	}

	public boolean isUpdating() {
		return UPDATE.equals(operation);
	}

	public boolean isDeleting() {
		return DELETE.equals(operation);
	}

	public boolean isBinding() {
		return BIND.equals(operation);
	}

	public boolean isUnbinding() {
		return UNBIND.equals(operation);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		JobProgress that = (JobProgress) o;
		return Objects.equals(id, that.id) &&
			   Objects.equals(state, that.state) &&
			   Objects.equals(date, that.date) &&
			   Objects.equals(description, that.description) &&
			   Objects.equals(operation, that.operation) &&
			   Objects.equals(referenceId, that.referenceId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, state, date, description, operation, referenceId);
	}

}
