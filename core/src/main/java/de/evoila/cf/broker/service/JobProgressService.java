/**
 * 
 */
package de.evoila.cf.broker.service;

import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.JobRepository;
import org.springframework.stereotype.Service;

/**
 * @author Christian Brinker, evoila.
 * @author Marco Di Martino
 *
 */
@Service
public class JobProgressService  {

	private JobRepository jobRepository;

	public JobProgressService(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	public JobProgress getProgress(String id) {
		return jobRepository.getJobProgress(id);
	}

	public void startJob(ServiceInstance serviceInstance, String description, String operation) {
		jobRepository.saveJobProgress(serviceInstance.getId(), JobProgress.IN_PROGRESS, description, operation);
	}

	public void failJob(ServiceInstance serviceInstance, String description) {
		jobRepository.updateJobProgress(serviceInstance.getId(), JobProgress.FAILED, description);
	}

	public void succeedProgress(ServiceInstance serviceInstance, String description) {
		jobRepository.updateJobProgress(serviceInstance.getId(), JobProgress.SUCCESS, description);
	}
	public void startJob(String bindingId, String description, String operation) {
		jobRepository.saveJobProgress(bindingId, JobProgress.IN_PROGRESS, description, operation);
	}

	public void failJob(String bindingId, String description) {
		jobRepository.updateJobProgress(bindingId, JobProgress.FAILED, description);
	}

	public void succeedProgress(String bindingId, String description) {
		jobRepository.updateJobProgress(bindingId, JobProgress.SUCCESS, description);
	}



}
