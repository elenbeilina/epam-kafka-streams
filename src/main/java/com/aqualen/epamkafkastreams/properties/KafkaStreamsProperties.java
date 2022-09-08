package com.aqualen.epamkafkastreams.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaStreamsProperties {

  private String sourceTopic1;
  private String sinkTopic1;
  private String sourceTopic2;

}
