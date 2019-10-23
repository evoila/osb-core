package de.evoila.cf.broker.util;

import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceRequest;
import de.evoila.cf.broker.model.ServiceInstanceUpdateRequest;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Johannes Hiemer-
 */
public class ServiceInstanceUtils {

    private static String USERNAME = "user";
    private static String PASSWORD = "password";
    private static String HOSTNAME = "hostname";
    private static String PORT = "port";
    private static String HOSTS = "hosts";

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

    public static String connectionUrl(List<ServerAddress> serverAddresses) {
        if (serverAddresses == null) {
            return "";
        }
        String url = "";
        for (ServerAddress serverAddress : serverAddresses) {
            if (serverAddress.getIp() == null) {
                continue;
            }
            if (url.length() > 0)
                url = url.concat(",");

            url = url.concat(serverAddress.getIp() + ":" + serverAddress.getPort());
        }
        return url;
    }

    public static Map<String, Object> bindingObject(List<ServerAddress> serverAddresses,
                                                    String username,
                                                    String password,
                                                    Map<String, Object> additionalConfigs) {
        if (serverAddresses == null ||
            serverAddresses.isEmpty()) {

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
        if (serverAddresses == null) {
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
     *
     * This method heavily relies on the {@linkplain Object#equals(Object)} method to check for equality
     * and resulting non-effective changes to a field. So it is mandatory for all objects contained in
     * {@linkplain ServiceInstance#getParameters()} to have an overwritten equals method or can ensure equality by identity.
     *
     * @param serviceInstanceId the service instance object to compare with the provision request
     * @param request the provision request object to compare with the service instance
     * @param serviceInstance the service instance object to compare with the provision request
     * @return true if provisioning would create an identical instance and false if it would not
     */
    public static boolean wouldCreateIdenticalInstance(String serviceInstanceId, ServiceInstanceRequest request, ServiceInstance serviceInstance) {
        if (StringUtils.isEmpty(serviceInstanceId) || request == null || serviceInstance == null) return true;
        if (request.getContext() == null ^ serviceInstance.getContext() == null) return false;
        if (request.getParameters() == null ^ serviceInstance.getParameters() == null) return false;

        return serviceInstanceId.equals(serviceInstance.getId())
                && request.getServiceDefinitionId().equals(serviceInstance.getServiceDefinitionId())
                && request.getPlanId().equals(serviceInstance.getPlanId())
                && request.getOrganizationGuid().equals(serviceInstance.getOrganizationGuid())
                && request.getSpaceGuid().equals(serviceInstance.getSpaceGuid())
                && compareContext(request, serviceInstance)
                && request.getParameters().equals(serviceInstance.getParameters());
    }

    /**
     * A null safe comparison of context objects from a request and service instance.
     *
     * @param request The ServiceInstanceRequest that may holds a context object
     * @param serviceInstance The ServiceInstance that may holds a context objects
     * @return true if both context objects are null or they are equal.
     */
    private static boolean compareContext(ServiceInstanceRequest request, ServiceInstance serviceInstance) {
        return request.getContext() == null ? serviceInstance.getContext() == null
                : request.getContext().equals(serviceInstance.getContext());
    }

     /**
     * Checks whether an update with the given ServiceInstanceUpdateRequest would effectively change the service instance.
     *
     * This method heavily relies on the {@linkplain Object#equals(Object)} method to check for equality
     * and resulting non-effective changes to a field. So it is mandatory for all objects contained in
     * {@linkplain ServiceInstance#getParameters()} to have an overwritten equals method or can ensure equality by identity.
     *
     * If either of the two values is null, the return value is always false.
     * @param serviceInstance the service instance object to compare with the update request
     * @param request the update request object to compare with the service instance
     * @return true if update would have effects and false if it would not or a parameter is null
     */
    public static boolean isEffectivelyUpdating(ServiceInstance serviceInstance, ServiceInstanceUpdateRequest request) {
        if (serviceInstance == null || request == null) return false;
        if (request.getContext() == null ^ serviceInstance.getContext() == null) return true;
        if (request.getParameters() == null ^ serviceInstance.getParameters() == null) return true;

        if (!request.getServiceDefinitionId().equals(serviceInstance.getServiceDefinitionId())
            || !request.getPlanId().equals(serviceInstance.getPlanId()))
            return true;

        return !request.getContext().equals(serviceInstance.getContext())
                || !request.getParameters().equals(serviceInstance.getParameters());
    }
}
