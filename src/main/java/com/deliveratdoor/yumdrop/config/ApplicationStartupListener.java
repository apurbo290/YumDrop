package com.deliveratdoor.yumdrop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener {

    private static final Logger log = LoggerFactory.getLogger(ApplicationStartupListener.class);

    private final Environment env;

    public ApplicationStartupListener(Environment env) {
        this.env = env;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String port    = env.getProperty("server.port", "8080");
        String profile = String.join(", ", env.getActiveProfiles());
        String appName = env.getProperty("spring.application.name", "YumDrop");

        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("  Application  : {}", appName);
        log.info("  Profile      : {}", profile.isBlank() ? "default" : profile);
        log.info("  Port         : {}", port);
        log.info("  Swagger UI   : http://localhost:{}/swagger-ui/index.html", port);
        log.info("  API Base     : http://localhost:{}/api", port);
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
