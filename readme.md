### Kafka streams

----
#### Initial tips:
1. All tasks can be done in one Spring Boot application or each task per a new Spring Boot application. It’s preferred to have them all in one Spring Boot application and just split them via .java files in the following way: \
   - .java - for the first task
   - .java - for the second task
   - etc.
2. Topic names are stored in the application.properties file. Initial topics can be created with a similar command (the example is for Windows, for UNIX we use kafka-topics instead of kafka-topics.bat, and it’s assumed you have a local Kafka broker):
   ```
   > kafka-topics.bat -create -bootstrap-server local:9092 -replication-factor 1 -partitions 1 -topic testTopic
   ```
3. Initial Kafka messages can be pushed in the created topic above with this command: 
    ```
   > kafka-console-producer.bat -broker-list localhost:9092 -topic testTopic
   ```
4. Kafka messages can be read from the created topic with the command:
    ```
    > kafka-console-consumer.bat -bootstrap-server localhost:9092 -topic testTopic -from-beginning
    ```
5. The simplest way to create Kafka Streams in Spring Boot, having the annotations: @Configuration, @EnableKafka, @EnableKafkaStreams on your class, is via Beans:
   ```
   @Bean
   public KStream<K, V> myFirstStream(StreamsBuilder kStreamBuilder) {
   return kStreamBuilder.stream...
   }
   ```
6. Terminal operations, e.g., foreach(. . . ), can be performed inside the bean that returns some Kafka Stream:
   ```
   @Bean
   public KStream<K, V> filteredStream(KStream<K, V> originalStream) {
   originalStream.foreach(...);
   return originalStream.filter(...);
   }
   ```
7. Minimum required properties for the Kafka Streams application to implement the tasks:
   ```
   spring.kafka.streams.application-id=my-kafka-streams-application
   spring.kafka.streams.bootstrap-servers=localhost:9092
   server.port=8081
   ```
   Also you might want to have the setting to reduce amount of logs:
   logging.level.root=WARN 
----
#### Practical Tasks:
1. We push any text data to the “task1-1” topic (via a command above) and expect to get the same data in the “task1-2” topic (1 point)
   - Implement functionality to read data from the topic “task1-1” and then transfer all data from the topic to the topic “task1-2”. The read data from the topic “task1-1” gets returned as a Stream.
   - When a Spring Application gets stopped, the application gracefully shutdowns Kafka Streams.
2. We push text data to task2 (via the command above). Every message represents some sentence. We split the sentence into words, then we push the data into two our topics: for long and short words, and after that we merge the topics (1 point)
   - Implement functionality to read data from the topic “task2”
   - Then filter the data by value that the value is not null
   - After it, make several messages out of one if the source message is a sentence (otherwise we have a similar message: same value, but only with a changed key). The key in new messages is their value’s length
   - Then print to the console every message after it all
   - After it, split the current Stream into two with the common name “words-” and additional names “short” (for messages whose value’s length is less than 10 symbols), and “long” (all the rest).
   - Another Bean takes the final result of all actions above (it has to be “Map<String, KStream<Integer, String» . . . ”) and filters out long and short messages by whether they contain the “a” letter or no
   - Finally, the results (two filtered Streams) get merged within a new Bean and every message gets printed to the console \
**Tips**:
   - Use flatMap to create a few messages from one with your new key for messages (in our case it’s their length), so the message >{ key:null, value:“abcd a1234 a0” }
   gets transformed into 3 messages: >{ key:4, value:“abcd” }, { key:5, value: “a1234” }, { key:2, value: “a0” }
   - It doesn’t matter what regex you pick to split sentences into words. We focus on Kafka Streams. The regex can be taken from, e.g, the FlatMap example on the link in the Lectures block
3. Read data from two topics and then JOIN them with each other based on our Long key. Data in two topics is in the format: “number:text”. The number is used as the Key to join messages from two topics. (1 point)
   - Implement functionality to read from both topics task3-1 and task3-2
   - Once the data is read, it’s (data from both topics) filtered out that the value is not null and value contains “:”
   - Then Streams with filtered data get a new key: the Long key. The key is formed from the value: we take the number from the value in the format “number:text” (i.e.: value = “1:hello world text”, so “1” will form the key)
   - After receiving a new key for the Streams, we print every message (print key and value) to the console
   - JOIN (INNER JOIN) the first Stream with the second Stream based on the created keys. Messages have to be joined if and only if a difference between time when they appeared/were_pushed isn’t more than 1 minute. The value of joined messages has to be in
   the following format: > left_value + + right_value
   - In the JOIN add 30 seconds to join records which can be out of order
   - Print to the console every result of JOIN
