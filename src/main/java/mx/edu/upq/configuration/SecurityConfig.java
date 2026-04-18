package mx.edu.upq.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import org.springframework.security.web.header.writers.StaticHeadersWriter;

/**
 * Configuración de Seguridad para Spring Boot 3.5 / Spring Security 6.4+.
 * <p>
 * Define autenticación en memoria, reglas de autorización, política de cabeceras HTTP
 * de seguridad (CSP, HSTS, Permissions-Policy, etc.) y compatibilidad con CDN de terceros.
 */
@Configuration
@EnableMethodSecurity  // Habilita @PreAuthorize en los controladores
public class SecurityConfig {

	/**
	 * Define usuarios en memoria (demo).
	 * - user / 54321  : rol USER  (solo puede listar ciudades y descargar Excel)
	 * - admin / 12345 : rol ADMIN (puede crear, editar y eliminar)
	 */
	@Bean
	public UserDetailsService users() {
		UserDetails user = User.builder()
				.username("user")
				.password(passwordEncoder().encode("54321"))
				.roles("USER")      // Spring añade automáticamente el prefijo "ROLE_"
				.build();

		UserDetails admin = User.builder()
				.username("admin")
				.password(passwordEncoder().encode("12345"))
				.roles("ADMIN")
				.build();

		return new InMemoryUserDetailsManager(user, admin);
	}

	/**
	 * Codificador de contraseñas BCrypt (recomendado).
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Configuración principal de la cadena de filtros de seguridad.
	 */
	@Bean
	public SecurityFilterChain filter(HttpSecurity http) throws Exception {
		http
				// ----- 1. Reglas de autorización (quién accede a qué) -----
				.authorizeHttpRequests(auth -> auth
						// Rutas públicas (no requieren autenticación)
						.requestMatchers(
								"/login",           // Página de login personalizada
								"/css/**",          // Archivos estáticos CSS
								"/js/**",           // Archivos JavaScript locales
								"/images/**",       // Imágenes
								"/favicon.ico",     // Icono del sitio
								"/*.map",           // Source maps de librerías JS
								"/**/*.map",         // (evita que se traten como rutas con parámetros)
								"/.well-known/**" // Devtools, chrome
						).permitAll()
						// Cualquier otra petición requiere autenticación
						.anyRequest().authenticated()
				)
				// ----- 2. Configuración del formulario de login -----
				.formLogin(form -> form
						.loginPage("/login")                // Página de login personalizada
						.defaultSuccessUrl("/cities", true) // Redirige a /cities tras login exitoso
						.permitAll()                        // Permitir acceso a todos a la página de login
				)
				// ----- 3. Configuración de logout (cierre de sesión) -----
				.logout(logout -> logout
						.logoutUrl("/logout")               // URL que procesa el logout
						// Permite cerrar sesión mediante GET (por simplicidad en el enlace de la vista)
						.logoutRequestMatcher(request ->
								"GET".equalsIgnoreCase(request.getMethod()) && "/logout".equals(request.getRequestURI()))
						.logoutSuccessUrl("/login?logout")  // Redirige al login con mensaje
						.permitAll()
				)
				// ----- 4. Cabeceras de seguridad HTTP (la parte más importante) -----
				.headers(headers -> headers
						// 4.1 Referrer-Policy: controla cuánta información de la URL se envía en el header 'Referer'
						.referrerPolicy(ref -> ref
								.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
						)
						// 4.2 Content-Security-Policy (CSP): mecanismo fundamental contra XSS y data injection
						.contentSecurityPolicy(csp -> csp
								.policyDirectives(
										"default-src 'self' https://cdn.jsdelivr.net; " +
												"script-src 'self' 'unsafe-inline' 'unsafe-eval' " +
												"https://cdn.jsdelivr.net " +   // Bootstrap, Thymeleaf inline
												"https://code.jquery.com " +     // jQuery
												"https://cdnjs.cloudflare.com; " + // Bootbox
												"style-src 'self' 'unsafe-inline' " +
												"https://cdn.jsdelivr.net " +   // Bootstrap CSS, Flatpickr CSS
												"https://fonts.googleapis.com " + // Google Fonts
												"https://cdnjs.cloudflare.com; " +
												"font-src 'self' " +
												"https://fonts.gstatic.com " +    // Google Fonts
												"https://cdn.jsdelivr.net; " +    // Bootstrap Icons
												"img-src 'self' data: https:; " +
												"object-src 'none'; " +
												"frame-ancestors 'self';"
								)
						)
						// 4.3 HTTP Strict Transport Security (HSTS): obliga a usar HTTPS
						.httpStrictTransportSecurity(hsts -> hsts
								.includeSubDomains(true)
								.maxAgeInSeconds(3153600)
						)
						// 4.4 X-Content-Type-Options: Se agrega automáticamente por Spring Security
						// 4.5 X-Frame-Options: protección contra clickjacking
						.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
						// 4.6 Permissions-Policy: controla APIs del navegador
						.permissionsPolicyHeader(permissions -> permissions
								.policy("geolocation=(), microphone=(), camera=(), payment=()")
						)
						// 4.7 Cabeceras personalizadas adicionales
						.addHeaderWriter(new StaticHeadersWriter("Cross-Origin-Embedder-Policy", "require-corp"))
						.addHeaderWriter(new StaticHeadersWriter("Cross-Origin-Opener-Policy", "same-origin"))
						.addHeaderWriter(new StaticHeadersWriter("Cross-Origin-Resource-Policy", "same-origin"))
						.addHeaderWriter(new StaticHeadersWriter("X-XSS-Protection", "1; mode=block"))
				);

		return http.build();
	}

}
