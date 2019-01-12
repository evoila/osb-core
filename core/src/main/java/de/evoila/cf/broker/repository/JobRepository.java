package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.JobProgress;

/**
 * @author Johannes Hiemer.
 */
public interface JobRepository {

    /**
     * @param id
     * @return
     */
	JobProgress getJobProgressById(String id);

    /**
     * @param referenceId
     * @return
     */
    JobProgress getJobProgressByReferenceId(String referenceId);

    /**
     * @param id
     * @param referenceId
     * @param progress
     * @param description
     * @param operation
     * @return
     */
    JobProgress saveJobProgress(String id, String referenceId, String progress, String description, String operation);

    /**
     * @param id
     * @param progress
     * @param description
     * @return
     */
    JobProgress updateJobProgress(String id, String progress, String description);

    /**
     * @param id
     * @return
     */
	boolean containsJobProgress(String id);

    /**
     * @param id
     */
    void deleteJobProgress(String id);

}