package be.helmo.planivacances.configuration;

import be.helmo.planivacances.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(2)
public class AuthorizationFilter extends OncePerRequestFilter implements WebMvcConfigurer {

    private final List<String> excludedEndpoints = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/token",
            "/api/users/number",
            "/api/users/number/flux",
            "/api/users/admin/message",
            "/api/users/country/**"
            // Add more exclusion patterns as needed
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private AuthService authServices;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if("websocket".equalsIgnoreCase(request.getHeader("upgrade"))) {
            filterChain.doFilter(request,response);
            return;
        }
        // Check if the request URI matches any excluded endpoint pattern
        if (excludedEndpoints.stream().anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()))) {
            // Continue with the filter chain for excluded requests
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        String uid = null;

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            // Authorization header is not present, return 401 Unauthorized
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unauthorized: Token non présent");
            return;
        } else if((uid = authServices.verifyToken(authorizationHeader)) == null) {
            //Authorization header token not valid 401 Unauthorized
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unauthorized: Token invalide");
            return;
        }

        // Continue with the filter chain for all other requests
        request.setAttribute("uid",uid);

        filterChain.doFilter(request, response);
    }
}
