package org.przybyl.logs.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by Piotr Przybył (piotr@przybyl.org).
 */

public class HelloServlet extends HttpServlet {
    private final static Logger logger = LoggerFactory.getLogger(HelloServlet.class);
    private static Random random = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (random.nextInt(10) == 0) {
            logger.error("Oh no, an error occurred!", new RuntimeException("Error code: "+ random.nextInt(10)));
            resp.sendError(500, "An error occurred, please refresh");
        } else {
            handleRequest(req, resp);
        }
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        startResponse(resp);

        Optional<String> optional = getUsername(req);
        if (optional.isPresent()) {
            String greeting = createGreetingFor(optional.get());
            logger.info(greeting);
            resp.getWriter().write(greeting);
        } else {
            logger.debug("Unauthenticated request received.");
            resp.getWriter()
                .write("Hello, Stranger! Why don't you introduce yourself?<br>" + "<form method=\"post\"><input name=\"nick\" type=\"text\"/><input type=\"submit\" value=\"Send\"/></form>");

        }

        finishResponse(resp);
    }

    private String createGreetingFor(String username) {
        String[] greetings = {"Howdy %s", "Siemandero %s!", "Ave %s, morituri te salutant!", "EHLO %s", "Здравствуй %s!", "Salut %s!"};
        return String.format(greetings[random.nextInt(greetings.length)], username);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nick = req.getParameter("nick");
        if (nick != null && !nick.isEmpty() && ! getUsername(req).isPresent()) {
            resp.addCookie(new Cookie("username", nick));
            resp.sendRedirect("/");
        }
    }

    private void startResponse(HttpServletResponse resp) throws IOException {
        resp.getWriter().write("<!DOCTYPE html><html><head><meta name=\"viewport\" content=\"width=device-width, " +
                "initial-scale=1.0\"></head><body>");
    }

    private void finishResponse(HttpServletResponse resp) throws IOException {
        resp.getWriter()
            .write(String.format("<hr><i>This is %s [:%s]</i>",
                    System.getProperty("INSTANCE"),
                    System.getProperty("PORT", "7000")));
        resp.getWriter().write("</body></html>");
    }

    static Optional<String> getUsername(HttpServletRequest req) {
        return Stream.of(req.getCookies()).filter(c -> c.getName().equals("username")).map(Cookie::getValue).findAny();
    }
}
