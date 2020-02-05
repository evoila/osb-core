package de.evoila.cf.broker.util;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceRequest;
import de.evoila.cf.broker.model.ServiceInstanceUpdateRequest;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.repository.JobRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Johannes Hiemer-
 */
@Service
public class ServiceInstanceUtils {

    private static String USERNAME = "user";
    private static String PASSWORD = "password";
    private static String HOSTNAME = "hostname";
    private static String PORT = "port";
    private static String HOSTS = "hosts";

    private JobRepository jobRepository;
    private ServiceInstanceRepository serviceInstanceRepository;

    public ServiceInstanceUtils(JobRepository jobRepository, ServiceInstanceRepository serviceInstanceRepository) {
        this.jobRepository = jobRepository;
        this.serviceInstanceRepository = serviceInstanceRepository;
    }

    public static List<ServerAddress> filteredServerAddress(List<ServerAddress> serverAddresses, String filter) {
        if (serverAddresses == null) {
            return Collections.emptyList();
        }
        if (filter == null) {
            return new ArrayList<>(serverAddresses);
        }
        return serverAddresses.stream()
                .filter(s -> s.getName().contains(filter))
                .collect(Collectors.toList());
    }

    /**
     * Takes a List of ServerAddress and returns their IPs and Ports as a String each separated with a comma.
     * <p>
     * Example: 192.168.254.1:8080, 192.168.254.1:8080
     *
     * @param serverAddresses ServerAddress objects as a List
     * @return A String containing the ServerAddresses as Ips and ports separated with a comma.
     * If the list is null or empty, it returns an empty String.
     */
    public static String connectionUrl(List<ServerAddress> serverAddresses) {
        if (serverAddressesNullOrEmpty(serverAddresses)) {
            return "";
        }
        String url = "";
        for (ServerAddress serverAddress : serverAddresses) {
            if (serverAddress.getIp() != null) {
                url = concatServerAddressToUrl(url, serverAddress);
            }
        }
        return url;
    }

    /**
     * Takes a single ServerAddress object and adds it's Ip and Port to the provided url string separated by a comma. if
     *
     * @param url           the URL as a String
     * @param serverAddress the ServerAddress containing the IP and Port that are supposed to be concat to the URL
     * @return the initial url with the added server address, separated by a comma. If the url is an empty String it returns the
     * Ip and port of the ServerAddress object.
     */
    private static String concatServerAddressToUrl(String url, ServerAddress serverAddress) {
        if (!url.isEmpty()) {
            url = url.concat(",");
        }

        return url.concat(serverAddress.getIp() + ":" + serverAddress.getPort());
    }


    public static Map<String, Object> bindingObject(List<ServerAddress> serverAddresses,
                                                    String username,
                                                    String password,
                                                    Map<String, Object> additionalConfigs) {
        if (serverAddressesNullOrEmpty(serverAddresses)) {
            return Collections.emptyMap();
        }

        Map<String, Object> credentials = new HashMap<>();
        if (serverAddresses.size() == 1) {
            credentials.put(HOSTNAME, serverAddresses.get(0).getIp());
            credentials.put(PORT, serverAddresses.get(0).getPort());
        } else {
            List<Map<String, Object>> hosts = new ArrayList<>();
            serverAddresses.forEach(serverAddress -> hosts.add(new HashMap<>() {{
                put(HOSTNAME, serverAddress.getIp());
                put(PORT, serverAddress.getPort());
            }}));

            credentials.put(HOSTS, hosts);
        }

        if (!StringUtils.isEmpty(username))
            credentials.put(USERNAME, username);

        if (!StringUtils.isEmpty(password))
            credentials.put(PASSWORD, password);

        if (additionalConfigs != null) {
            credentials.putAll(additionalConfigs);
        }

        return credentials;
    }

    public static String hostList(List<ServerAddress> serverAddresses) {
        if (serverAddressesNullOrEmpty(serverAddresses)) {
            return "";
        }
        String hosts = "";
        for (ServerAddress serverAddress : serverAddresses) {
            String ip = serverAddress.getIp();
            if (ip == null) {
                continue;
            }
            if (hosts.length() > 0)
                hosts = hosts.concat(",");

            hosts = hosts.concat(ip);
        }
        return hosts;
    }

    public static String portList(List<ServerAddress> serverAddresses) {
        if (serverAddressesNullOrEmpty(serverAddresses)) {
            return "";
        }
        String ports = "";
        for (ServerAddress serverAddress : serverAddresses) {
            if (ports.length() > 0)
                ports = ports.concat(",");

            ports = ports.concat(String.valueOf(serverAddress.getPort()));
        }
        return ports;
    }

    public static ServerAddress serverAddress(String name, String host, int port) {
        return new ServerAddress(name, host, port);
    }

