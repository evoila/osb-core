
package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * @author Christian Brinker, Marco Di Martino, Johannes Hiemer, Johannes Strauß.
 */
@Service
public class JobProgressService {

	private JobRepository jobRepository;

    private static Logger log = LoggerFactory.getLogger(JobProgressService.class);

	public JobProgressService(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	public JobProgress getProgressById(String id) {
		return jobRepository.getJobProgressById(id);
	}

    public JobProgress getProgressByReferenceId(String referenceId) {
        return jobRepository.getJobProgressByReferenceId(referenceId);
    }

	public JobProgress startJob(String id, String referenceId, String description, String operation) throws ServiceBrokerException {
        JobProgress jobProgress = jobRepository.saveJobProgress(id, referenceId, JobProgress.IN_PROGRESS, description, operation);

        if (jobProgress == null) {
            throw new ServiceBrokerException("An exception occurred while saving new JobProgress object with Id: " + id);
        }

        return jobProgress;
    }

    public JobProgress failJob(String id, String description) throws ServiceBrokerException {
        try {
            return jobRepository.updateJobProgress(id, JobProgress.FAILED, description);
        } catch (NoSuchElementException e) {
            throw new ServiceBrokerException("An exception occurred while updating JobProgress object with Id: " + id);
        }
    }

    public JobProgress succeedProgress(String id, String description) throws ServiceBrokerException {
        try {
            return jobRepository.updateJobProgress(id, JobProgress.SUCCESS, description);
        } catch (NoSuchElementException e) {
            log.error("Could update JobProgress object with id: " + id, e);
            throw new ServiceBrokerException("An Error occurred while updating new JobProgress object with Id:" + id);
        }
    }

}
