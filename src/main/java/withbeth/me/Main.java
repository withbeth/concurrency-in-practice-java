package withbeth.me;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import withbeth.me.ch2.threadsafe.StatelessFactorizer;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ServletWebServerFactory serverFactory = new TomcatServletWebServerFactory();
        WebServer webServer = serverFactory.getWebServer(servletContext -> {
            servletContext
                    .addServlet("myFactorServlet", new StatelessFactorizer())
                    .addMapping("/factor");
        });
        webServer.start();
    }
}