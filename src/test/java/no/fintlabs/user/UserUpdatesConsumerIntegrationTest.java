package no.fintlabs.user;

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
public class UserUpdatesConsumerIntegrationTest {

    private static String applicationId = "fint-access-management";
    private static String topicOrgId = "testorg";
    private static String topicDomainContext = "testdomain";

    @Container
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @MockBean
    private AccessUserRepository accessUserRepositoryMock;

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> kafka.getBootstrapServers());
        registry.add("fint.kafka.topic.org-id", () -> topicOrgId);
        registry.add("fint.kafka.topic.domain-context", () -> topicDomainContext);
        registry.add("fint.kafka.application-id", () -> applicationId);
        registry.add("opa.jsonexport.filename", () -> "");
    }

    @Test
    public void shouldConsumeAccessUserAndSave() throws InterruptedException {
        AccessUser accessUser = AccessUser.builder()
                .resourceId("123")
                .userId("237")
                .userName("morten.solberg@vigoiks.no")
                .firstName("Morten")
                .lastName("Solberg")
                .build();

        KafkaTemplate<String, AccessUser> kafkaTemplate = createKafkaTemplate(kafka.getBootstrapServers());
        kafkaTemplate.send(topicOrgId + "." + topicDomainContext + ".entity.user", "testKey", accessUser);

        verify(accessUserRepositoryMock, timeout(5000)).save(ArgumentMatchers.argThat(
                user -> user.getResourceId().equals(accessUser.getResourceId())
                        && user.getUserId().equals(accessUser.getUserId())
                        && user.getUserName().equals(accessUser.getUserName())
                        && user.getFirstName().equals(accessUser.getFirstName())
                        && user.getLastName().equals(accessUser.getLastName())
        ));
    }

    private KafkaTemplate<String, AccessUser> createKafkaTemplate(String bootstrapServers) {
        Map<String, Object> producerConfig = new HashMap<>();
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        ProducerFactory<String, AccessUser> producerFactory = new DefaultKafkaProducerFactory<>(producerConfig);
        return new KafkaTemplate<>(producerFactory);
    }
}
