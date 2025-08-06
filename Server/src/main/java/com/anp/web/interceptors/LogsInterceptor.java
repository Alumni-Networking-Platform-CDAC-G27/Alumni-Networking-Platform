package com.anp.web.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.anp.services.LoggerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LogsInterceptor implements HandlerInterceptor {
    private final LoggerService loggerService;

    public LogsInterceptor(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null) {
            String method = request.getMethod();

            if ((method.equals("POST") || method.equals("PUT") || method.equals("DELETE"))
                    && !request.getRequestURI().endsWith("error")) {

                String[] uriParts = request.getRequestURI().split("/");
                if (uriParts.length >= 3) {
                    String tableName = uriParts[1];
                    String action = uriParts[2];
                    String principal = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous";

                    loggerService.createLog(method, principal, tableName, action);
                }
            }
        }

        return true;
    }
}
