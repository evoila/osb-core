package de.evoila.cf.broker.util.ServiceInstanceUtilsTest;

import de.evoila.cf.broker.model.ServiceInstanceRequest;
import de.evoila.cf.broker.model.context.Context;
import de.evoila.cf.broker.util.ServiceInstanceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WouldCreateIdenticalInstanceTest extends BaseTest {

    @Mock
    private ServiceInstanceRequest request;

    private Map<String, Object> parameters = Map.of("Key", "Value");

    @Nested
    class returnsTrue {

        @Test
        void withNullInstanceId() {
            boolean result = ServiceInstanceUtils.wouldCreateIdenticalInstance(null, request, serviceInstance);
            assertTrue(result);
        }

        @Test
        void withEmptyInstanceId() {
            boolean result = ServiceInstanceUtils.wouldCreateIdenticalInstance("", request, serviceInstance);
            assertTrue(result);
        }

        @Test
        void withNullRequest() {
            boolean result = ServiceInstanceUtils.wouldCreateIdenticalInstance(HAPPY_SERVICE_INSTANCE_ID, null, serviceInstance);
            assertTrue(result);
        }

        @Test
        void withNullServiceInstance() {
            boolean result = ServiceInstanceUtils.wouldCreateIdenticalInstance(HAPPY_SERVICE_INSTANCE_ID, request, null);
            assertTrue(result);
        }

        @Test
        void withAllValid() {
            when(serviceInstance.getId()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(request.getServiceDefinitionId()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(serviceInstance.getServiceDefinitionId()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(request.getPlanId()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(serviceInstance.getPlanId()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(request.getOrganizationGuid()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(serviceInstance.getOrganizationGuid()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(request.getSpaceGuid()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(serviceInstance.getSpaceGuid()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(request.getContext()).thenReturn(context);
            when(serviceInstance.getContext()).thenReturn(context);
            when(request.getParameters()).thenReturn(parameters);
            when(serviceInstance.getParameters()).thenReturn(parameters);
            boolean result = ServiceInstanceUtils.wouldCreateIdenticalInstance(HAPPY_SERVICE_INSTANCE_ID, request, serviceInstance);
            assertTrue(result);
        }
    }

    @Nested
    class returnsFalse {

        private void testForFalse() {
            boolean result = ServiceInstanceUtils.wouldCreateIdenticalInstance(HAPPY_SERVICE_INSTANCE_ID, request, serviceInstance);
            assertFalse(result);
        }

        @Nested
        class withOneContextNull {

            @Test
            void withRequestContextNull() {
                when(request.getContext()).thenReturn(null);
                when(serviceInstance.getContext()).thenReturn(context);
                testForFalse();
            }

            @Test
            void withServiceInstanceContextNull() {
                when(request.getContext()).thenReturn(context);
                when(serviceInstance.getContext()).thenReturn(null);
                testForFalse();
            }
        }

        @Nested
        class withOneParametersMapNull {

            @Test
            void withRequestParametersNull() {
                when(request.getParameters()).thenReturn(null);
                when(serviceInstance.getParameters()).thenReturn(Collections.emptyMap());
                testForFalse();
            }

            @Test
            void withServiceInstanceParametersNull() {
                when(request.getParameters()).thenReturn(Collections.emptyMap());
                when(serviceInstance.getParameters()).thenReturn(null);
                testForFalse();
            }

        }

        @Nested
        class withDifferentInstanceIds {

            @Test
            void withServiceInstanceGetIdReturningNull() {
                when(serviceInstance.getId()).thenReturn(null);
                testForFalse();
            }

            @Test
            void withServiceInstanceGetIdReturningEmptyId() {
                when(serviceInstance.getId()).thenReturn("");
                testForFalse();
            }

            @Test
            void withServiceInstanceGetIdReturningOtherId() {
                when(serviceInstance.getId()).thenReturn("Mock");
                testForFalse();
            }

        }

        @Nested
        class withDifferentServiceDefinitionIds {

            @BeforeEach
            void setUp() {
                when(serviceInstance.getId()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            }

            @Nested
            class withOneNull {

                @Test
                void withRequestIdNull() {
                    when(request.getServiceDefinitionId()).thenReturn(null);
                    when(serviceInstance.getServiceDefinitionId()).thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                    testForFalse();
                }

                @Test
                void withServiceInstanceIdNull() {
                    when(request.getServiceDefinitionId()).thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                    when(serviceInstance.getServiceDefinitionId()).thenReturn(null);
                    testForFalse();
                }

            }

            @Nested
            class withOneIdEmpty {

                @Test
                void withRequestIdEmpty() {
                    when(request.getServiceDefinitionId()).thenReturn("");
                    when(serviceInstance.getServiceDefinitionId()).thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                    testForFalse();
                }

                @Test
                void withServiceInstanceIdEmpty() {
                    when(request.getServiceDefinitionId()).thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                    when(serviceInstance.getServiceDefinitionId()).thenReturn("");
                    testForFalse();
                }

            }

            @Test
            void withRequestIdOtherId() {
                when(request.getServiceDefinitionId()).thenReturn("Mock");
                when(serviceInstance.getServiceDefinitionId()).thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                testForFalse();
            }

        }

        @Nested
        class withDifferentPlanIds {

            @BeforeEach
            void setUp() {
                when(serviceInstance.getId()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            }

            @Nested
            class withOneNull {

                @Test
                void withRequestIdNull() {
                    when(request.getPlanId()).thenReturn(null);
                    when(serviceInstance.getPlanId()).thenReturn(HAPPY_PLAN_ID);
                    testForFalse();
                }

                @Test
                void withServiceInstanceIdNull() {
                    when(request.getPlanId()).thenReturn(HAPPY_PLAN_ID);
                    when(serviceInstance.getPlanId()).thenReturn(null);
                    testForFalse();
                }

            }

            @Nested
            class withOneIdEmpty {

                @Test
                void withRequestIdEmpty() {
                    when(request.getPlanId()).thenReturn("");
                    when(serviceInstance.getPlanId()).thenReturn(HAPPY_PLAN_ID);
                    testForFalse();
                }

                @Test
                void withServiceInstanceIdEmpty() {
                    when(request.getPlanId()).thenReturn(HAPPY_PLAN_ID);
                    when(serviceInstance.getPlanId()).thenReturn("");
                    testForFalse();
                }
            }

            @Test
            void withRequestIdOtherId() {
                when(request.getPlanId()).thenReturn("Mock");
                when(serviceInstance.getPlanId()).thenReturn(HAPPY_PLAN_ID);
                testForFalse();
            }
        }

        @Nested
        class withDifferentOrganizationGuids {

            @BeforeEach
            void setUp() {
                when(serviceInstance.getId()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            }

            @Nested
            class withOneNull {

                @Test
                void withRequestIdNull() {
                    when(request.getOrganizationGuid()).thenReturn(null);
                    when(serviceInstance.getOrganizationGuid()).thenReturn(HAPPY_ORGANIZATION_GUID);
                    testForFalse();
                }

                @Test
                void withServiceInstanceIdNull() {
                    when(request.getOrganizationGuid()).thenReturn(HAPPY_ORGANIZATION_GUID);
                    when(serviceInstance.getOrganizationGuid()).thenReturn(null);
                    testForFalse();
                }
            }

            @Nested
            class withOneIdEmpty {

                @Test
                void withRequestIdEmpty() {
                    when(request.getOrganizationGuid()).thenReturn("");
                    when(serviceInstance.getOrganizationGuid()).thenReturn(HAPPY_ORGANIZATION_GUID);
                    testForFalse();
                }

                @Test
                void withServiceInstanceIdEmpty() {
                    when(request.getOrganizationGuid()).thenReturn(HAPPY_ORGANIZATION_GUID);
                    when(serviceInstance.getOrganizationGuid()).thenReturn("");
                    testForFalse();
                }

            }

            @Test
            void withRequestIdOtherId() {
                when(request.getOrganizationGuid()).thenReturn("Mock");
                when(serviceInstance.getOrganizationGuid()).thenReturn(HAPPY_ORGANIZATION_GUID);
                testForFalse();
            }

        }

        @Nested
        class withDifferentSpaceGUIDs {

            @BeforeEach
            void setUp() {
                when(serviceInstance.getId())
                        .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            }

            @Nested
            class withOneNull {

                @Test
                void withRequestIdNull() {
                    when(request.getSpaceGuid()).thenReturn(null);
                    when(serviceInstance.getSpaceGuid()).thenReturn(HAPPY_SPACE_GUID);
                    testForFalse();
                }

                @Test
                void withServiceInstanceIdNull() {
                    when(request.getSpaceGuid()).thenReturn(HAPPY_SPACE_GUID);
                    when(serviceInstance.getSpaceGuid()).thenReturn(null);
                    testForFalse();
                }
            }

            @Nested
            class withOneIdEmpty {

                @Test
                void withRequestIdEmpty() {
                    when(request.getSpaceGuid()).thenReturn("");
                    when(serviceInstance.getSpaceGuid()).thenReturn(HAPPY_SPACE_GUID);
                    testForFalse();
                }

                @Test
                void withServiceInstanceIdEmpty() {
                    when(request.getSpaceGuid()).thenReturn(HAPPY_SPACE_GUID);
                    when(serviceInstance.getSpaceGuid()).thenReturn("");
                    testForFalse();
                }
            }

            @Test
            void withRequestIdOtherId() {
                when(request.getSpaceGuid()).thenReturn("Mock");
                when(serviceInstance.getSpaceGuid()).thenReturn(HAPPY_SPACE_GUID);
                testForFalse();
            }

        }

        @Test
        void withDifferentContexts() {
            when(serviceInstance.getId()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            Context secondContext = mock(Context.class);
            when(request.getContext()).thenReturn(context);
            when(serviceInstance.getContext()).thenReturn(secondContext);
            // By default mocked objects only test equality over references, which is ok here
            testForFalse();
        }

        @Test
        void withDifferentParameters() {
            when(serviceInstance.getId()).thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(request.getParameters()).thenReturn(Collections.emptyMap());
            when(serviceInstance.getParameters()).thenReturn(parameters);
            testForFalse();
        }
    }
}
