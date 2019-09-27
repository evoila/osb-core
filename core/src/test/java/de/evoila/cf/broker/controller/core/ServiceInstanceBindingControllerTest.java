package de.evoila.cf.broker.controller.core;

import com.google.gson.Gson;

import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;

import de.evoila.cf.broker.exception.AsyncRequiredException;
import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingBadRequestException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingNotFoundException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingNotRetrievableException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.BaseServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.JobProgressResponse;
import de.evoila.cf.broker.model.ServiceBrokerErrorResponse;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.util.EmptyRestResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceInstanceBindingControllerTest {

    private static final String     HAPPY_INSTANCE_ID           = "e73d39bd-f720-4729-8746-a5e9f87b18c2";
    private static final String     HAPPY_BINDING_ID            = "932240cb-825e-49dc-8593-21cb5dcab7ef";
    private static final String     HAPPY_SERVICE_ID            = "48d3ceef-dbf9-43eb-b53e-e3a394873e17";
    private static final String     HAPPY_PLAN_ID               = "466f8623-2cc5-4f24-9823-e2533272e190";
    private static final String     HAPPY_API_HEADER            = "2.15";
    private static final String     HAPPY_REQUEST_ID = "17e0e6a9-aea6-432c-92dd-280b5bf62dea";
    private static final String     HAPPY_ORIGINATING_ID        = "cloudfoundry eyANCiAgInVzZXJfaWQiOiAiNjgzZWE3NDgtMzA5Mi00ZmY0LWI2NTYtMzljYWNjNGQ1MzYwIg0KfQ==";
    private static final Boolean    HAPPY_ACCEPTS_INCOMPLETE    = true;
    private static final String     HAPPY_OPERATION             = "cbfb4b3f-0653-4877-88ff-51e7dd4d9d23";
    private static final String     HAPPY_SERVICE_DEFINITION_ID = "f9dba371-60e3-4c60-8263-bddf29aa400d";

    private static final Path resourcePath = Path.of(".",
                                                     "src",
                                                     "test",
                                                     "resources",
                                                     "ServiceInstanceBindingController");

    private static final String FILE_EXPECTED_SERVICE_INSTANCE_BINDING = "expectedServiceInstanceBinding.json";

    @Mock
    private BindingServiceImpl bindingService;
    @Mock
    private CatalogService catalogService;

    private ServiceInstanceBindingController controller;

    @BeforeEach
    void setUp() {
        controller = new ServiceInstanceBindingController(bindingService, catalogService);
    }

    @Nested
    class bindServiceInstance {

        @Mock
        private ServiceInstanceBindingRequest request;
        @Mock
        private BaseServiceInstanceBindingResponse bindingResponse;

        @BeforeEach
        void setUp() {
            when(request.getAppGuid()).thenReturn("34423438-7a9a-4600-82a4-8fb829cb770e");
        }

        @Test
        void emptyAppGuid() throws Exception {
            when(request.getAppGuid()).thenReturn("");
            ResponseEntity response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                                     HAPPY_BINDING_ID,
                                                                     HAPPY_API_HEADER,
                                                                     HAPPY_REQUEST_ID,
                                                                     HAPPY_ORIGINATING_ID,
                                                                     HAPPY_ACCEPTS_INCOMPLETE,
                                                                     request);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(EmptyRestResponse.BODY, response.getBody());
        }

        @Test
        void acceptsIncompleteAndApi2_13() {
            assertThrows(ServiceInstanceBindingBadRequestException.class,
                         () -> controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                              HAPPY_BINDING_ID,
                                                              "2.13",
                                                              HAPPY_REQUEST_ID,
                                                              HAPPY_ORIGINATING_ID,
                                                              HAPPY_ACCEPTS_INCOMPLETE,
                                                              request));
        }

        @Test
        void createServiceInstanceBindingThrows() throws Exception {
            Exception[] exceptions = {
                    new ServiceInstanceBindingExistsException("Test1", "Test2"),
                    new ServiceBrokerException(),
                    new ServiceDefinitionDoesNotExistException("Test3"),
                    new ServiceInstanceDoesNotExistException("Test4"),
                    new InvalidParametersException("Test5"),
                    new AsyncRequiredException(),
                    new ValidationException("Test6"),
                    new PlatformException("Test7")
            };
            when(bindingService.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                             HAPPY_INSTANCE_ID,
                                                             request,
                                                             HAPPY_ACCEPTS_INCOMPLETE))
                    .thenThrow(exceptions);
            for (Exception expectedEx : exceptions) {
                Exception ex = assertThrows(expectedEx.getClass(),
                                            () -> controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                                                 HAPPY_BINDING_ID,
                                                                                 HAPPY_API_HEADER,
                                                                                 HAPPY_REQUEST_ID,
                                                                                 HAPPY_ORIGINATING_ID,
                                                                                 HAPPY_ACCEPTS_INCOMPLETE,
                                                                                 request));
                assertSame(expectedEx, ex);
            }
        }

        @SuppressWarnings("unchecked")
        @Nested
        class asyncBindingResponse {

            private ResponseEntity<BaseServiceInstanceBindingResponse> response;

            @BeforeEach
            void setUp() throws Exception {
                when(bindingService.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                 HAPPY_INSTANCE_ID,
                                                                 request,
                                                                 HAPPY_ACCEPTS_INCOMPLETE))
                        .thenReturn(bindingResponse);
                when(bindingResponse.isAsync()).thenReturn(HAPPY_ACCEPTS_INCOMPLETE);
            }

            @AfterEach
            void tearDown() {
                assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
                assertSame(bindingResponse, response.getBody());
            }

            @Test
            void validIdentityHeaders() throws Exception {
                response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                          HAPPY_BINDING_ID,
                                                          HAPPY_API_HEADER,
                                                          HAPPY_REQUEST_ID,
                                                          HAPPY_ORIGINATING_ID,
                                                          HAPPY_ACCEPTS_INCOMPLETE,
                                                          request);
            }

            @Test
            void noIdentityHeaders() throws Exception {
                response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                          HAPPY_BINDING_ID,
                                                          HAPPY_API_HEADER,
                                                          null,
                                                          null,
                                                          HAPPY_ACCEPTS_INCOMPLETE,
                                                          request);
            }

            @Test
            void invalidIdentityHeaders() throws Exception {
                response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                          HAPPY_BINDING_ID,
                                                          HAPPY_API_HEADER,
                                                          HAPPY_ORIGINATING_ID,
                                                          HAPPY_REQUEST_ID,
                                                          HAPPY_ACCEPTS_INCOMPLETE,
                                                          request);
            }

        }

        @SuppressWarnings("unchecked")
        @Nested
        class syncBindingResponse {

            private ResponseEntity<BaseServiceInstanceBindingResponse> response;

            @AfterEach
            void tearDown() {
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertSame(bindingResponse, response.getBody());
            }

            @Test
            void acceptsIncomplete_True() throws Exception {
                when(bindingService.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                 HAPPY_INSTANCE_ID,
                                                                 request,
                                                                 HAPPY_ACCEPTS_INCOMPLETE))
                        .thenReturn(bindingResponse);
                when(bindingResponse.isAsync()).thenReturn(false);
                response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                          HAPPY_BINDING_ID,
                                                          HAPPY_API_HEADER,
                                                          HAPPY_REQUEST_ID,
                                                          HAPPY_ORIGINATING_ID,
                                                          HAPPY_ACCEPTS_INCOMPLETE,
                                                          request);
            }

            @Test
            void acceptsIncomplete_False() throws Exception {
                final boolean async = false;
                when(bindingService.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                 HAPPY_INSTANCE_ID,
                                                                 request,
                                                                 async))
                        .thenReturn(bindingResponse);
                when(bindingResponse.isAsync()).thenReturn(async);
                response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                          HAPPY_BINDING_ID,
                                                          HAPPY_API_HEADER,
                                                          HAPPY_REQUEST_ID,
                                                          HAPPY_ORIGINATING_ID,
                                                          async,
                                                          request);
            }

            @Test
            void acceptsIncomplete_Null() throws Exception {
                final boolean async = false;
                when(bindingService.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                 HAPPY_INSTANCE_ID,
                                                                 request,
                                                                 async))
                        .thenReturn(bindingResponse);
                when(bindingResponse.isAsync()).thenReturn(async);
                response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                          HAPPY_BINDING_ID,
                                                          HAPPY_API_HEADER,
                                                          HAPPY_REQUEST_ID,
                                                          HAPPY_ORIGINATING_ID,
                                                          null,
                                                          request);
            }

        }

    }

    @Nested
    class unbind {

        @Mock
        private ServiceInstanceBindingRequest request;
        @Mock
        private BaseServiceInstanceBindingResponse bindingResponse;

        @Test
        void acceptsIncompleteAndApi2_13() {
            assertThrows(ServiceInstanceBindingBadRequestException.class,
                         () -> controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                              HAPPY_BINDING_ID,
                                                              "2.13",
                                                              HAPPY_REQUEST_ID,
                                                              HAPPY_ORIGINATING_ID,
                                                              HAPPY_ACCEPTS_INCOMPLETE,
                                                              request));
        }


        @Nested
        class deleteServiceInstanceBindingThrows {

            @Test
            void catched() throws Exception {
                Exception[] exceptions = {
                        new ServiceInstanceBindingDoesNotExistsException("Test1"),
                        new ServiceDefinitionDoesNotExistException("Test2")
                };
                when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                 HAPPY_PLAN_ID,
                                                                 HAPPY_ACCEPTS_INCOMPLETE))
                        .thenThrow(exceptions);
                for (Exception ignored : exceptions) {
                    ResponseEntity response = controller.unbind(HAPPY_INSTANCE_ID,
                                                                HAPPY_BINDING_ID,
                                                                HAPPY_SERVICE_ID,
                                                                HAPPY_PLAN_ID,
                                                                HAPPY_ACCEPTS_INCOMPLETE,
                                                                HAPPY_API_HEADER,
                                                                HAPPY_REQUEST_ID,
                                                                HAPPY_ORIGINATING_ID);
                    assertEquals(HttpStatus.GONE, response.getStatusCode());
                    assertEquals(EmptyRestResponse.BODY, response.getBody());
                }
            }

            @Test
            void notCatched() throws Exception {
                Exception[] exceptions = {
                        new AsyncRequiredException()
                };
                when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                 HAPPY_PLAN_ID,
                                                                 HAPPY_ACCEPTS_INCOMPLETE))
                        .thenThrow(exceptions);
                for (Exception expectedEx : exceptions) {
                    Exception ex = assertThrows(expectedEx.getClass(),
                                                () -> controller.unbind(HAPPY_INSTANCE_ID,
                                                                        HAPPY_BINDING_ID,
                                                                        HAPPY_SERVICE_ID,
                                                                        HAPPY_PLAN_ID,
                                                                        HAPPY_ACCEPTS_INCOMPLETE,
                                                                        HAPPY_API_HEADER,
                                                                        HAPPY_REQUEST_ID,
                                                                        HAPPY_ORIGINATING_ID));
                    assertSame(expectedEx, ex);
                }
            }

        }

        @SuppressWarnings("unchecked")
        @Nested
        class asyncBindingResponse {

            private ResponseEntity<BaseServiceInstanceBindingResponse> response;

            @BeforeEach
            void setUp() throws Exception {
                when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                 HAPPY_PLAN_ID,
                                                                 HAPPY_ACCEPTS_INCOMPLETE))
                        .thenReturn(bindingResponse);
                when(bindingResponse.isAsync()).thenReturn(HAPPY_ACCEPTS_INCOMPLETE);
            }

            @AfterEach
            void tearDown() {
                assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
                assertSame(bindingResponse, response.getBody());
            }

            @Test
            void validIdentityHeaders() throws Exception {
                response = controller.unbind(HAPPY_INSTANCE_ID,
                                             HAPPY_BINDING_ID,
                                             HAPPY_SERVICE_ID,
                                             HAPPY_PLAN_ID,
                                             HAPPY_ACCEPTS_INCOMPLETE,
                                             HAPPY_API_HEADER,
                                             HAPPY_REQUEST_ID,
                                             HAPPY_ORIGINATING_ID);
            }

            @Test
            void noIdentityHeaders() throws Exception {
                response = controller.unbind(HAPPY_INSTANCE_ID,
                                             HAPPY_BINDING_ID,
                                             HAPPY_SERVICE_ID,
                                             HAPPY_PLAN_ID,
                                             HAPPY_ACCEPTS_INCOMPLETE,
                                             HAPPY_API_HEADER,
                                             null,
                                             null);
            }

            @Test
            void invalidIdentityHeaders() throws Exception {
                response = controller.unbind(HAPPY_INSTANCE_ID,
                                             HAPPY_BINDING_ID,
                                             HAPPY_SERVICE_ID,
                                             HAPPY_PLAN_ID,
                                             HAPPY_ACCEPTS_INCOMPLETE,
                                             HAPPY_API_HEADER,
                                             HAPPY_ORIGINATING_ID,
                                             HAPPY_REQUEST_ID);
            }

        }

        @Nested
        class syncBindingResponse {

            private ResponseEntity response;

            @AfterEach
            void tearDown() {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(EmptyRestResponse.BODY, response.getBody());
            }

            @Test
            void deleteServiceInstanceBindingReturns_Null() throws Exception {
                when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                 HAPPY_PLAN_ID,
                                                                 HAPPY_ACCEPTS_INCOMPLETE))
                        .thenReturn(null);
                response = controller.unbind(HAPPY_INSTANCE_ID,
                                             HAPPY_BINDING_ID,
                                             HAPPY_SERVICE_ID,
                                             HAPPY_PLAN_ID,
                                             HAPPY_ACCEPTS_INCOMPLETE,
                                             HAPPY_API_HEADER,
                                             HAPPY_REQUEST_ID,
                                             HAPPY_ORIGINATING_ID);
            }

            @Test
            void acceptsIncomplete_True() throws Exception {
                when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                 HAPPY_PLAN_ID,
                                                                 HAPPY_ACCEPTS_INCOMPLETE))
                        .thenReturn(bindingResponse);
                when(bindingResponse.isAsync()).thenReturn(false);
                response = controller.unbind(HAPPY_INSTANCE_ID,
                                             HAPPY_BINDING_ID,
                                             HAPPY_SERVICE_ID,
                                             HAPPY_PLAN_ID,
                                             HAPPY_ACCEPTS_INCOMPLETE,
                                             HAPPY_API_HEADER,
                                             HAPPY_REQUEST_ID,
                                             HAPPY_ORIGINATING_ID);
            }

            @Test
            void acceptsIncomplete_False() throws Exception {
                final boolean async = false;
                when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                 HAPPY_PLAN_ID,
                                                                 async))
                        .thenReturn(bindingResponse);
                when(bindingResponse.isAsync()).thenReturn(async);
                response = controller.unbind(HAPPY_INSTANCE_ID,
                                             HAPPY_BINDING_ID,
                                             HAPPY_SERVICE_ID,
                                             HAPPY_PLAN_ID,
                                             async,
                                             HAPPY_API_HEADER,
                                             HAPPY_REQUEST_ID,
                                             HAPPY_ORIGINATING_ID);
            }

            @Test
            void acceptsIncomplete_Null() throws Exception {
                final boolean async = false;
                when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                 HAPPY_PLAN_ID,
                                                                 async))
                        .thenReturn(bindingResponse);
                when(bindingResponse.isAsync()).thenReturn(async);
                response = controller.unbind(HAPPY_INSTANCE_ID,
                                             HAPPY_BINDING_ID,
                                             HAPPY_SERVICE_ID,
                                             HAPPY_PLAN_ID,
                                             null,
                                             HAPPY_API_HEADER,
                                             HAPPY_REQUEST_ID,
                                             HAPPY_ORIGINATING_ID);
            }

        }

    }

    @Nested
    class lastOperation {

        @Mock
        private JobProgressResponse jobProgressResponse;

        private ResponseEntity<JobProgressResponse> response;

        @Nested
        class jobProgressResponse {

            @AfterEach
            void tearDown() {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertSame(jobProgressResponse, response.getBody());
            }

            @Test
            void withOperation() throws Exception {
                when(bindingService.getLastOperationById(HAPPY_BINDING_ID, HAPPY_OPERATION))
                        .thenReturn(jobProgressResponse);
                response = controller.lastOperation(HAPPY_INSTANCE_ID,
                                                    HAPPY_BINDING_ID,
                                                    HAPPY_SERVICE_ID,
                                                    HAPPY_PLAN_ID,
                                                    HAPPY_REQUEST_ID,
                                                    HAPPY_ORIGINATING_ID,
                                                    HAPPY_OPERATION);
            }

            @Test
            void nullOperation() throws Exception {
                when(bindingService.getLastOperationByReferenceId(HAPPY_BINDING_ID))
                        .thenReturn(jobProgressResponse);
                response = controller.lastOperation(HAPPY_INSTANCE_ID,
                                                    HAPPY_BINDING_ID,
                                                    HAPPY_SERVICE_ID,
                                                    HAPPY_PLAN_ID,
                                                    HAPPY_REQUEST_ID,
                                                    HAPPY_ORIGINATING_ID,
                                                    null);
            }

            @Test
            void noIdentityHeaders() throws Exception {
                when(bindingService.getLastOperationById(HAPPY_BINDING_ID, HAPPY_OPERATION))
                        .thenReturn(jobProgressResponse);
                response = controller.lastOperation(HAPPY_INSTANCE_ID,
                                                    HAPPY_BINDING_ID,
                                                    null,
                                                    null,
                                                    null,
                                                    null,
                                                    HAPPY_OPERATION);
            }

            @Test
            void invalidIdentityHeaders() throws Exception {
                when(bindingService.getLastOperationById(HAPPY_BINDING_ID, HAPPY_OPERATION))
                        .thenReturn(jobProgressResponse);
                response = controller.lastOperation("",
                                                    HAPPY_BINDING_ID,
                                                    "",
                                                    "",
                                                    HAPPY_ORIGINATING_ID,
                                                    HAPPY_REQUEST_ID,
                                                    HAPPY_OPERATION);
            }

        }

        @Nested
        class exceptionThrown {

            @Test
            void getLastOperationById() throws Exception {
                ServiceInstanceBindingDoesNotExistsException expectedEx = new ServiceInstanceBindingDoesNotExistsException("Test");
                when(bindingService.getLastOperationById(HAPPY_BINDING_ID, HAPPY_OPERATION))
                        .thenThrow(expectedEx);
                ServiceInstanceBindingDoesNotExistsException ex = assertThrows(ServiceInstanceBindingDoesNotExistsException.class,
                                                                               () -> controller.lastOperation(HAPPY_INSTANCE_ID,
                                                                                                              HAPPY_BINDING_ID,
                                                                                                              HAPPY_SERVICE_ID,
                                                                                                              HAPPY_PLAN_ID,
                                                                                                              HAPPY_REQUEST_ID,
                                                                                                              HAPPY_ORIGINATING_ID,
                                                                                                              HAPPY_OPERATION));
                assertSame(expectedEx, ex);
            }

            @Test
            void getLastOperationByReferenceId() throws Exception {
                ServiceInstanceBindingDoesNotExistsException expectedEx = new ServiceInstanceBindingDoesNotExistsException("Test");
                when(bindingService.getLastOperationByReferenceId(HAPPY_BINDING_ID))
                        .thenThrow(expectedEx);
                ServiceInstanceBindingDoesNotExistsException ex = assertThrows(ServiceInstanceBindingDoesNotExistsException.class,
                                                                               () -> controller.lastOperation(HAPPY_INSTANCE_ID,
                                                                                                              HAPPY_BINDING_ID,
                                                                                                              HAPPY_SERVICE_ID,
                                                                                                              HAPPY_PLAN_ID,
                                                                                                              HAPPY_REQUEST_ID,
                                                                                                              HAPPY_ORIGINATING_ID,
                                                                                                              null));
                assertSame(expectedEx, ex);
            }

        }

    }

    @Nested
    class fetch {

        @Mock
        private ServiceInstance serviceInstance;
        @Mock
        private ServiceDefinition serviceDefinition;


        @SuppressWarnings({"unchecked", "ConstantConditions"})
        @Test
        void serviceBrokerErrorResponse() throws Exception {
            when(bindingService.getServiceInstance(HAPPY_INSTANCE_ID))
                    .thenThrow(new ServiceInstanceDoesNotExistException("Test"));
            ResponseEntity<ServiceBrokerErrorResponse> response = controller.fetch(HAPPY_INSTANCE_ID,
                                                                                   HAPPY_BINDING_ID,
                                                                                   HAPPY_ORIGINATING_ID,
                                                                                   HAPPY_REQUEST_ID);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("ServiceInstanceNotFound",
                         response.getBody().getError());
            assertEquals("No service instance was found for the given id. This could be caused by a desynchronization between broker and platform.",
                         response.getBody().getDescription());
        }

        @Nested
        class exceptionThrown {

            @Test
            void fetchServiceInstanceBinding() throws Exception {
                when(bindingService.getServiceInstance(HAPPY_INSTANCE_ID))
                        .thenReturn(serviceInstance);
                when(serviceInstance.getServiceDefinitionId())
                        .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                        .thenReturn(serviceDefinition);
                when(serviceDefinition.isBindingsRetrievable())
                        .thenReturn(true);
                ServiceInstanceBindingNotFoundException expectedEx = new ServiceInstanceBindingNotFoundException();
                when(bindingService.fetchServiceInstanceBinding(HAPPY_BINDING_ID, HAPPY_INSTANCE_ID))
                        .thenThrow(expectedEx);
                ServiceInstanceBindingNotFoundException ex = assertThrows(ServiceInstanceBindingNotFoundException.class,
                                                                          () -> controller.fetch(HAPPY_INSTANCE_ID,
                                                                                                 HAPPY_BINDING_ID,
                                                                                                 HAPPY_ORIGINATING_ID,
                                                                                                 HAPPY_REQUEST_ID));
                assertSame(expectedEx, ex);

            }

            @Test
            void serviceInstanceBindingNotRetrievableException() throws Exception {
                when(bindingService.getServiceInstance(HAPPY_INSTANCE_ID))
                        .thenReturn(serviceInstance);
                when(serviceInstance.getServiceDefinitionId())
                        .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                        .thenReturn(serviceDefinition);
                when(serviceDefinition.isBindingsRetrievable())
                        .thenReturn(false);
                ServiceInstanceBindingNotRetrievableException ex = assertThrows(ServiceInstanceBindingNotRetrievableException.class,
                                                                                () -> controller.fetch(HAPPY_INSTANCE_ID,
                                                                                                       HAPPY_BINDING_ID,
                                                                                                       HAPPY_ORIGINATING_ID,
                                                                                                       HAPPY_REQUEST_ID));
                assertEquals("The Service Binding could not be retrievable. You should not attempt to call this endpoint",
                             ex.getMessage());
            }

        }

        @SuppressWarnings("unchecked")
        @Nested
        class serviceInstanceBindingResponse {

            private ServiceInstanceBindingResponse                  bindingResponse;
            private ResponseEntity<ServiceInstanceBindingResponse>  response;

            @BeforeEach
            void setUp() throws Exception {
                String json = Files.readString(resourcePath.resolve(FILE_EXPECTED_SERVICE_INSTANCE_BINDING));
                ServiceInstanceBinding binding = new Gson().fromJson(json, ServiceInstanceBinding.class);
                bindingResponse = new ServiceInstanceBindingResponse(binding);
                when(bindingService.getServiceInstance(HAPPY_INSTANCE_ID))
                        .thenReturn(serviceInstance);
                when(serviceInstance.getServiceDefinitionId())
                        .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                        .thenReturn(serviceDefinition);
                when(serviceDefinition.isBindingsRetrievable())
                        .thenReturn(true);
                when(bindingService.fetchServiceInstanceBinding(HAPPY_BINDING_ID, HAPPY_INSTANCE_ID))
                        .thenReturn(binding);
            }

            @AfterEach
            void tearDown() {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(bindingResponse, response.getBody());
            }

            @Test
            void validIdentityHeaders() throws Exception {
                response = controller.fetch(HAPPY_INSTANCE_ID,
                                            HAPPY_BINDING_ID,
                                            HAPPY_ORIGINATING_ID,
                                            HAPPY_REQUEST_ID);
            }

            @Test
            void noIdentityHeaders() throws Exception {
                response = controller.fetch(HAPPY_INSTANCE_ID,
                                            HAPPY_BINDING_ID,
                                            null,
                                            null);
            }

            @Test
            void invalidIdentityHeaders() throws Exception {
                response = controller.fetch(HAPPY_INSTANCE_ID,
                                            HAPPY_BINDING_ID,
                                            HAPPY_REQUEST_ID,
                                            HAPPY_ORIGINATING_ID);
            }

        }

    }

}
