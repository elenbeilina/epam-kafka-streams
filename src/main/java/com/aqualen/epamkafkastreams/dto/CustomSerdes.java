package com.aqualen.epamkafkastreams.dto;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public final class CustomSerdes {

  private CustomSerdes() {
  }

  public static Serde<Employee> employeeSerde() {
    JsonSerializer<Employee> serializer = new JsonSerializer<>();
    JsonDeserializer<Employee> deserializer = new JsonDeserializer<>(Employee.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }

}