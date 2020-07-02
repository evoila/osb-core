package de.evoila.cf.broker.controller.utils;

import de.evoila.cf.broker.model.JobProgressResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class JobProgressUtilsTest {

    static final int HAPPY_RETRY_AFTER = 120;

    private JobProgressUtils utils;
    @Mock
    JobProgressResponse jobProgressResponse;

    @BeforeEach
    void setUp() {
        utils = new JobProgressUtils();
    }

    @Test
    void retryAfterIsZero() {
        ResponseEntity<JobProgressResponse> response = utils.buildJobProgressResponseEntity(jobProgressResponse);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), jobProgressResponse);
    }

    @Test
    void RetryAfterIsLagerThanZero(){
        utils.setRetryAfter(HAPPY_RETRY_AFTER);
        ResponseEntity<JobProgressResponse> response = utils.buildJobProgressResponseEntity(jobProgressResponse);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getHeaders().get("Retry-After")).get(0), Integer.toString(HAPPY_RETRY_AFTER));
        assertEquals(response.getBody(), jobProgressResponse);
    }
}
