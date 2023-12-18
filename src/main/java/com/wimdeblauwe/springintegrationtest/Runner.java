package com.wimdeblauwe.springintegrationtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    private final SpringIntegrationTestApplicationConfiguration.EmailGateway emailGateway;

    public Runner(SpringIntegrationTestApplicationConfiguration.EmailGateway emailGateway) {
        this.emailGateway = emailGateway;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("Sending 1");
        emailGateway.sendEmail("This is my body", "target");
        LOGGER.info("Sending 2");
        emailGateway.sendEmail("This is my body2", "target2");
    }
}
