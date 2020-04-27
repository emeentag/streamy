package com.realtimeProcessor.wordCount;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class WordCountController {
  @Autowired
  WordCountService wordCountService;

  @RequestMapping(value = "/wc", method = RequestMethod.GET)
  public Map<String, Long> wordCount(@RequestParam String words) {
    List<String> wordList = Arrays.asList(words.split("\\|"));
    return wordCountService.getCount(wordList);
  }

}