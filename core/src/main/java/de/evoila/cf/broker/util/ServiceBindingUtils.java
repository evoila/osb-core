package de.evoila.cf.broker.util;

import de.evoila.cf.broker.exception.ServiceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.repository.BindingRepository;
import de.evoila.cf.broker.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.NoSuchElementException;

/**
 * @author Marius Berger
 */
@Service
public class ServiceBindingUtils {

    private JobRepository jobRepository;
    private BindingRepository bindingRepository;

    public ServiceBindingUtils(BindingRepository bindingRepository, JobRepository jobRepository) {
        this.bindingRepository = bindingRepository;
        this.jobRepository = jobRepository;
    }

    /**
     * Checks whether the given service instance binding is currently blocked by another operation by checking for {@linkplain JobProgress#IN_PROGRESS}
     * as the state of the available JobProgress object in the storage.
     * This method also takes in account, whether the targeted action is equal to the running operation,
     * in which case the service instance binding is marked as NOT blocked.
     * @param binding service instance binding to check for active operations
     * @param action desired action to take
     * @return false if service instance binding has no running operations or the action is equal to the running operation; true otherwise
     */
    public boolean isBlocked(ServiceInstanceBinding binding, String action) {
        if (binding == null || ObjectUtils.isEmpty(action)) return false;

        JobProgress jobProgress;
        try {
            jobProgress = jobRepository.getJobProgressByReferenceId(binding.getId());
        } catch (NoSuchElementException ex) {
            jobProgress = null;
        }
        if (jobProgress == null) return false;

        return jobProgress.isInProgress() && !jobProgress.getOperation().equals(action);
    }

    /**
     * Searches for the service instance binding with the given serviceInstanceId and
     * then calls {@linkplain #isBlocked(ServiceInstanceBinding, String)}.
     * @param serviceBindingId id of the service instance binding to search with
     * @param action desired action to take
     * @return false if service instance binding has no running operations or the action is equal to the running operation; true otherwise
     * @throws ServiceInstanceBindingDoesNotExistsException if no service instance binding was found with the given serviceInstanceId
     */
    public boolean isBlocked(String serviceBindingId, String action) throws ServiceInstanceBindingDoesNotExistsException {
        if (!bindingRepository.containsInternalBindingId(serviceBindingId)) {

            throw new ServiceInstanceBindingDoesNotExistsException(serviceBindingId);
        }
        ServiceInstanceBinding binding = bindingRepository.findOne(serviceBindingId);
        return isBlocked(binding, action);
    }
}
