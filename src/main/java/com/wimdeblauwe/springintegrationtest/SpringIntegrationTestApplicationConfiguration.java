package com.wimdeblauwe.springintegrationtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.jdbc.metadata.JdbcMetadataStore;
import org.springframework.integration.jdbc.store.JdbcChannelMessageStore;
import org.springframework.integration.jdbc.store.channel.MySqlChannelMessageStoreQueryProvider;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.store.ChannelMessageStore;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.Header;

import javax.sql.DataSource;

@Configuration
public class SpringIntegrationTestApplicationConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringIntegrationTestApplicationConfiguration.class);

    public static final String CONCURRENT_METADATA_STORE_PREFIX = "_spring_integration_";

    @MessagingGateway
    public interface EmailGateway {
        @Gateway(requestChannel = "mailbox")
        void sendEmail(String mailBody,
                       @Header String target);
    }

    @Bean
    public JdbcChannelMessageStore messageStore(DataSource dataSource) {
        JdbcChannelMessageStore jdbcChannelMessageStore = new JdbcChannelMessageStore(dataSource);
        jdbcChannelMessageStore.setTablePrefix(CONCURRENT_METADATA_STORE_PREFIX);
        jdbcChannelMessageStore.setChannelMessageStoreQueryProvider(
                new MySqlChannelMessageStoreQueryProvider());
        return jdbcChannelMessageStore;
    }

    @Bean
    ConcurrentMetadataStore concurrentMetadataStore(DataSource dataSource) {
        JdbcMetadataStore jdbcMetadataStore = new JdbcMetadataStore(dataSource);
        jdbcMetadataStore.setTablePrefix(CONCURRENT_METADATA_STORE_PREFIX);
        return jdbcMetadataStore;
    }


    @Bean
    MessageHandler sendEmailMessageHandler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String target = (String) message.getHeaders().get("target");
                LOGGER.info("not sending email with body: {} {}", message, target);
                throw new RuntimeException("");
            }
        };
    }

    @Bean
    QueueChannel mailboxChannel(JdbcChannelMessageStore jdbcChannelMessageStore) {
        return MessageChannels.queue(jdbcChannelMessageStore, "mailbox").getObject();
    }

    @Bean
    public IntegrationFlow buildFlow(ChannelMessageStore channelMessageStore,
                                     MessageHandler sendEmailMessageHandler) {
        return IntegrationFlow.from("mailbox")
                              .routeToRecipients(routes -> {
                                  routes
                                          .transactional()
                                          .recipientFlow(flow -> flow
                                                  .channel(channels -> channels.queue(channelMessageStore, "outbox"))
                                                  .handle(sendEmailMessageHandler, e -> e.poller(poller -> poller.fixedDelay(1000).transactional()))
                                          );
                              }).get();
    }

}
