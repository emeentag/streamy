package com.streamy;

import com.streamy.kafka.producers.PageViewProducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class StreamyApplication {

	// @Autowired
	// PageViewProducer pageViewProducer;

	public static void main(String[] args) {
		SpringApplication.run(StreamyApplication.class, args);
	}

	// @Override
	// public void run(String... args) throws Exception {

	// log.info("Start streaming...");

	// for (int i = 0; i < 5; i++) {
	// this.pageViewProducer.produce("Test " + i, "Value " + i);
	// }
	// }
}
