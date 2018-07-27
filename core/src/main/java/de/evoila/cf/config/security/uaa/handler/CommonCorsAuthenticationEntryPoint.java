package de.evoila.cf.config.security.uaa.handler;

import de.evoila.cf.config.security.uaa.utils.DefaultCorsHeader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** @author Johannes Hiemer. */
public class CommonCorsAuthenticationEntryPoint implements AuthenticationEntryPoint,
        InitializingBean {

    private String realmName;

    // ~ Methods
    // ========================================================================================================

    public void afterPropertiesSet() throws Exception {
        Assert.hasText(realmName, "realmName must be specified");
    }

	@Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
						 AuthenticationException exception) throws IOException {

		response.addHeader(DefaultCorsHeader.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
	}

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }
}
