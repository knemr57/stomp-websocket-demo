package com.afaqy.avl.websocket.config;

import com.afaqy.avl.websocket.model.UnitUpdate;
import com.afaqy.avl.websocket.processor.UnitUpdateProcessor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfig {

    private final UnitUpdateProcessor unitUpdateProcessor;

    @Autowired
    public KafkaStreamsConfig(UnitUpdateProcessor unitUpdateProcessor) {
        this.unitUpdateProcessor = unitUpdateProcessor;
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stomp-websocket-demo");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer configurer() {
        return fb -> fb.setStateListener(
                (newState, oldState) -> log.info("State transition from {} to {}", oldState, newState));
    }

    @Bean
    public KStream<String, UnitUpdate> kStream(StreamsBuilder kStreamBuilder) {

        KStream<String, UnitUpdate> stream = kStreamBuilder.stream("streaming_topic",
                Consumed.with(Serdes.String(), new JsonSerde<>(UnitUpdate.class)));

        stream.process(() -> unitUpdateProcessor);

        stream.print(Printed.toSysOut());

        return stream;
    }

}
