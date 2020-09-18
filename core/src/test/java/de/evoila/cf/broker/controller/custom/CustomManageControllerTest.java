package de.evoila.cf.broker.controller.custom;

import de.evoila.cf.broker.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.evoila.cf.broker.model.JobProgressResponse;
import de.evoila.cf.broker.model.ResponseMessage;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceUpdateRequest;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.DeploymentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomManageControllerTest {

    private static final String HAPPY_SERVICE_INSTANCE_ID   = "72d68795-3a98-4469-879f-2f4924fa9844";
    private static final String HAPPY_SERVICE_DEFINITION_ID = "cefb9a35-3ff5-4fce-b116-9e62c1b76a1f";
    private static final String HAPPY_PLAN_ID               = "a96cb92a-640f-4425-9728-8bc9ddc2a71b";
    private static final String HAPPY_OPERATION_ID          = "e7434e99-1923-487c-a452-bf2eae131515";

    @Mock
    private ServiceInstanceRepository serviceInstanceRepository;
    @Mock
    private ServiceDefinitionRepository serviceDefinitionRepository;
    @Mock
    private DeploymentService deploymentService;

    @Mock
    private ServiceInstance serviceInstance;
    @Mock
    private Map<String, Object> requestParameters;

    private CustomManageController controller;

    @BeforeEach
    void setUp() {
        controller = new CustomManageController(serviceInstanceRepository,
                                                serviceDefinitionRepository,
                                                deploymentService);
    }

    @Nested
    class getMethod {

        @Nested
        class exceptionThrown {

            @Test
            void withFetchServiceInstanceThrowing() throws ConcurrencyErrorException, ServiceBrokerException, ServiceInstanceNotFoundException {
                Exception[] exceptions = {
                        new UnsupportedOperationException(),
                        new ServiceBrokerException(),
                        new ConcurrencyErrorException(),
                        new ServiceInstanceNotFoundException()
                };
                when(deploymentService.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenThrow(exceptions);
                for (Exception expectedEx : exceptions) {
                    Exception ex = assertThrows(expectedEx.getClass(),
                                                () -> controller.get(HAPPY_SERVICE_INSTANCE_ID));
                    assertSame(expectedEx, ex);
                }
            }

            @Test
            void withServiceInstanceNull() throws ConcurrencyErrorException, ServiceBrokerException, ServiceInstanceNotFoundException {
                ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID);
                when(deploymentService.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(null);
                ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                                       () -> controller.get(HAPPY_SERVICE_INSTANCE_ID));
                assertEquals(expectedEx, ex);
            }

        }

        @Test
        void okResponse() throws ServiceInstanceDoesNotExistException, ConcurrencyErrorException, ServiceBrokerException, ServiceInstanceNotFoundException {
            when(deploymentService.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            ResponseEntity<ServiceInstance> response = controller.get(HAPPY_SERVICE_INSTANCE_ID);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(serviceInstance, response.getBody());
        }

    }

    @Nested
    class submitMethod {

        private ServiceInstanceUpdateRequest updateRequest;

        @BeforeEach
        void setUp() {
            updateRequest = new ServiceInstanceUpdateRequest(HAPPY_SERVICE_DEFINITION_ID,
                                                             HAPPY_PLAN_ID,
                                                             null);
            updateRequest.setParameters(requestParameters);
        }

        @Nested
        class exceptionThrown {

            @Test
            void withGetServiceInstanceThrowing() throws ServiceInstanceDoesNotExistException {
                ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException("Mock");
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenThrow(expectedEx);
                ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                                       () -> controller.submit(HAPPY_SERVICE_INSTANCE_ID,
                                                                                               requestParameters));
                assertSame(expectedEx, ex);
            }

            @Test
            void withServiceInstanceNull() throws ServiceInstanceDoesNotExistException {
                ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException("Could not find Service Instance");
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(null);
                ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                                       () -> controller.submit(HAPPY_SERVICE_INSTANCE_ID,
                                                                                               requestParameters));
                assertEquals(expectedEx, ex);
            }

            @Test
            void withUpdateServiceInstanceThrowing() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionPlanDoesNotExistException {
                Exception[] exceptions = {
                        new ServiceBrokerException(),
                        new ServiceInstanceDoesNotExistException("Mock")
                };
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(serviceInstance);
                when(serviceInstance.getServiceDefinitionId())
                        .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                when(serviceInstance.getPlanId())
                        .thenReturn(HAPPY_PLAN_ID);
                when(deploymentService.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                             updateRequest))
                        .thenThrow(exceptions);
                for (Exception expectedEx : exceptions) {
                    Exception ex = assertThrows(expectedEx.getClass(),
                                                () -> controller.submit(HAPPY_SERVICE_INSTANCE_ID,
                                                                        requestParameters));
                    assertSame(expectedEx, ex);
                }

            }

        }

        @SuppressWarnings("unchecked")
        @Test
        void badRequestResponse() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceDefinitionPlanDoesNotExistException {
            ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException("Mock");
            ResponseMessage<String> expectedResponseMessage = new ResponseMessage<>(expectedEx.getMessage());
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstance.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
            when(serviceInstance.getPlanId())
                    .thenReturn(HAPPY_PLAN_ID);
            when(deploymentService.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                         updateRequest))
                    .thenThrow(expectedEx);
            ResponseEntity<ResponseMessage<String>> response = controller.submit(HAPPY_SERVICE_INSTANCE_ID,
                                                                                 requestParameters);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(expectedResponseMessage, response.getBody());
        }

        @SuppressWarnings("unchecked")
        @Test
        void okResponse() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionPlanDoesNotExistException {
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstance.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
            when(serviceInstance.getPlanId())
                    .thenReturn(HAPPY_PLAN_ID);
            HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
            when(httpServletRequest.getAttribute("javax.servlet.include.context_path"))
                    .thenReturn("CONTEXT");
            when(httpServletRequest.getAttribute("javax.servlet.include.request_uri"))
                    .thenReturn("URI");
            when(httpServletRequest.getAttribute("javax.servlet.include.servlet_path"))
                    .thenReturn("SERVLET");
            ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(httpServletRequest);
            RequestContextHolder.setRequestAttributes(servletRequestAttributes);
            String expectedLocation = MvcUriComponentsBuilder
                                              .fromMethodCall(MvcUriComponentsBuilder.on(CustomManageController.class)
                                                                                     .lastOperation(HAPPY_SERVICE_INSTANCE_ID, null))
                                              .build()
                                              .toUriString();
            HttpHeaders expectedHeaders = new HttpHeaders();
            expectedHeaders.add(HttpHeaders.LOCATION, expectedLocation);
            ResponseEntity<ResponseMessage<String>> response = controller.submit(HAPPY_SERVICE_INSTANCE_ID,
                                                                                 requestParameters);
            verify(deploymentService, times(1))
                    .updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                           updateRequest);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedHeaders, response.getHeaders());
            assertEquals(new ResponseMessage<>("Configuration updated successfully"),
                         response.getBody());
        }

    }

    @Nested
    class lastOperationMethod {

        @Nested
        class exceptionThrown {

            @Test
            void withGetLastOperationByIdThrowing() throws ServiceInstanceDoesNotExistException {
                ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException("Mock");
                when(deploymentService.getLastOperationById(HAPPY_SERVICE_INSTANCE_ID,
                                                            HAPPY_OPERATION_ID))
                        .thenThrow(expectedEx);
                ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                                       () -> controller.lastOperation(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                      HAPPY_OPERATION_ID));
                assertSame(expectedEx, ex);
            }

            @Test
            void withGetLastOperationByReferenceIdThrowing() throws ServiceInstanceDoesNotExistException {
                ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException("Mock");
                when(deploymentService.getLastOperationByReferenceId(HAPPY_SERVICE_INSTANCE_ID))
                        .thenThrow(expectedEx);
                ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                                       () -> controller.lastOperation(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                      null));
                assertSame(expectedEx, ex);
            }

        }

        @Nested
        class okResponse {

            @Mock
            JobProgressResponse jobProgressResponse;

            private void validateResponse(ResponseEntity<JobProgressResponse> response) {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertSame(jobProgressResponse, response.getBody());
            }

            @Test
            void withOperation() throws ServiceInstanceDoesNotExistException {
                when(deploymentService.getLastOperationById(HAPPY_SERVICE_INSTANCE_ID,
                                                            HAPPY_OPERATION_ID))
                        .thenReturn(jobProgressResponse);
                ResponseEntity<JobProgressResponse> response = controller.lastOperation(HAPPY_SERVICE_INSTANCE_ID,
                                                                                        HAPPY_OPERATION_ID);
                validateResponse(response);
            }

            @Test
            void withoutOperation() throws ServiceInstanceDoesNotExistException {
                when(deploymentService.getLastOperationByReferenceId(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(jobProgressResponse);
                ResponseEntity<JobProgressResponse> response = controller.lastOperation(HAPPY_SERVICE_INSTANCE_ID,
                                                                                        null);
                validateResponse(response);
            }

        }

    }

}
