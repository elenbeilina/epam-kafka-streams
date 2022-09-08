package com.aqualen.epamkafkastreams.config;

import com.aqualen.epamkafkastreams.properties.KafkaStreamsProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class Task3 {

  private final KafkaStreamsProperties kafkaStreamsProperties;

  @Bean
  String streamTask3(StreamsBuilder builder) {
    Serde<String> serde = Serdes.String();

    KStream<Long, String> stream1 = builder
        .stream(kafkaStreamsProperties.getSourceTopic3dash1(), Consumed.with(serde, serde))
        .filter((key, value) -> StringUtils.contains(value, ":"))
        .map((key, value) -> new KeyValue<>(
            Long.parseLong(StringUtils.substringBefore(value, ":")),
            StringUtils.substringAfter(value, ":")));
    KStream<Long, String> stream2 = builder
        .stream(kafkaStreamsProperties.getSourceTopic3dash2(), Consumed.with(serde, serde))
        .filter((key, value) -> StringUtils.contains(value, ":"))
        .map((key, value) -> new KeyValue<>(
            Long.parseLong(StringUtils.substringBefore(value, ":")),
            StringUtils.substringAfter(value, ":")));

    stream1.print(Printed.toSysOut());
    stream2.print(Printed.toSysOut());

    stream1
        .join(stream2, (value1, value2) -> value1 + " + " + value2,
            JoinWindows.ofTimeDifferenceAndGrace(Duration.ofMinutes(1), Duration.ofSeconds(30)))
        .print(Printed.toSysOut());

    return null;
  }
}
