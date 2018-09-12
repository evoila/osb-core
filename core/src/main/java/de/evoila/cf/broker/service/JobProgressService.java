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
 *
 */
@Service
public class JobProgressService  {

	private JobRepository jobRepository;

	public JobProgressService(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	public JobProgress getProgress(String serviceInstanceId) {
		return jobRepository.getJobProgress(serviceInstanceId);
	}

	public void startJob(ServiceInstance serviceInstance) {
		changeStatus(serviceInstance, JobProgress.IN_PROGRESS);
	}

	public void failJob(ServiceInstance serviceInstance, String description) {
		changeStatus(serviceInstance, JobProgress.FAILED);
	}

	public void succeedProgress(ServiceInstance serviceInstance) {
		changeStatus(serviceInstance, JobProgress.SUCCESS);
	}

	private void changeStatus(ServiceInstance serviceInstance, String newStatus) {
		jobRepository.saveOrUpdateJobProgress(serviceInstance.getId(), newStatus);
	}
}
