package org.przybyl.logs.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by Piotr Przybył (piotr@przybyl.org) on 2017-05-07.
 */
public class MDCUserAndInstanceFilter implements Filter {

    private final static Logger logger = LoggerFactory.getLogger(MDCUserAndInstanceFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //NOOP
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        Optional<String> username = HelloServlet.getUsername((HttpServletRequest) request);

        MDC.put("username", username.orElse("UNKNOWN"));
        MDC.put("instance", System.getProperty("INSTANCE", "UNKNOWN"));
        try {
            chain.doFilter(request, response);
            int status = ((HttpServletResponse) response).getStatus();
            MDC.put("responseCode", "" + status);
            logger.debug("Finished processing.");
        } finally {
            MDC.remove("username");
            MDC.remove("instance");
            MDC.remove("responseCode");
        }

    }

    @Override
    public void destroy() {
        //NOOP
    }
}
