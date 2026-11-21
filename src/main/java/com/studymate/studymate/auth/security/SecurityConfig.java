package com.studymate.studymate.auth.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//This is your central Spring Security configuration.
// Until now, we created the individual authentication components.
// This class connects them and tells Spring Security how to handle every incoming request.
@Configuration //@Configuration tells Spring that this class contains configuration methods, usually methods that create and register beans.
public class SecurityConfig {

    @Bean //When this application starts, execute this method and register the returned object in the Spring container.
    public PasswordEncoder passwordEncoder(){ //We're returning an object that follows the PasswordEncoder interface.
        return new BCryptPasswordEncoder();
    }
    //BCryptPasswordEncoder → implements password hashing using the BCrypt algorithm.
    //CryptPasswordEncoder implements the PasswordEncoder interface.

    private final JwtAuthenticationFilter jwtAuthenticationFilter; //Checks JWTs on incoming requests
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomUserDetailsService customUserDetailsService){
        this.jwtAuthenticationFilter=jwtAuthenticationFilter;
        this.customUserDetailsService=customUserDetailsService;
    }

    @Bean
    //AuthenticationProvider is a Spring Security interface that defines how authentication is performed.
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder){
        //DaoAuthenticationProvider is an implementation that authenticates users using user details retrieved from a data source, typically a database.
        DaoAuthenticationProvider provider= new DaoAuthenticationProvider(customUserDetailsService); //Tells the provider which service to use to retrieve user information.
        provider.setPasswordEncoder(passwordEncoder); //Tells the provider which password encoder to use when checking the entered password against the stored hash.
        return provider;
    }

    @Bean
    //AuthenticationManager is responsible for coordinating authentication.
    //our future AuthService will need it during login.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    //SecurityFilterChain is the configured chain of security filters that Spring Security uses to process requests.
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception{
        http
                .csrf(csrf-> csrf.disable()) //So disabling CSRF is reasonable for this API design, provided we don't also rely on automatically attached authentication cookies.we're using JWTs sent explicitly in the Authorization header
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //STATELESS tells Spring Security not to create or use an HTTP session to maintain the security context
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(exception-> exception.authenticationEntryPoint((request, response, authException) ->
                        response.sendError( //when an unauthenticated user tries to access a protected resource.401
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized"
                        )))
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers( //Allows anyone to access these endpoints without a JWT.
                                HttpMethod.POST,
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore( //This is how we connect the JWT filter to Spring Security.
                        jwtAuthenticationFilter,
                        //That way, our JWT filter gets the opportunity to authenticate requests using their JWT before later security processing takes place.
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build(); //builds and returns the configured SecurityFilterChain bean.
    }

    @Bean //This disables the servlet-container registration while keeping the filter registered inside Spring Security's filter chain.
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter filter
    ) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);

        return registration;
    }
}
