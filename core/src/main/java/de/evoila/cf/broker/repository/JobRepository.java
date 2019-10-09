package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.JobProgress;

/**
 * @author Johannes Hiemer.
 */
public interface JobRepository {

	JobProgress getJobProgressById(String id);

    /**
     * Used to access a JobProgress Object with an instance or binding ID.
     *
     * @param referenceId the id of the object that is being created. For ServiceInstances the instanceId, for bindings th bindingId
     * @return JobProgress the JobProgress Object with the provided referenceId.
     * @throws java.util.NoSuchElementException gets thrown if the Element does not exist.
     */
    JobProgress getJobProgressByReferenceId(String referenceId);

    JobProgress saveJobProgress(String id, String referenceId, String progress, String description, String operation);

    JobProgress updateJobProgress(String id, String progress, String description);

	boolean containsJobProgress(String id);

	void deleteJobProgress(String id);

}