package com.aqualen.epamkafkastreams.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "kafka")
public class KafkaStreamsProperties {

  private String sourceTopic1;
  private String sinkTopic1;
  private String sourceTopic2;
  private String sourceTopic3dash1;
  private String sourceTopic3dash2;
  private String sourceTopic4;

}
