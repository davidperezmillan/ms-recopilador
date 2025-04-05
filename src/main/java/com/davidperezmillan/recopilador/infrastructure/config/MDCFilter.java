package com.davidperezmillan.recopilador.infrastructure.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class MDCFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // Asigna un identificador único para la petición
            MDC.put("requestId", UUID.randomUUID().toString());
            // Puedes extraer información adicional, por ejemplo, el IP del cliente:
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            MDC.put("clientIP", httpRequest.getRemoteAddr());
            MDC.put("applicationName", "recopilador");
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
