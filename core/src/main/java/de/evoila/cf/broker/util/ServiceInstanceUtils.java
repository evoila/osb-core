package de.evoila.cf.broker.util;

import de.evoila.cf.broker.model.ServerAddress;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceInstanceUtils {

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

    public static ServerAddress serverAddress(String name, String host, int port) {
        return new ServerAddress(name, host, port);
    }
}
