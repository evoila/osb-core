package de.evoila.cf.broker.controller.utils;

import de.evoila.cf.broker.controller.core.ServiceInstanceBindingController;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.annotations.ResponseAdvice;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


/** @author Marco Di Martino
 *
 * This class is used to customize the response body of the ServiceInstanceBindingResponse to provide information about
 * the user the request the binding in the json response. The Controller Advice will look for the annotation
 * @ResponseAdvice: if the annotation is present in the method called, the advice will be supported.
 * The information about the user will be fetched from the Originating-Identity header, if provided in the request.
 * */

@ControllerAdvice(assignableTypes = ServiceInstanceBindingController.class)
public class OriginatingHeaderAdvice implements ResponseBodyAdvice<ServiceInstanceBindingResponse> {

    private final Logger log = LoggerFactory.getLogger(OriginatingHeaderAdvice.class);

    private static final String ORIGINATING_IDENTITY_HEADER = "X-Broker-API-Originating-Identity";

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        List<Annotation> annotations = Arrays.asList(returnType.getMethodAnnotations());
        boolean flag = annotations.stream().anyMatch(annotation -> annotation.annotationType().equals(ResponseAdvice.class));
        return flag;
    }

    @Override
    public ServiceInstanceBindingResponse beforeBodyWrite(ServiceInstanceBindingResponse body, MethodParameter returnType, MediaType selectedContentType, Class<? extends
            HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        List<String> header = request.getHeaders().get(ORIGINATING_IDENTITY_HEADER);
        String headerValue;
        if (header != null) {

            headerValue = header.get(0);
            String[] toDecode = headerValue.split(" ");

            log.info("decoding header value " + toDecode[1]);

            byte[] byteValueBase64Decoded = Base64.getDecoder().decode(toDecode[1]);
            String stringValueBase64Decoded = new String(byteValueBase64Decoded);

            String userId;
            JSONObject jsonObj;

            try {
                jsonObj = new JSONObject(stringValueBase64Decoded);
                userId = jsonObj.getString("user_id");
            } catch (Exception E) { throw new RuntimeException("Bad value on Originating-Identity Header."); }

            body.setOriginatingUser(userId);
            return body;
        }
        return body;
    }
}

