package com.streamy.kafka;

import java.util.concurrent.CountDownLatch;

public class KafkaProps {

  public static final String PageViewTopic = "PageView";

  public static final CountDownLatch Latch = new CountDownLatch(3);

}