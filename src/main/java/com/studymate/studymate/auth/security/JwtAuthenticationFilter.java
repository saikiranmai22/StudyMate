package com.studymate.studymate.auth.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
//OncePerRequestFilter: A Spring Security class that ensures this filter is invoked once per request within a single request dispatch.
//we want our JWT validation logic to execute automatically whenever a request passes through the security filter chain.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService; //Extract email and validate JWT
    private final CustomUserDetailsService customUserDetailsService; //Fetch user details from database

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService){
        this.jwtService=jwtService;
        this.customUserDetailsService=customUserDetailsService;
    }

    @Override //Our filter intercepts this request before it reaches the controller.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        //The frontend sends the JWT in the Authorization HTTP header.
        String authHeader=request.getHeader(HttpHeaders.AUTHORIZATION);//Extract the Authorization header

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
            //if null or has no bearer token format Stops executing the remaining code in our JWT filter.
            //some endpoints, such as registration and login, are public.
            //Spring Security will decide whether authentication is required for the requested endpoint.
        }

        String token=authHeader.substring(7); //Bearer  contains seven characters, including the space-removes them

        try{ //Because an invalid, malformed or expired JWT can cause an exception during parsing.
            // We want to handle those failures instead of allowing them to crash the request processing.

            if(SecurityContextHolder.getContext().getAuthentication()==null){ //SecurityContextHolder: It contains the current user's authentication details, if authentication has already been established.
                //- An Authentication object if a user is already authenticated.
                //- null if no authentication has been established.
                //Why check for null?
                //We don't want to authenticate the same request again if authentication is already present.

                String email=jwtService.extractEmail(token);// Extract email. This also verifies the signature and expiry.
                if (email == null || email.isBlank()) {
                    throw new IllegalArgumentException("JWT subject is missing");
                }

                UserDetails userDetails=customUserDetailsService.loadUserByUsername(email); //Load the user from the database

                //Create an Authentication object to represent that authenticated user.
                //The three-argument constructor represents an authenticated token. The two-argument constructor represents an unauthenticated authentication request.
                UsernamePasswordAuthenticationToken authentication= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                //attaches additional information about the HTTP request to the authentication object.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //Store authentication in SecurityContextHolder
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication); //Puts the authenticated user inside the container
                SecurityContextHolder.setContext(context); //Makes that container available to Spring Security
                //Spring Security can now access the authenticated user's details and authorities during the rest of request processing.
                //a controller can access the current user through Spring Security's authentication information.

                System.out.println("JWT authenticated user: " + email);
            }
        }catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex){
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid or expired JWT");
            return;
        }
        System.out.println("Request: " + request.getMethod() + " " +
                request.getRequestURI());

        System.out.println("Authenticated: " +
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated());

        filterChain.doFilter(request,response);
        //Spring Security authorizes the request and, if permitted, it reaches the controller.
    }

}
