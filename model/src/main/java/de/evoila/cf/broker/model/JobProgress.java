/**
 * 
 */
package de.evoila.cf.broker.model;

import java.util.Date;

/**
 * 
 * @author Johannes Hiemer.
 * @author Marco Di Martino
 *
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

	private String operation;

	public JobProgress() {
	}

	public JobProgress(String serviceInstanceId, String progress, String description) {
		this(serviceInstanceId, progress, description, null);
	}

	public JobProgress(String serviceInstanceId, String progress, String description, String operation) {
		this.id = serviceInstanceId;
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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
}
