package com.isetrip.jbotify.analytics;

import com.isetrip.jbotify.data.JBotifyConfiguration;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

@Slf4j
public class AnalyticsManager {

    private final TemplateEngine templateEngine;
    private final HttpServer server;

    public AnalyticsManager(JBotifyConfiguration configuration) throws IOException {
        this.templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");

        this.templateEngine.setTemplateResolver(resolver);

        this.server = HttpServer.create(new InetSocketAddress(configuration.getAnalyticsAddress(), configuration.getAnalyticsPort()), 0);

        createContexts();
        createStyles();
        createImages();

        server.start();

        log.info("Analytics works on http://" + configuration.getAnalyticsAddress() + ":" + configuration.getAnalyticsPort() + "/");
    }

    private void createContexts() {
        createIndex();
    }

    private void createIndex() {
        this.server.createContext("/", httpExchange -> {
            Context context = new Context();

            String response = this.templateEngine.process("index", context);
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        });
    }

    private void createStyles() {
        this.server.createContext("/styles/", httpExchange -> {
            String requestedFile = httpExchange.getRequestURI().getPath().substring("/styles/".length());
            InputStream inputStream = AnalyticsManager.class.getResourceAsStream("/templates/styles/" + requestedFile);

            byte[] bytes = inputStream.readAllBytes();
            httpExchange.getResponseHeaders().add("Content-Type", "text/css");
            httpExchange.sendResponseHeaders(200, bytes.length);

            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(bytes);
            outputStream.close();
        });
    }

    private void createImages() {
        this.server.createContext("/images/", httpExchange -> {
            String requestedFile = httpExchange.getRequestURI().getPath().substring("/images/".length());
            InputStream inputStream = AnalyticsManager.class.getResourceAsStream("/templates/images/" + requestedFile);

            if (inputStream != null) {
                byte[] bytes = inputStream.readAllBytes();
                httpExchange.sendResponseHeaders(200, bytes.length);

                OutputStream outputStream = httpExchange.getResponseBody();
                outputStream.write(bytes);
                outputStream.close();
            } else {
                httpExchange.sendResponseHeaders(404, -1); // Файл не найден
            }
        });

    }


    public void shutdown() {
        this.server.stop(0);
    }
}
