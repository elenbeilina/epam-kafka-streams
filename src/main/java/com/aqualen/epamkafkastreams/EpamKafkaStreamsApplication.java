package com.aqualen.epamkafkastreams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@EnableKafkaStreams
@SpringBootApplication
public class EpamKafkaStreamsApplication {

  public static void main(String[] args) {
    SpringApplication.run(EpamKafkaStreamsApplication.class, args);
  }

}