    /**
     * Checks whether a service creation with the given ServiceInstanceRequest would provision an identical service instance.
     * <p>
     * This method heavily relies on the {@linkplain Object#equals(Object)} method to check for equality
     * and resulting non-effective changes to a field. So it is mandatory for all objects contained in
     * {@linkplain ServiceInstance#getParameters()} to have an overwritten equals method or can ensure equality by identity.
     *
     * @param serviceInstanceId the service instance object to compare with the provision request
     * @param request           the provision request object to compare with the service instance
     * @param serviceInstance   the service instance object to compare with the provision request
     * @return true if provisioning would create an identical instance and false if it would not
     */
    public static boolean wouldCreateIdenticalInstance(String serviceInstanceId, ServiceInstanceRequest request, ServiceInstance serviceInstance) {
        if (StringUtils.isEmpty(serviceInstanceId) || request == null || serviceInstance == null) return true;
        if (request.getContext() == null ^ serviceInstance.getContext() == null) return false;
        if (request.getParameters() == null ^ serviceInstance.getParameters() == null) return false;

        return serviceInstanceId.equals(serviceInstance.getId()) &&
                Objects.equals(request.getServiceDefinitionId(), serviceInstance.getServiceDefinitionId()) &&
                Objects.equals(request.getPlanId(), serviceInstance.getPlanId()) &&
                Objects.equals(request.getOrganizationGuid(), serviceInstance.getOrganizationGuid()) &&
                Objects.equals(request.getSpaceGuid(), serviceInstance.getSpaceGuid()) &&
                compareContext(request, serviceInstance) &&
                Objects.equals(request.getParameters(), serviceInstance.getParameters());
    }

    /**
     * A null safe comparison of context objects from a request and service instance.
     *
     * @param request         The ServiceInstanceRequest that may holds a context object
     * @param serviceInstance The ServiceInstance that may holds a context objects
     * @return true if both context objects are null or they are equal.
     */
    private static boolean compareContext(ServiceInstanceRequest request, ServiceInstance serviceInstance) {
        return request.getContext() == null ? serviceInstance.getContext() == null
                : request.getContext().equals(serviceInstance.getContext());
    }

    /**
     * Checks whether an update with the given ServiceInstanceUpdateRequest would effectively change the service instance.
     * <p>
     * This method heavily relies on the {@linkplain Object#equals(Object)} method to check for equality
     * and resulting non-effective changes to a field. So it is mandatory for all objects contained in
     * {@linkplain ServiceInstance#getParameters()} to have an overwritten equals method or can ensure equality by identity.
     * <p>
     * If either of the two values is null, the return value is always false.
     *
     * @param serviceInstance the service instance object to compare with the update request
     * @param request         the update request object to compare with the service instance
     * @return true if update would have effects and false if it would not or a parameter is null
     */
    public static boolean isEffectivelyUpdating(ServiceInstance serviceInstance, ServiceInstanceUpdateRequest request) {
        if (serviceInstance == null || request == null) {
            return false;
        }

        return (request.getContext() == null ^ serviceInstance.getContext() == null) ||
                (request.getParameters() == null ^ serviceInstance.getParameters() == null) ||
                !Objects.equals(request.getServiceDefinitionId(), serviceInstance.getServiceDefinitionId()) ||
                !Objects.equals(request.getPlanId(), serviceInstance.getPlanId()) ||
                !Objects.equals(request.getContext(), serviceInstance.getContext()) ||
                !Objects.equals(request.getParameters(), serviceInstance.getParameters());
    }

    private static boolean serverAddressesNullOrEmpty(List<ServerAddress> serverAddresses) {
        return serverAddresses == null || serverAddresses.isEmpty();
    }

    /**
     * Checks whether the given service instance is currently blocked by another operation by checking for {@linkplain JobProgress#IN_PROGRESS}
     * as the state of the available JobProgress object in the storage.
     * This method also takes in account, whether the targeted action is equal to the running operation,
     * in which case the service instance is marked as NOT blocked.
     *
     * @param serviceInstance service instance to check for active operations
     * @param action          desired action to take
     * @return false if service instance has no running operations or the action is equal to the running operation; true otherwise
     */
    public boolean isBlocked(ServiceInstance serviceInstance, String action) {
        if (serviceInstance == null || StringUtils.isEmpty(action)) return false;

        JobProgress jobProgress = jobRepository.getJobProgressByReferenceId(serviceInstance.getId());
        if (jobProgress == null) return false;

        return jobProgress.isInProgress() && !jobProgress.getOperation().equals(action);
    }

    /**
     * Searches for the service instance with the given serviceInstanceId and then calls {@linkplain #isBlocked(ServiceInstance, String)}.
     *
     * @param serviceInstanceId id of the service instance to search with
     * @param action            desired action to take
     * @return false if service instance has no running operations or the action is equal to the running operation; true otherwise
     * @throws ServiceInstanceDoesNotExistException if no service instance was found with the given serviceInstanceId
     */
    public boolean isBlocked(String serviceInstanceId, String action) throws ServiceInstanceDoesNotExistException {
        return isBlocked(serviceInstanceRepository.getServiceInstance(serviceInstanceId), action);
    }
}
