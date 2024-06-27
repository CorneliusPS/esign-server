package co.id.bcafinance.finalproject.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:kafkaconfig.properties")
public class KafkaProConfig {
    private static String kafkaProHost;
    private static String kafkaProTopics;

    public static String getKafkaProHost() {
        return kafkaProHost;
    }

    @Value("${kafka.pro.host}")
    private void setKafkaProHost(String kafkaProHost) {
        KafkaProConfig.kafkaProHost = kafkaProHost;
    }

    public static String getKafkaProTopics() {
        return kafkaProTopics;
    }

    @Value("${kafka.pro.topics}")
    private void setKafkaProTopics(String kafkaProTopics) {
        KafkaProConfig.kafkaProTopics = kafkaProTopics;
    }
}
