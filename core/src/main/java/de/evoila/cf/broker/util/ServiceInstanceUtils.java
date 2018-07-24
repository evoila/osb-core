package de.evoila.cf.broker.util;

import de.evoila.cf.broker.model.ServerAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceInstanceUtils {

    private static String USERNAME = "user";
    private static String PASSWORD = "password";
    private static String HOSTNAME = "hostname";
    private static String PORT = "port";
    private static String HOSTS = "hosts";

    public static List<ServerAddress> filteredServerAddress(List<ServerAddress> serverAddresses, String filter) {
        return serverAddresses.stream()
                .filter(s -> {
                    if (s.getName().contains(filter))
                        return true;

                    return false;
                }).collect(Collectors.toList());
    }

    public static String connectionUrl(List<ServerAddress> serverAddresses) {
        String url = "";
        for (ServerAddress serverAddress : serverAddresses) {
            if (url.length() > 0)
                url = url.concat(",");

            url = url.concat(serverAddress.getIp() + ":" + serverAddress.getPort());
        }
        return url;
    }

    public static Map<String, Object> bindingObject(List<ServerAddress> serverAddresses,
                                                    String username, String password, Map<String, Object> additionalConfigs) {
        Map<String, Object> credentials = new HashMap<>();

        if (serverAddresses.size() == 1) {
            credentials.put(HOSTNAME, serverAddresses.get(0).getIp());
            credentials.put(PORT, serverAddresses.get(0).getPort());
        } else {
            List<Map<String, Object>> hosts = new ArrayList<>();
            serverAddresses.forEach(serverAddress -> {
                hosts.add(new HashMap<String, Object>() {{
                    put(HOSTNAME, serverAddress.getIp());
                    put(PORT, serverAddress.getPort());
                }});
            });

            credentials.put(HOSTS, hosts);
        }
        credentials.put(USERNAME, username);
        credentials.put(PASSWORD, password);
        credentials.putAll(additionalConfigs);

        return credentials;
    }

    public static String hostList(List<ServerAddress> serverAddresses) {
        String hosts = "";
        for (ServerAddress serverAddress : serverAddresses) {
            if (hosts.length() > 0)
                hosts = hosts.concat(",");

            hosts = hosts.concat(serverAddress.getIp());
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
}