package de.evoila.cf.broker.controller.utils;

import de.evoila.cf.broker.model.JobProgressResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class JobProgressUtils {

    private int retryAfter;

    public ResponseEntity<JobProgressResponse> buildJobProgressResponseEntity(JobProgressResponse jobProgressResponse) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.OK);
        if (retryAfter > 0) {
            builder.header("Retry-After", Integer.toString(retryAfter));
        }

        return builder.body(jobProgressResponse);
    }

    @Autowired
    public void setRetryAfter(@Value("${server.retryAfter:0}") int retryAfter) {
        this.retryAfter = retryAfter;
    }
}
