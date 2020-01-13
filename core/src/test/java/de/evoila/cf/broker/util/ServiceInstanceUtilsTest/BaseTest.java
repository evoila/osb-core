package de.evoila.cf.broker.util.ServiceInstanceUtilsTest;

import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.context.Context;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class BaseTest {

    static final String USERNAME = "user";
    static final String PASSWORD = "password";
    static final String HOSTNAME = "hostname";
    static final String PORT = "port";
    static final String HOSTS = "hosts";

    static final String HAPPY_SERVICE_INSTANCE_ID   = "57817290-9c56-453d-8961-eb4f80c427a6";
    static final String HAPPY_SERVICE_DEFINITION_ID = "83927042-5573-41dc-ae51-12e13f9ee42f";
    static final String HAPPY_PLAN_ID               = "d0851f8d-c201-4e11-8453-7dbbcdaf8ec3";
    static final String HAPPY_ORGANIZATION_GUID     = "ac80b58e-5c29-4f1c-897c-b2c36326ff0f";
    static final String HAPPY_SPACE_GUID            = "4ec8d1c7-bba0-4da1-ad70-8ac2d0f0eb44";

    @Mock
    ServiceInstance serviceInstance;
    @Mock
    Context context;

}
