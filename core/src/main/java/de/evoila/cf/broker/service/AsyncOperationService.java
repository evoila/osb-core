package de.evoila.cf.broker.service;

import de.evoila.cf.broker.model.JobProgress;

/**
 * @author Johannes Hiemer.
 */
public interface AsyncOperationService {

    JobProgress getProgressByReferenceId(String referenceId);

    JobProgress getProgressById(String progressId);
}
