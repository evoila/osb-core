package de.evoila.cf.broker.controller.custom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.backup.BackupItem;
import de.evoila.cf.broker.service.BackupCustomService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Currently the {@link Pageable} parameter of {@link CustomBackupController#items(String, Pageable)}
 * is unused and therefore we pass null when calling the method.
 */
@ExtendWith(MockitoExtension.class)
class CustomBackupControllerTest {

    private static final String HAPPY_SERVICE_INSTANCE_ID = "64054a32-484d-42d5-9a11-7d7b9490ca7c";

    @Mock
    private BackupCustomService backupCustomService;

    private CustomBackupController controller;

    @BeforeEach
    void setUp() {
        controller = new CustomBackupController(backupCustomService);
    }

    @Nested
    class exceptionThrown {

        @Test
        void withGetItemsThrowing() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
            Exception[] exceptions = {
                    new ServiceInstanceDoesNotExistException("Mock"),
                    new ServiceDefinitionDoesNotExistException("Mock"),
                    new ServiceBrokerException()
            };
            when(backupCustomService.getItems(HAPPY_SERVICE_INSTANCE_ID))
                    .thenThrow(exceptions);
            for (Exception expectedE : exceptions) {
                Exception e = assertThrows(expectedE.getClass(),
                                           () -> controller.items(HAPPY_SERVICE_INSTANCE_ID, null));
                assertSame(expectedE, e);
            }
        }

    }

    @Nested
    class okResponse {

        private void testForOkResponseWithBackupItems(Map<String, String> backupItems) throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
            List<BackupItem> expectedBackupItems = backupItems.entrySet()
                                                              .stream()
                                                              .map(e -> new BackupItem(e.getKey(), e.getValue()))
                                                              .collect(Collectors.toList());
            Page<BackupItem> expectedPage = new PageImpl<>(expectedBackupItems);
            when(backupCustomService.getItems(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(backupItems);
            ResponseEntity<Page<BackupItem>> response = controller.items(HAPPY_SERVICE_INSTANCE_ID, null);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedPage, response.getBody());
        }

        @Test
        void noItems() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
            testForOkResponseWithBackupItems(Collections.emptyMap());
        }

        @Test
        void severalItems() throws ServiceInstanceDoesNotExistException, ServiceBrokerException, ServiceDefinitionDoesNotExistException {
            testForOkResponseWithBackupItems(Map.of("Key1", "Value1",
                                                    "Key2", "Value2",
                                                    "Key3", "Value3"));
        }

    }

}
