package be.helmo.planivacances.configuration;

import be.helmo.planivacances.service.AuthService;
import be.helmo.planivacances.service.GroupService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            "/api/users/country/.+"
    };

    private final String[] verifyIsInGroupEndpoints = {
            "/api/activity/calendar/(.+)",
            "/api/activity/(.+)/.+",
            "/api/activity/(.+)",
            "/api/group/invitation/(.+)/.+",
            "/api/group/invitation/(.+)",
            "/api/group/(.+)",
            "/api/place/list/(.+)",
            "/api/place/(.+)/.+",
            "/api/place/(.+)",
    };


    @Autowired
    private AuthService authServices;

    @Autowired
    private GroupService groupServices;

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

        //filter endpoints with non-needed authorization
        if (doRequestContains(request, excludedEndpoints) != null) {
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

        String endpointFound;
        //filter access and define forbidden actions
        if((endpointFound = doRequestContains(request, verifyIsInGroupEndpoints)) != null) {
            if(!request.getRequestURI().contains("/group/list")) {
                Pattern p = Pattern.compile(endpointFound);
                Matcher m = p.matcher(request.getRequestURI());
                String gid = null;
                if(m.matches()) {
                    gid = m.group(1);
                }
                if (!groupServices.isInGroup(uid, gid)) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("Forbidden: L'utilisateur n'a pas accès au groupe");
                    return;
                }
            }
        }

        // Continue with the filter chain for all other requests
        request.setAttribute("uid",uid);

        filterChain.doFilter(request, response);
    }

    /**
     * Check if the request uri is in the list provided
     * @param request (ServletRequest)
     * @param endpoints (String[]) list of endpoints
     * @return (true) if the list contains the uri of the request
     */
    private String doRequestContains(ServletRequest request, String[] endpoints) {
        String uri = ((javax.servlet.http.HttpServletRequest) request).getRequestURI();

        // Check if the URI matches any excluded pattern
        for (String endpoint : endpoints) {//".+" +
            if (uri.equals(endpoint) || uri.matches(endpoint)) {
                return endpoint;
            }
        }

        return null;
    }
}
