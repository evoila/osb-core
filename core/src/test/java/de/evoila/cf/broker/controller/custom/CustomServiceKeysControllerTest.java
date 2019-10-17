package de.evoila.cf.broker.controller.custom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.repository.BindingRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BindingService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomServiceKeysControllerTest {

    private static final String HAPPY_SERVICE_INSTANCE_ID = "2b2312d2-c964-4e3d-9d0c-f8e6543f76fb";

    @Mock
    private BindingRepository bindingRepository;
    @Mock
    private BindingService bindingService;
    @Mock
    private ServiceInstanceRepository serviceInstanceRepository;

    private CustomServiceKeysController controller;

    @BeforeEach
    void setUp() {
        controller = new CustomServiceKeysController(bindingRepository,
                                                     bindingService,
                                                     serviceInstanceRepository);
    }

    @Nested
    class getGeneralInformationMethod {

        private void testAndValidateForBindings(List<ServiceInstanceBinding> bindingList) {
            when(bindingRepository.getBindingsForServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(bindingList);
            Page<ServiceInstanceBinding> expectedPage = new PageImpl<>(bindingList);
            ResponseEntity<Page<ServiceInstanceBinding>> response = controller.getGeneralInformation(HAPPY_SERVICE_INSTANCE_ID);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedPage, response.getBody());
        }

        @Test
        void withEmptyBindingsList() {
            testAndValidateForBindings(new ArrayList<>());
        }

        @Test
        void withNonEmptyBindingsList() {
            testAndValidateForBindings(new ArrayList<>() {{
                add(new ServiceInstanceBinding("ID1",
                                               "INSTANCE1",
                                               new HashMap<>() {{
                                                   put("KEY1", "VALUE1");
                                                   put("KEY2", "VALUE2");
                                               }},
                                               "URL1"));
                add(new ServiceInstanceBinding("ID2",
                                               "INSTANCE2",
                                               new HashMap<>() {{
                                                   put("KEY3", "VALUE3");
                                                   put("KEY4", "VALUE4");
                                               }},
                                               "URL2"));
            }});
        }

    }

    @Nested
    class getServiceKeyMethod {

    }

    @Nested
    class createServiceKeyMethod {

    }

    @Nested
    class deleteMethod {

    }

}
