package com.aqualen.epamkafkastreams.config;

import com.aqualen.epamkafkastreams.properties.KafkaStreamsProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
public class Task2 {

  private final KafkaStreamsProperties kafkaStreamsProperties;

  @Bean
  Map<String, KStream<Integer, String>> lengthenedWordBranchesTask2(StreamsBuilder builder) {
    Serde<String> serde = Serdes.String();

    KStream<Integer, String> wordsStream = builder
        .stream(kafkaStreamsProperties.getSourceTopic2(), Consumed.with(serde, serde))
        .filter((k, v) -> StringUtils.hasLength(v))
        .flatMap((key, value) -> {
          String[] words = value.split(" ");
          return Arrays.stream(words)
              .map(word -> new KeyValue<>(word.length(), word))
              .collect(Collectors.toSet());
        });

    wordsStream.print(Printed.toSysOut());
    return wordsStream.split(Named.as("words-"))
        .branch((key, value) -> key < 10, Branched.as("short"))
        .defaultBranch(Branched.as("long"));
  }

  @Bean
  Map<String, KStream<Integer, String>> lengthenedWordBranchesWithATask2(
      Map<String, KStream<Integer, String>> lengthenedWordBranchesTask2
  ) {
    lengthenedWordBranchesTask2
        .forEach((length, integerStringKStream) -> integerStringKStream
            .filter((key, value) -> value.contains("a")));

    return lengthenedWordBranchesTask2;
  }

  @Bean
  Map<String, KStream<Integer, String>> mergedLengthenedWordBranchesWithATask2(
      Map<String, KStream<Integer, String>> lengthenedWordBranchesTask2
  ) {
    KStream<Integer, String> longWordsStream = lengthenedWordBranchesTask2.get("words-long");
    KStream<Integer, String> shortWordsStream = lengthenedWordBranchesTask2.get("words-short");

    longWordsStream.merge(shortWordsStream)
        .print(Printed.toSysOut());

    return new HashMap<>();
  }
}
