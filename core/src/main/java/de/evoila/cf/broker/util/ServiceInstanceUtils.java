package de.evoila.cf.broker.util;

import de.evoila.cf.broker.model.ServerAddress;

import java.util.List;

public class ServiceInstanceUtils {

    public static ServerAddress filteredServerAddress(List<ServerAddress> serverAddresses, String filter) {
        ServerAddress serverAddress = null;
        if (serverAddresses.size() == 1)
            serverAddress = serverAddresses.get(0);
        else {
            serverAddress = serverAddresses.stream()
                    .map(s -> {
                        if (s.getName().contains(filter))
                            return s;

                        return null;
                    }).findFirst().get();
        }
        return serverAddress;
    }
}
