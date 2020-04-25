package com.streamy.kafka.producers;

public interface IProducer {
  public void produce(final String key, final String value) throws Exception;
}