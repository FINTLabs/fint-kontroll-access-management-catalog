package no.fintlabs.user.kafka;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.kafka.entity.EntityConsumerFactoryService;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
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
    public ConcurrentMessageListenerContainer<String, AccessUserKafka> userConsumer(EntityConsumerFactoryService entityConsumerFactoryService
    ) {
        EntityTopicNameParameters entityTopicNameParameters = EntityTopicNameParameters
                .builder()
                .resource("kontrolluser")
                .build();

        return entityConsumerFactoryService.createFactory(AccessUserKafka.class, (ConsumerRecord<String, AccessUserKafka> consumerRecord) -> {
                    log.info("UserConsumer: Got accessuser update, saving: " + consumerRecord.value());

                    AccessUserKafka accessUserKafka = consumerRecord.value();
                    AccessUser accessUser = AccessUserKafkaMapper.toAccessUser(accessUserKafka);
                    accessUserRepository.save(accessUser);
                })
                .createContainer(entityTopicNameParameters);
    }
}
