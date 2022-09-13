package com.aqualen.epamkafkastreams.config;

import com.aqualen.epamkafkastreams.dto.CustomSerdes;
import com.aqualen.epamkafkastreams.dto.Employee;
import com.aqualen.epamkafkastreams.properties.KafkaStreamsProperties;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.test.TestRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class Task4Test {

  private final Properties streamProps = new Properties();
  @Spy
  private KafkaStreamsProperties kafkaStreamsProperties =
      new KafkaStreamsProperties(null, null, null, null, null, "task4");
  @InjectMocks
  private Task4 task4;
  private TestInputTopic<String, String> inputTopic;
  private TestOutputTopic<Integer, Employee> outputTopic;

  @BeforeEach
  void setUp() {
    streamProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "epam-kafka-streams");
  }

  @Test
  void streamTask4() {
    String message = "{\"name\":\"John\",\"company\":\"EPAM\",\"position\":\"junior developer\",\"experience\":2}";
    String expectedResult = new Employee("John", "EPAM", "junior developer", 2).toString();

    StreamsBuilder builder = new StreamsBuilder();
    KStream<String, Employee> stringEmployeeKStream = task4.streamTask4(builder);
    stringEmployeeKStream.to("result", Produced.with(Serdes.String(), CustomSerdes.employeeSerde()));

    TopologyTestDriver topologyTestDriver = new TopologyTestDriver(builder.build(), streamProps);
    inputTopic =
        topologyTestDriver.createInputTopic("task4", new StringSerializer(),
            new StringSerializer(), Instant.now(), Duration.ZERO);
    outputTopic =
        topologyTestDriver.createOutputTopic("result", new IntegerDeserializer(), CustomSerdes.employeeSerde().deserializer());

    inputTopic.pipeInput(message);
    TestRecord<Integer, Employee> result = outputTopic.readRecord();

    assertThat(result.value()).hasToString(expectedResult);
  }
}