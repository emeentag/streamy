// package com.streamy.kafka.consumers;

// import com.streamy.kafka.KafkaProps;

// import org.apache.kafka.clients.consumer.ConsumerRecord;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Component;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Component
// public class PageViewConsumer implements IConsumer {

// @Override
// @KafkaListener(topics = KafkaProps.PageViewTopic)
// public void consume(ConsumerRecord<?, ?> consumerRecord) throws Exception {
// log.info(
// "Topic: " + consumerRecord.topic() + " Key: " + consumerRecord.key() + "
// Value: " + consumerRecord.value());
// }

// }