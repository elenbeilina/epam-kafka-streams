package com.aqualen.epamkafkastreams.config;

import com.aqualen.epamkafkastreams.properties.KafkaStreamsProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
public class KafkaStreamsConfig {

  private final KafkaStreamsProperties kafkaStreamsProperties;

  @Bean
  KStream<String, String> defineStream(StreamsBuilder builder) {
    Serde<String> serde = Serdes.String();

    KStream<String, String> stream = builder
        .stream(kafkaStreamsProperties.getSourceTopic1(), Consumed.with(serde, serde));
    stream
        .to(kafkaStreamsProperties.getSinkTopic1());

    return stream;
  }

}
