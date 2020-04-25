package com.streamy.kafka.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IConsumer {
  public void consume(ConsumerRecord<?, ?> cr) throws Exception;
}