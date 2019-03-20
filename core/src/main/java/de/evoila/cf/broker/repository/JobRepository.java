package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.JobProgress;

/**
 * @author Johannes Hiemer.
 */
public interface JobRepository {

	JobProgress getJobProgressById(String id);

    JobProgress getJobProgressByReferenceId(String referenceId);

    JobProgress saveJobProgress(String id, String referenceId, String progress, String description, String operation);

    JobProgress updateJobProgress(String id, String progress, String description);

	boolean containsJobProgress(String id);

	void deleteJobProgress(String id);

}