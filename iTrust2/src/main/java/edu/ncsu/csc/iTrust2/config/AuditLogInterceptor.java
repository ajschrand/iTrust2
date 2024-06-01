package edu.ncsu.csc.iTrust2.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Interceptor to log HTTP requests to the API endpoint
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
public class AuditLogInterceptor extends HandlerInterceptorAdapter {

    /** LoggerUtil instance for logging events to the database */
    @Autowired
    private LoggerUtil loggerUtil;

    @Override
    public boolean preHandle ( final HttpServletRequest request, final HttpServletResponse response,
            final Object handler ) throws Exception {
        return true;
    }

    @Override
    public void afterCompletion ( final HttpServletRequest request, final HttpServletResponse response,
            final Object handler, final Exception exception ) throws Exception {
    }

    @Override
    public void postHandle ( final HttpServletRequest request, final HttpServletResponse response, final Object handler,
            final ModelAndView modelAndView ) throws Exception {

        loggerUtil.log( TransactionType.HTTP_REQUEST, request.getRemoteUser() + ":" + request.getRemoteAddr(),
                "Request " + request.getMethod() + " " + request.getRequestURI() + ", response code: "
                        + response.getStatus() );
    }
}
