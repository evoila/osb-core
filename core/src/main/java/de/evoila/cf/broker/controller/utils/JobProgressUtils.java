package de.evoila.cf.broker.controller.utils;

import de.evoila.cf.broker.model.JobProgressResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class JobProgressUtils {

    @Value("${server.retry-after}")
    private Integer retryAfter;

    public ResponseEntity<JobProgressResponse> buildJobProgressResponseEntity(JobProgressResponse jobProgressResponse) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.OK);
        if (retryAfter != null) {
            builder.header("Retry-After", Integer.toString(retryAfter));
        }

        return builder.body(jobProgressResponse);
    }
}
