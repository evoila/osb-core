package de.evoila.cf.config.security;

import de.evoila.cf.broker.bean.AuthenticationPropertiesConfiguration;
import de.evoila.cf.config.security.uaa.UaaRelyingPartyFilter;
import de.evoila.cf.config.security.uaa.handler.CommonCorsAuthenticationEntryPoint;
import de.evoila.cf.config.security.uaa.handler.UaaRelyingPartyAuthenticationFailureHandler;
import de.evoila.cf.config.security.uaa.handler.UaaRelyingPartyAuthenticationSuccessHandler;
import de.evoila.cf.config.security.uaa.provider.UaaRelyingPartyAuthenticationProvider;
import de.evoila.cf.config.security.uaa.utils.Endpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * @author Johannes Hiemer.
 * @author Marco Di Martino.
 *
 */
@Configuration
@EnableWebSecurity
@Order(1)
public class CustomSecurityConfiguration extends WebSecurityConfigurerAdapter  {

	@Autowired
	private AuthenticationPropertiesConfiguration authentication;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
				.inMemoryAuthentication()
				.withUser(authentication.getUsername())
				.password(authentication.getPassword())
				.roles(authentication.getRole(), "ACTUATOR");
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		UaaRelyingPartyFilter uaaRelyingPartyFilter = new UaaRelyingPartyFilter(authenticationManager());
		uaaRelyingPartyFilter.setSuccessHandler(new UaaRelyingPartyAuthenticationSuccessHandler());
		uaaRelyingPartyFilter.setFailureHandler(new UaaRelyingPartyAuthenticationFailureHandler());

		http.addFilterBefore(uaaRelyingPartyFilter, LogoutFilter.class)
				.authorizeRequests()
				.antMatchers(HttpMethod.GET, Endpoints.V2_ENDPOINT).authenticated()
				.antMatchers(HttpMethod.GET, Endpoints.V2_CATALOG).authenticated()
				.antMatchers(HttpMethod.GET, Endpoints.V2_CATALOG_EXT).authenticated()
				.antMatchers(Endpoints.V2_SERVICE_INSTANCES).authenticated()
				.antMatchers(HttpMethod.GET, Endpoints.INFO).authenticated()
				.antMatchers(HttpMethod.GET, Endpoints.HEALTH).authenticated()
				.antMatchers(HttpMethod.GET, Endpoints._ERROR).authenticated()

				.antMatchers(HttpMethod.GET, Endpoints.ENV).authenticated()
				.antMatchers(HttpMethod.GET, Endpoints.V2_DASHBOARD_SID).permitAll()
				.antMatchers(HttpMethod.GET, Endpoints.V2_DASHBOARD_SID_CONFIRM).permitAll()
				.antMatchers(Endpoints.V2_BACKUP+"/**").permitAll()
				.antMatchers(Endpoints.V2_DASHBOARD_MANAGE+"/**").authenticated()
				.and()
				.authorizeRequests()
				.antMatchers(HttpMethod.GET,"/v2/authentication/{serviceInstanceId}").permitAll()
				.antMatchers(HttpMethod.GET,"/v2/authentication/{serviceInstanceId}/confirm").permitAll()
				.antMatchers(HttpMethod.GET, "/v2/manage/**").authenticated()
				.antMatchers(HttpMethod.GET, "/v2/extensions").authenticated()
				.and()
				.httpBasic()
				.and()
				.anonymous().disable()
				.exceptionHandling()
				.authenticationEntryPoint(new org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint("Authorization"))
				.and()
				.csrf().disable();
	}


	@Configuration
	@Order(10)
	public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

		@Bean
		public UaaRelyingPartyAuthenticationProvider openIDRelyingPartyAuthenticationProvider() {
			return new UaaRelyingPartyAuthenticationProvider();
		}

		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder)
				throws Exception {
			authenticationManagerBuilder
					.authenticationProvider(openIDRelyingPartyAuthenticationProvider());
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			UaaRelyingPartyFilter uaaRelyingPartyFilter = new UaaRelyingPartyFilter(authenticationManager());
			uaaRelyingPartyFilter.setSuccessHandler(new UaaRelyingPartyAuthenticationSuccessHandler());
			uaaRelyingPartyFilter.setFailureHandler(new UaaRelyingPartyAuthenticationFailureHandler());


			http.addFilterBefore(uaaRelyingPartyFilter, LogoutFilter.class)


					.csrf().disable()

					.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

					.and()

					.exceptionHandling()
					.authenticationEntryPoint(new CommonCorsAuthenticationEntryPoint())

					.and()

					.authorizeRequests()
					.antMatchers(HttpMethod.GET,"/v2/authentication/{serviceInstanceId}").permitAll()
					.antMatchers(HttpMethod.GET,"/v2/authentication/{serviceInstanceId}/confirm").permitAll()
					.antMatchers(HttpMethod.GET, "/v2/manage/**").authenticated()
					.antMatchers(HttpMethod.GET, "/v2/extensions").authenticated()
					.and()
					.anonymous().disable()
					.exceptionHandling()
					.authenticationEntryPoint(new org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint("headerValue"));
		}
	}
}
