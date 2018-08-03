package de.evoila.cf.broker;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * @author Johannes Hiemer.
 */
@Rollback
public abstract class BaseApiTest extends SpectrumMockMvcTest {

    protected static final String DEFAULT_MSG_ERROR_KEY = "$._embedded.objectErrors.*.defaultMessage";

    @Value("${api.url}")
    protected String apiUrl;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    public BaseApiTest() {
    }

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    public ResultActions sendPostRequest(String url, String content) throws Exception {
        return mockMvc.perform(post(getUrl(url))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));
    }

    public ResultActions sendPutRequest(String url, String content) throws Exception {
        return mockMvc.perform(put(getUrl(url))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));
    }

    public ResultActions sendAuthorizedPutRequest(String url, String content, String email) throws Exception {
        return mockMvc.perform(put(getUrl(url))
                .header(AUTHORIZATION_HEADER, authorizationHeaderFor(email))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));
    }

    public ResultActions sendGetRequest(String url) throws Exception {
        return mockMvc.perform(get(getUrl(url))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    public String getUrl(String url) {
        return apiUrl + url;
    }

    public String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    public static String extractJsonPathValue(ResultActions result, String path) throws UnsupportedEncodingException {
        String content = result.andReturn().getResponse().getContentAsString();
        Configuration configuration = Configuration.defaultConfiguration().setOptions(Option.ALWAYS_RETURN_LIST);
        List<String> rs = JsonPath.using(configuration).parse(content).read(path);
        Assert.assertEquals(1, rs.size());

        return rs.get(0);
    }

    public static Long extractID(ResultActions resultActions, String path)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder(extractJsonPathValue(resultActions, path));
        return Long.parseLong(result.substring(result.lastIndexOf("/") + 1, result.length()));
    }

}
