package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.service.AsyncOperationService;
import de.evoila.cf.broker.service.JobProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Johannes Hiemer.
 */
public class AsyncOperationServiceImpl implements AsyncOperationService {

    static Logger log = LoggerFactory.getLogger(AsyncDeploymentServiceImpl.class);

    JobProgressService progressService;

    public AsyncOperationServiceImpl(JobProgressService progressService) {
        this.progressService = progressService;
    }


    @Override
    public JobProgress getProgressByReferenceId(String referenceId) {
        try {
            return progressService.getProgressByReferenceId(referenceId);
        } catch (Exception e) {
            log.error("Error during job progress retrieval", e);
            return new JobProgress(JobProgress.UNKNOWN, JobProgress.UNKNOWN, JobProgress.UNKNOWN, "Error during job progress retrieval");
        }
    }

    @Override
    public JobProgress getProgressById(String progressId) {
        try {
            return progressService.getProgressById(progressId);
        } catch (Exception e) {
            log.error("Error during job progress retrieval", e);
            return new JobProgress(JobProgress.UNKNOWN, JobProgress.UNKNOWN, JobProgress.UNKNOWN, "Error during job progress retrieval");
        }
    }

    void logUnexpectedException(String jobProgressId, String operation, Exception e){
        progressService.failJob(jobProgressId,
                "Internal error during Instance " + operation +", please contact our support.");
        log.error("Exception during Instance " + operation, e);
    }

}
