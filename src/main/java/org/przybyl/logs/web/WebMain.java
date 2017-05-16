package org.przybyl.logs.web;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import javax.servlet.DispatcherType;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.util.EnumSet;

/**
 * Created by Piotr Przybył (piotr@przybyl.org).
 */
public class WebMain {


    public static final EnumSet<DispatcherType> DISPATCHER_TYPES = EnumSet.of(DispatcherType.REQUEST,
            DispatcherType.FORWARD,
            DispatcherType.INCLUDE,
            DispatcherType.ERROR);

    public static void main(String[] args) throws Exception {

        final org.glassfish.grizzly.http.server.HttpServer server = createServer();

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
        server.start();

        System.out.println("Press Ctrl + C to quit the server");
        System.out.println("Running with PID " + getPid());
    }


    private static HttpServer createServer() {
        URI uri = URI.create("http://0.0.0.0:" + System.getProperty("PORT", "7000"));
        WebappContext context = createWebappContext();

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, false);
        context.deploy(server);

        return server;
    }

    private static WebappContext createWebappContext() {
        WebappContext context = new WebappContext("Context"/*, Configuration.PATH*/);


        context.addFilter(EncodingFilter.class.getName(), EncodingFilter.class)
               .addMappingForUrlPatterns(DISPATCHER_TYPES, "/*");

        context.addFilter("MDCFilter", MDCInsertingServletFilter.class)
               .addMappingForUrlPatterns(DISPATCHER_TYPES, "/*");

        context.addFilter("MDCUserAndInstanceFilter", MDCUserAndInstanceFilter.class)
               .addMappingForUrlPatterns(DISPATCHER_TYPES, "/*");



        context.addServlet(HelloServlet.class.getName(), HelloServlet.class).addMapping("/*");

        return context;
    }

    private static String getPid() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

}

