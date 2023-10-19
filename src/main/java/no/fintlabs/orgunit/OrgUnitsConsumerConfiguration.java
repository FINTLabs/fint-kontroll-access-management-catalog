package no.fintlabs.orgunit;

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
public class OrgUnitsConsumerConfiguration {

    @Autowired
    private OrgUnitRepository orgUnitRepository;

    @Bean
    public ConcurrentMessageListenerContainer<String, OrgUnit> orgUnitConsumer(EntityConsumerFactoryService entityConsumerFactoryService
    ) {
        EntityTopicNameParameters entityTopicNameParameters = EntityTopicNameParameters
                .builder()
                .resource("orgunit")
                .build();

        return entityConsumerFactoryService.createFactory(OrgUnit.class, (ConsumerRecord<String, OrgUnit> consumerRecord) -> {
                    log.info("Got orgunit update, saving: " + consumerRecord.value());
                    orgUnitRepository.save(consumerRecord.value());
                })
                .createContainer(entityTopicNameParameters);
    }
}
