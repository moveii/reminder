package at.spengergasse.nvs.server.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * This class automatically send the HTTP Status 401 'UNAUTHORIZED' when the request is not authenticated.
 */

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    /**
     * Automatically sends the HTTP Status 401 'UNAUTHORIZED' when the request is not authenticated.
     *
     * @param request       the request of the client
     * @param response      the response (empty at this point)
     * @param authException the exception (when authentication failed)
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
