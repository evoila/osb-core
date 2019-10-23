package de.evoila.cf.broker.util.ServiceInstanceUtilsTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.util.ServiceInstanceUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerAddressTest extends BaseTest {

    private static final String HAPPY_NAME  = "Name";
    private static final String HAPPY_IP    = "Ip";
    private static final int HAPPY_PORT = 443;

    @Test
    void withAllNull() {
        ServerAddress expectedResult = new ServerAddress(null,
                                                         null,
                                                         0);
        ServerAddress result = ServiceInstanceUtils.serverAddress(null,
                                                                  null,
                                                                  0);
        assertEquals(expectedResult, result);
    }

    @Test
    void withNameNull() {
        ServerAddress expectedResult = new ServerAddress(null,
                                                         HAPPY_IP,
                                                         HAPPY_PORT);
        ServerAddress result = ServiceInstanceUtils.serverAddress(null,
                                                                  HAPPY_IP,
                                                                  HAPPY_PORT);
        assertEquals(expectedResult, result);
    }

    @Test
    void withIpNull() {
        ServerAddress expectedResult = new ServerAddress(HAPPY_NAME,
                                                         null,
                                                         HAPPY_PORT);
        ServerAddress result = ServiceInstanceUtils.serverAddress(HAPPY_NAME,
                                                                  null,
                                                                  HAPPY_PORT);
        assertEquals(expectedResult, result);
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    @Nested
    class withInvalidPort {

        @Test
        void withNegative() {
            ServerAddress expectedResult = new ServerAddress(HAPPY_NAME,
                                                             null,
                                                             -1);
            ServerAddress result = ServiceInstanceUtils.serverAddress(HAPPY_NAME,
                                                                      null,
                                                                      -1);
            assertEquals(expectedResult, result);
        }

        @Test
        void withTooBig() {
            ServerAddress expectedResult = new ServerAddress(HAPPY_NAME,
                                                             null,
                                                             9999999);
            ServerAddress result = ServiceInstanceUtils.serverAddress(HAPPY_NAME,
                                                                      null,
                                                                      9999999);
            assertEquals(expectedResult, result);
        }

    }

}
