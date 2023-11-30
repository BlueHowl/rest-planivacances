package be.helmo.planivacances.configuration;

import be.helmo.planivacances.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(2)
public class AuthorizationFilter extends OncePerRequestFilter implements WebMvcConfigurer {

    private final String[] excludedEndpoints = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/token",
            "/api/users/number",
            "/api/users/number/flux",
            "/api/users/admin/message",
            "/api/chat/message",
            "/api/chat/messages",
            "/api/users/country/{variable}"};


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

        if (shouldExclude(request)) {
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

    private boolean shouldExclude(ServletRequest request) {
        String uri = ((javax.servlet.http.HttpServletRequest) request).getRequestURI();

        // Check if the URI matches any excluded pattern
        for (String excludedEndpoint : excludedEndpoints) {//".+" +
            if (uri.startsWith(excludedEndpoint) || uri.matches( excludedEndpoint.replace("{variable}", "[^/]+"))) {
                return true;
            }
        }

        return false;
    }
}
