package de.evoila.cf.broker.controller.utils.DashboardUtilsTest;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.evoila.cf.broker.model.catalog.Dashboard;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;

@ExtendWith(MockitoExtension.class)
abstract class BaseTest {

    static final String     HAPPY_DASHBOARD_URL = "https://www.test.com";
    static final String     HAPPY_INSTANCE_ID   = "975834d6-bb9f-4825-98f2-ddaad251a6d3";
    static final String     HAPPY_REDIRECT_URI  = "https://www.test2.com";
    static final String[]   HAPPY_APPENDIXES    = {"first",
                                                   "second",
                                                   "third"};

    @Mock
    ServiceDefinition serviceDefinition;
    @Mock
    Dashboard dashboard;

}
