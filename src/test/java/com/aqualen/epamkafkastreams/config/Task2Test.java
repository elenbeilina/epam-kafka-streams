package com.aqualen.epamkafkastreams.config;

import com.aqualen.epamkafkastreams.properties.KafkaStreamsProperties;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class Task2Test {

  private final Properties streamProps = new Properties();
  @Spy
  private KafkaStreamsProperties kafkaStreamsProperties =
      new KafkaStreamsProperties(null, null, "task2", null, null, null);
  @InjectMocks
  private Task2 task2;
  private TestInputTopic<String, String> inputTopic;
  private TestOutputTopic<Integer, String> outputTopic;

  @BeforeEach
  void setUp() {
    streamProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "epam-kafka-streams");
  }

  @Test
  void testTask2() {
    String message = "Hello world! I am here! Hello all!";
    String expectedResult = "{1=I, 2=am, 4=all!, 5=Hello, 6=world!}";

    StreamsBuilder builder = new StreamsBuilder();
    Map<String, KStream<Integer, String>> lengthenedWordBranches = task2.lengthenedWordBranchesTask2(builder);
    Map<String, KStream<Integer, String>> lengthenedWordBranchesWithA = task2.lengthenedWordBranchesWithATask2(lengthenedWordBranches);
    KStream<Integer, String> mergedLengthenedWordBranchesWithA = task2.mergedLengthenedWordBranchesWithATask2(lengthenedWordBranchesWithA);
    mergedLengthenedWordBranchesWithA.to("result", Produced.with(Serdes.Integer(), Serdes.String()));

    TopologyTestDriver topologyTestDriver = new TopologyTestDriver(builder.build(), streamProps);
    inputTopic =
        topologyTestDriver.createInputTopic("task2", new StringSerializer(),
            new StringSerializer(), Instant.now(), Duration.ZERO);
    outputTopic =
        topologyTestDriver.createOutputTopic("result", new IntegerDeserializer(), new StringDeserializer());

    inputTopic.pipeInput(message);
    Map<Integer, String> result = outputTopic.readKeyValuesToMap();

    assertThat(result).hasToString(expectedResult);
  }
}