package org.przybyl.logs.web;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by Piotr Przybył (piotr@przybyl.org) on 2017-05-07.
 */
public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //NOOP
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        //NOOP
    }
}
