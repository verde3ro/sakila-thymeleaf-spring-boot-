package mx.edu.upq.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
public class SecurityConfig {

	@Bean
	public UserDetailsService users() {
		UserDetails user = User.builder()
				.username("user")
				.password(passwordEncoder().encode("54321"))
				.roles("USER")
				.build();

		UserDetails admin = User.builder()
				.username("admin")
				.password(passwordEncoder().encode("12345"))
				.roles("ADMIN")
				.build();

		return new InMemoryUserDetailsManager(user, admin);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filter(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						// Recursos públicos (estáticos, login, favicon)
						.requestMatchers(
								"/login",
								"/css/**",
								"/js/**",
								"/images/**",
								"/favicon.ico"
						).permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/cities", true) // Redirige a /cities tras login exitoso
						.permitAll()
				)
				.logout(logout -> logout
						.logoutUrl("/logout") // El métdo default es post
						.logoutRequestMatcher(request -> "GET".equalsIgnoreCase(request.getMethod()) && "/logout".equals(request.getRequestURI()))
						.logoutSuccessUrl("/login?logout")
						.permitAll()
				)
				.headers(headers -> headers
						.referrerPolicy(ref -> ref
								.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
						)
						// CSP más restrictiva y compatible con Bootstrap CDN
						.contentSecurityPolicy(csp -> csp
								.policyDirectives(
										"default-src 'self' https://cdn.jsdelivr.net; " +
												"script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net; " +
												"style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://fonts.googleapis.com; " +
												"font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net; " +
												"img-src 'self' data: https:; " +
												"object-src 'none'; " +
												"frame-ancestors 'self';"
								)
						)
						.httpStrictTransportSecurity(hsts -> hsts
								.includeSubDomains(true)
								.maxAgeInSeconds(3153600)
						)
						.contentTypeOptions(content -> {})
						.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
						.addHeaderWriter((request, response) ->
								response.setHeader("X-XSS-Protection", "1; mode=block"))
				);

		return http.build();
	}

}
