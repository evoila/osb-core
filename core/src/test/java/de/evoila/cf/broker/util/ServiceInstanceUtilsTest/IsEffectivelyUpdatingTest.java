package de.evoila.cf.broker.util.ServiceInstanceUtilsTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.HashMap;

import de.evoila.cf.broker.model.ServiceInstanceUpdateRequest;
import de.evoila.cf.broker.model.context.Context;
import de.evoila.cf.broker.util.ServiceInstanceUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IsEffectivelyUpdatingTest extends BaseTest {

    @Mock
    private ServiceInstanceUpdateRequest request;

    @Nested
    class returnsTrue {

        private void testForTrue() {
            boolean result = ServiceInstanceUtils.isEffectivelyUpdating(serviceInstance, request);
            assertTrue(result);
        }

        @Nested
        class withOneContextNull {

            @Test
            void withRequestContextNull() {
                when(request.getContext())
                        .thenReturn(null);
                when(serviceInstance.getContext())
                        .thenReturn(context);
                testForTrue();
            }

            @Test
            void withServiceInstanceContextNull() {
                when(request.getContext())
                        .thenReturn(context);
                when(serviceInstance.getContext())
                        .thenReturn(null);
                testForTrue();
            }

        }

        @Nested
        class withOneParametersMapNull {

            @Test
            void withRequestParametersNull() {
                when(request.getParameters())
                        .thenReturn(null);
                when(serviceInstance.getParameters())
                        .thenReturn(Collections.emptyMap());
                testForTrue();
            }

            @Test
            void withServiceInstanceParametersNull() {
                when(request.getParameters())
                        .thenReturn(Collections.emptyMap());
                when(serviceInstance.getParameters())
                        .thenReturn(null);
                testForTrue();
            }

        }

        @Nested
        class withDifferentServiceDefinitionIds {

            @Nested
            class withOneNull {

                @Test
                void withRequestIdNull() {
                    when(request.getServiceDefinitionId())
                            .thenReturn(null);
                    when(serviceInstance.getServiceDefinitionId())
                            .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                    testForTrue();
                }

                @Test
                void withServiceInstanceIdNull() {
                    when(request.getServiceDefinitionId())
                            .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                    when(serviceInstance.getServiceDefinitionId())
                            .thenReturn(null);
                    testForTrue();
                }

            }

            @Nested
            class withOneIdEmpty {

                @Test
                void withRequestIdEmpty() {
                    when(request.getServiceDefinitionId())
                            .thenReturn("");
                    when(serviceInstance.getServiceDefinitionId())
                            .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                    testForTrue();
                }

                @Test
                void withServiceInstanceIdEmpty() {
                    when(request.getServiceDefinitionId())
                            .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                    when(serviceInstance.getServiceDefinitionId())
                            .thenReturn("");
                    testForTrue();
                }

            }

            @Test
            void withRequestIdOtherId() {
                when(request.getServiceDefinitionId())
                        .thenReturn("Mock");
                when(serviceInstance.getServiceDefinitionId())
                        .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                testForTrue();
            }

        }

        @Nested
        class withDifferentPlanIds {

            @Nested
            class withOneNull {

                @Test
                void withRequestIdNull() {
                    when(request.getPlanId())
                            .thenReturn(null);
                    when(serviceInstance.getPlanId())
                            .thenReturn(HAPPY_PLAN_ID);
                    testForTrue();
                }

                @Test
                void withServiceInstanceIdNull() {
                    when(request.getPlanId())
                            .thenReturn(HAPPY_PLAN_ID);
                    when(serviceInstance.getPlanId())
                            .thenReturn(null);
                    testForTrue();
                }

            }

            @Nested
            class withOneIdEmpty {

                @Test
                void withRequestIdEmpty() {
                    when(request.getPlanId())
                            .thenReturn("");
                    when(serviceInstance.getPlanId())
                            .thenReturn(HAPPY_PLAN_ID);
                    testForTrue();
                }

                @Test
                void withServiceInstanceIdEmpty() {
                    when(request.getPlanId())
                            .thenReturn(HAPPY_PLAN_ID);
                    when(serviceInstance.getPlanId())
                            .thenReturn("");
                    testForTrue();
                }

            }

            @Test
            void withRequestIdOtherId() {
                when(request.getPlanId())
                        .thenReturn("Mock");
                when(serviceInstance.getPlanId())
                        .thenReturn(HAPPY_PLAN_ID);
                testForTrue();
            }

        }

        @Test
        void withDifferentContexts() {
            Context secondContext = mock(Context.class);
            when(request.getContext())
                    .thenReturn(context);
            when(serviceInstance.getContext())
                    .thenReturn(secondContext);
            // By default mocked objects only test equality over references, which is ok here
            testForTrue();
        }

        @Test
        void withDifferentParameters() {
            when(request.getParameters())
                    .thenReturn(Collections.emptyMap());
            when(serviceInstance.getParameters())
                    .thenReturn(new HashMap<>() {{
                        put("Key", "Value");
                    }});
            testForTrue();
        }

    }

    @Nested
    class returnsFalse {

        @SuppressWarnings("ConstantConditions")
        @Test
        void withNullServiceInstance() {
            boolean result = ServiceInstanceUtils.isEffectivelyUpdating(null, request);
            assertFalse(result);
        }

        @SuppressWarnings("ConstantConditions")
        @Test
        void withNullRequest() {
            boolean result = ServiceInstanceUtils.isEffectivelyUpdating(serviceInstance, null);
            assertFalse(result);
        }

        @Test
        void withAllValid() {
            when(request.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(serviceInstance.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(request.getPlanId())
                    .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(serviceInstance.getPlanId())
                    .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(request.getContext())
                    .thenReturn(context);
            when(serviceInstance.getContext())
                    .thenReturn(context);
            when(request.getParameters())
                    .thenReturn(new HashMap<>() {{
                        put("Key", "Value");
                    }});
            when(serviceInstance.getParameters())
                    .thenReturn(new HashMap<>() {{
                        put("Key", "Value");
                    }});
            boolean result = ServiceInstanceUtils.isEffectivelyUpdating(serviceInstance, request);
            assertFalse(result);
        }

    }

}