4. Create a custom SerDe to handle messages with JSON data in the topic “task4” (1 point)
   - Implement a custom SerDe to handle JSON (String) messages (transform from JSON to our entity) in the topic with the following fields: >{“name”:“John”,“company”:“EPAM”,“position”:“developer”,“experience”:5}
   - Filter messages out to make sure that their value is not NULL
   - The read messages get printed to the console

   **Tips**:
   - To implement a custom SerDe to handle JSON data you can take custom Serilizer and Deserializer from here: https://www.baeldung.com/kafka-custom-serializer#dependencies-1 but add type to “. . . implements Serializer. . . ” (it’s a mistake in the article)
   - Then you should use >Serdes.serdeFrom(new YourSerializer(), new YourDeserializer())
   - If you suddenly push a corrupted JSON message in the topic and then try to read with the implemented feature, your application might get stuck at reading the message with a constant error, so add the following property to your application.properties file to
   skip corrupted messages:
   spring.kafka.streams.properties.default.deserialization.exception.handler=org.apache.kafka.streams.errors.LogAndContinueExceptionHandler
5. Write unit tests for the implemented functionality in the 2nd and 4th task (1 point)
   - Write unit tests for the functionality in the 2nd task
   - Write unit tests for the functionality in the 4th task
   **Tips**:
   - An example of how to test a Bean with simple Kafka Stream’s logic: Having:
    ```
   // MyClass.java<br>
   @Bean
   public KStream<String, String> myStream(StreamsBuilder kStreamBuilder) {
   return kStreamBuilder.stream(environment.getRequiredProperty("first.topic.name")...
   }
   ```
   We can write the following test:
    ```
   // MyClassTest.java
   private Properties props;
   @Spy
   @InjectMocks
   private MyClass myClass;
   @Mock
   private Environment environment;
   @Before
   public void init() {
   myClass = new MyClass();
   ...
   props = new Properties();
   props.put(APPLICATION_ID_CONFIG, "my-test-app");
   props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:9092");
   when(environment.getRequiredProperty("first.topic.name")).thenReturn("myTopic");
   }
   @Test
   public void ...() {
   StreamsBuilder builder = new StreamsBuilder();
   MockApiProcessorSupplier<String, String, Void, Void> supplier = new MockApiProcessorSupplier<>();
   myClass.myStream(builder).process(supplier);
   try (TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
   TestInputTopic<String, String> inputTopic = driver.createInputTopic("myTopic", Serdes.String().serializer(), Serdes.String().serializer());
   inputTopic.pipeInput("testKey", "testValue");
   } catch (Exception e) {
   ...
   }
   ArrayList<KeyValueTimestamp<String, String>> resultValues = supplier.capturedProcessors(3).get(2).processed();
   assertEquals(1, resultValues.size());
   assertEquals("testValue", (resultValues.get(0).value()));
   ...
   }
   ```
   - Except mentioned dependencies at the beginning of this text, you should add some typical dependencies for unit tests, e.g., these ones: >org.powermock:powermock-api-mockito2:2.0.9 org.powermock:powermock-module-junit4:2.0.9 org.junit.vintage:junitvintage-engine
   or some other. It depends on what libraries you want to use for unit tests and their versions 
----
#### Optional Tasks:
1. Learn topology and optimize your application (1 bonus point)
   - Print Kafka Streams’ topology and enable Kafka logs to find a number of tasks created with your topology
   - Based on the topology and a number of tasks, set an optimal number of threads for your application
   - Optimize your topology with some built-in option
2. Monitoring Kafka Streams’ application with Prometheus (1 bonus point)
   - Add needed typical dependencies for metrics like: >io.micrometer:micrometer-registry-prometheus org.springframework.boot:spring-boot-starter-actuator
   - Unpack or have installed Prometheus somewhere
   - Make Prometheus to receive Kafka Streams’ metrics with, e.g., similar settings:
     - job_name: 'Kafka Streams'
       metrics_path: '/actuator/prometheus'
       static_configs:
     - targets: ['localhost:8081']
       - Observe various metrics which start with “kafka_consumer_fetch_manager_records. . . ” and find names of metrics which:  
       - Show how big a current message’s lag is 
       - Show max message’s lag 
       - Show how many messages have been processed
       - List those metrics somewhere (in .txt file, for example) \
   **Tip**: To enable metrics you can add, e.g., the following dependencies: >org.springframework.boot:spring-boot-starter-actuator org.springframework.boot:spring-boot-starter-web io.micrometer:micrometer-registry-prometheus javax.xml.bind:jaxb-api
----
#### References
1. Kafka Documentation
2. Kafka Streams With Spring Boot
----
### Testing process

There are several steps for full testing process of Kafka streaming application:
1. Go to `localTest` folder:
    ```
    cd localTest
    ```

2. Run docker environment:
    ```
    docker-compose up -d
    ```

3. Monitor environment via **control-center** util all clusters will be ready:
    ```
    http://localhost:9021/clusters
    ```

4. Specify profile in project configuration to **local**.

5. Push test data to kafka, just change the topic for each of the tasks, by default it is main:

    ```
    sh produce-data.sh
    ```

6. Start application.

7. To see metrics: http://localhost:9000/actuator/metrics
