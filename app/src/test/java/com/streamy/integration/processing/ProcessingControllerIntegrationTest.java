package com.streamy.integration.processing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ProcessingControllerIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  public void processFileShouldInitiateProcess() throws Exception {
    // given
    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/process/tets_file"));
    MvcResult result = resultActions.andReturn();

    // when
    String content = result.getResponse().getContentAsString();

    // then
    resultActions.andExpect(MockMvcResultMatchers.status().isOk());

  }
}