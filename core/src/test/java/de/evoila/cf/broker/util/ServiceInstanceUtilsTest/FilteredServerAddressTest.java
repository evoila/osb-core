package de.evoila.cf.broker.util.ServiceInstanceUtilsTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.util.ServiceInstanceUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class FilteredServerAddressTest extends BaseTest {

    private List<ServerAddress> serverAddresses = List.of(new ServerAddress("Heinz"),
            new ServerAddress("Ralf"),
            new ServerAddress("Gustav"),
            new ServerAddress("Olaf"),
            new ServerAddress("Ralf"));

    @Test
    void withNullServerAddressList() {
        List<ServerAddress> result = ServiceInstanceUtils.filteredServerAddress(null, "Shouldn't matter");
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void withNullFilter() {
        List<ServerAddress> result = ServiceInstanceUtils.filteredServerAddress(serverAddresses, null);
        assertNotSame(serverAddresses, result);
        assertEquals(serverAddresses, result);
    }

    @Nested
    class withValidListAndFilter {

        @Test
        void withNoMatch() {
            List<ServerAddress> result = ServiceInstanceUtils.filteredServerAddress(serverAddresses, "NoMatch");
            assertEquals(Collections.emptyList(), result);
        }

        @Test
        void withEmptyFilterMatchingAll() {
            List<ServerAddress> result = ServiceInstanceUtils.filteredServerAddress(serverAddresses, "");
            assertNotSame(serverAddresses, result);
            assertEquals(serverAddresses, result);
        }

        @Test
        void withOneMatch() {
            List<ServerAddress> expectedResult = List.of(serverAddresses.get(2));
            List<ServerAddress> result = ServiceInstanceUtils.filteredServerAddress(serverAddresses, "Gustav");
            assertEquals(expectedResult, result);
        }

        @Test
        void withTwoMatchesWithSameName() {
            List<ServerAddress> expectedResult = List.of(serverAddresses.get(1), serverAddresses.get(4));
            List<ServerAddress> result = ServiceInstanceUtils.filteredServerAddress(serverAddresses, "Ralf");
            assertEquals(expectedResult, result);
        }

        @Test
        void withFourMatchesWhichContainA() {
            List<ServerAddress> expectedResult = List.of(serverAddresses.get(1),
                    serverAddresses.get(2),
                    serverAddresses.get(3),
                    serverAddresses.get(4));
            List<ServerAddress> result = ServiceInstanceUtils.filteredServerAddress(serverAddresses, "a");
            assertEquals(expectedResult, result);
        }
    }
}
