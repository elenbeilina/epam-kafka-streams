package com.aqualen.epamkafkastreams.config;

import com.aqualen.epamkafkastreams.dto.CustomSerdes;
import com.aqualen.epamkafkastreams.dto.Employee;
import com.aqualen.epamkafkastreams.properties.KafkaStreamsProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class Task4 {

  private final KafkaStreamsProperties kafkaStreamsProperties;

  @Bean
  KStream<String, Employee> streamTask4(StreamsBuilder builder) {
    KStream<String, Employee> stream = builder
        .stream(kafkaStreamsProperties.getSourceTopic4(),
            Consumed.with(Serdes.String(), CustomSerdes.employeeSerde()))
        .filter((key, value) -> Objects.nonNull(value));
    stream.print(Printed.toSysOut());

    return stream;
  }
}
