package com.streamy.kafka.producers;

import com.streamy.kafka.KafkaProps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * PageViewProducer
 */
@Slf4j
@Component
public class PageViewProducer implements IProducer {

  @Autowired
  KafkaTemplate<String, String> template;

  @Override
  public void produce(final String key, final String value) throws Exception {
    this.template.send(KafkaProps.PageViewTopic, key, value);
    log.info("Message is sent.");
  }
}