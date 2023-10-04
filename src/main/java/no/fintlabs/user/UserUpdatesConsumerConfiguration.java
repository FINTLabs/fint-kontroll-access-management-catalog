package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.kafka.entity.EntityConsumerFactoryService;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Slf4j
@Configuration
public class UserUpdatesConsumerConfiguration {

    @Autowired
    private AccessUserRepository accessUserRepository;

    @Bean
    public ConcurrentMessageListenerContainer<String, AccessUser> userConsumer(EntityConsumerFactoryService entityConsumerFactoryService
    ) {
        EntityTopicNameParameters entityTopicNameParameters = EntityTopicNameParameters
                .builder()
                .resource("user")
                .build();

        ConcurrentMessageListenerContainer container =
                entityConsumerFactoryService.createFactory(AccessUser.class, (ConsumerRecord<String, AccessUser> consumerRecord) -> {
                            log.info("Got accessuser update, saving: " + consumerRecord.value());
                            accessUserRepository.save(consumerRecord.value());
                        })
                        .createContainer(entityTopicNameParameters);

        return container;
    }
}
