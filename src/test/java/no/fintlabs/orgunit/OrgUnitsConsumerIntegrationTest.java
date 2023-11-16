package no.fintlabs.orgunit;

import no.fintlabs.orgunit.repository.OrgUnit;
import no.fintlabs.orgunit.repository.OrgUnitRepository;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
public class OrgUnitsConsumerIntegrationTest {
    private static String applicationId = "fint-access-management";
    private static String topicOrgId = "testorg";
    private static String topicDomainContext = "testdomain";

    @Container
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @MockBean
    private OrgUnitRepository orgUnitRepositoryMock;

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> kafka.getBootstrapServers());
        registry.add("fint.kafka.topic.org-id", () -> topicOrgId);
        registry.add("fint.kafka.topic.domain-context", () -> topicDomainContext);
        registry.add("fint.kafka.application-id", () -> applicationId);
        registry.add("opa.jsonexport.filename", () -> "");
        registry.add("fint.kontroll.opa.url", () -> "");
    }

    @Test
    public void shouldConsumeOrgUnitsAndSave() {
        OrgUnit orgUnit = OrgUnit.builder()
                .orgUnitId("123")
                .build();

        KafkaTemplate<String, OrgUnit> kafkaTemplate = createKafkaTemplate(kafka.getBootstrapServers());
        kafkaTemplate.send(topicOrgId + "." + topicDomainContext + ".entity.orgunit", "testKey", orgUnit);

        verify(orgUnitRepositoryMock, timeout(5000)).save(ArgumentMatchers.argThat(
                kafkaOrgUnit -> kafkaOrgUnit.getOrgUnitId().equals(orgUnit.getOrgUnitId())
        ));
    }

    private KafkaTemplate<String, OrgUnit> createKafkaTemplate(String bootstrapServers) {
        Map<String, Object> producerConfig = new HashMap<>();
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        ProducerFactory<String, OrgUnit> producerFactory = new DefaultKafkaProducerFactory<>(producerConfig);
        return new KafkaTemplate<>(producerFactory);
    }
}
