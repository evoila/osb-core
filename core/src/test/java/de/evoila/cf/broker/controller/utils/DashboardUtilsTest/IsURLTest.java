package de.evoila.cf.broker.controller.utils.DashboardUtilsTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.controller.utils.DashboardUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A colon in the URL is treated specially by the constructor of {@link java.net.URL(String)}.
 * Therefore there are not all cases covered when using a colon in the string.
 */
@SuppressWarnings("InnerClassMayBeStatic")
class IsURLTest extends BaseTest {

    @Nested
    class valid {

        @Test
        void onlyOneSlashInScheme() {
            boolean result = DashboardUtils.isURL("https:/www.test.com");
            assertTrue(result);
        }

        @Test
        void moreThanTwoSlashesInScheme() {
            boolean result = DashboardUtils.isURL("https://///////////////////www.test.com");
            assertTrue(result);
        }

        @Test
        void randomStringWithoutColon() {
            boolean result = DashboardUtils.isURL("https://ui.ad7.89;_P34zö5fiwej.e5lwah5gas.vffwäopppß0üä/&/()ö54öu7tOP%&T§");
            assertTrue(result);
        }

        @Test
        void colonWithNormalPort() {
            boolean result = DashboardUtils.isURL("https://www.test.com:443/search");
            assertTrue(result);
        }

    }

    @Nested
    class invalid {

        @Test
        void nullString() {
            boolean result = DashboardUtils.isURL(null);
            assertFalse(result);
        }

        @Test
        void emptyString() {
            boolean result = DashboardUtils.isURL("");
            assertFalse(result);
        }

        @Test
        void colonFollowedByLetter() {
            boolean result = DashboardUtils.isURL("https://www.test.com:d/search");
            assertFalse(result);
        }

        @Test
        void colonFollowedByNumbersAndLetter() {
            boolean result = DashboardUtils.isURL("https://www.test.com:234d/search");
            assertFalse(result);
        }

        @Nested
        class scheme {

            @Test
            void missing() {
                boolean result = DashboardUtils.isURL("www.test.com");
                assertFalse(result);
            }

            @Test
            void unknownProtocol() {
                boolean result = DashboardUtils.isURL("xxxxxx://www.test.com");
                assertFalse(result);
            }

            @Test
            void noColon() {
                boolean result = DashboardUtils.isURL("https//www.test.com");
                assertFalse(result);
            }

        }

    }

}
